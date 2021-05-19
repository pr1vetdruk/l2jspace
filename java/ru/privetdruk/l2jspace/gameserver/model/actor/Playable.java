package ru.privetdruk.l2jspace.gameserver.model.actor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.TeamSetting;
import ru.privetdruk.l2jspace.gameserver.data.SkillTable.FrequentSkill;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManager;
import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectFlag;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.attack.PlayableAttack;
import ru.privetdruk.l2jspace.gameserver.model.actor.cast.PlayableCast;
import ru.privetdruk.l2jspace.gameserver.model.actor.container.npc.AggroInfo;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.SiegeGuard;
import ru.privetdruk.l2jspace.gameserver.model.actor.status.PlayableStatus;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.CreatureTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Duel.DuelState;
import ru.privetdruk.l2jspace.gameserver.model.entity.Siege;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.EtcItem;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ExUseSharedGroupItem;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.Revive;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * This class represents all {@link Playable} actors in the world : {@link Player}s and their different {@link Summon} types.
 */
public abstract class Playable extends Creature {
    private final Map<Integer, Long> _disabledItems = new ConcurrentHashMap<>();

    public Playable(int objectId, CreatureTemplate template) {
        super(objectId, template);
    }

    /**
     * @return The max weight that the {@link Playable} can carry.
     */
    public abstract int getWeightLimit();

    public abstract int getKarma();

    public abstract byte getPvpFlag();

    @Override
    public PlayableStatus<? extends Playable> getStatus() {
        return (PlayableStatus<?>) _status;
    }

    @Override
    public void setStatus() {
        _status = new PlayableStatus<>(this);
    }

    @Override
    public void setCast() {
        _cast = new PlayableCast<>(this);
    }

    @Override
    public void setAttack() {
        _attack = new PlayableAttack<>(this);
    }

    @Override
    public boolean doDie(Creature killer) {
        // killing is only possible one time
        synchronized (this) {
            if (isDead()) {
                return false;
            }

            // now reset currentHp to zero
            getStatus().setHp(0);

            setIsDead(true);
        }

        // Stop movement, cast and attack. Reset the target.
        abortAll(true);

        // Stop HP/MP/CP Regeneration task
        getStatus().stopHpMpRegeneration();

        Player playerKiller = Optional.ofNullable(killer).map(WorldObject::getActingPlayer).orElse(null);
        Player player;

        if (this instanceof Player) {
            player = (Player) this;
        } else {
            player = ((Summon) this).getOwner();
        }

        boolean noblesseBlessed = isNoblesseBlessed();

        if (player.isEventPlayer()) {
            EventEngine event = EventEngine.findActive();

            if (event.isRemoveBuffsOnDie()) {
                stopAllEffectsExceptThoseThatLastThroughDeath();
            }

            if (this instanceof Player) {
                event.revive(player, playerKiller);
            }
        }
        // Stop all active skills effects in progress
        // Same thing if the Character isn't a Noblesse Blessed L2Playable
        else if (isPhoenixBlessed() || noblesseBlessed) {
            // remove Lucky Charm if player has SoulOfThePhoenix/Salvation/NoblessBlessing buff
            if (getCharmOfLuck()) {
                stopCharmOfLuck(null);
            }

            if (noblesseBlessed) {
                stopNoblesseBlessing(null);
            }
        } else {
            stopAllEffectsExceptThoseThatLastThroughDeath();
        }

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        getStatus().broadcastStatusUpdate();

        // Notify Creature AI
        getAI().notifyEvent(AiEventType.DEAD, null, null);

        // Notify Quest of Playable's death
        Player actingPlayer = getActingPlayer();
        actingPlayer.getQuestList().getQuests(Quest::isTriggeredOnDeath).forEach(q -> q.notifyDeath((killer == null ? this : killer), actingPlayer));

        if (playerKiller != null) {
            playerKiller.onKillUpdatePvPKarma(this);
        }

        return true;
    }

    @Override
    public void doRevive() {
        if (!isDead() || isTeleporting())
            return;

        setIsDead(false);

        if (isPhoenixBlessed()) {
            stopPhoenixBlessing(null);

            getStatus().setMaxHpMp();
        } else
            getStatus().setHp(getStatus().getMaxHp() * Config.RESPAWN_RESTORE_HP);

        // Start broadcast status
        broadcastPacket(new Revive(this));
    }

    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || getStatus().getMoveSpeed() == 0;
    }

    public boolean checkIfPvP(Playable target) {
        if (target == null || target == this)
            return false;

        final Player player = getActingPlayer();
        if (player == null || player.getKarma() != 0)
            return false;

        final Player targetPlayer = target.getActingPlayer();
        if (targetPlayer == null || targetPlayer == this)
            return false;

        if (targetPlayer.getKarma() != 0 || targetPlayer.getPvpFlag() == 0)
            return false;

        return true;
    }

    /**
     * Send a {@link SystemMessage} packet using a {@link SystemMessageId} to the {@link Player} associated to this {@link Playable}.
     *
     * @param id : The {@link SystemMessageId} to send.
     */
    public void sendPacket(SystemMessageId id) {
    }

    public final boolean isNoblesseBlessed() {
        return _effects.isAffected(EffectFlag.NOBLESS_BLESSING);
    }

    public final void stopNoblesseBlessing(AbstractEffect effect) {
        if (effect == null)
            stopEffects(EffectType.NOBLESSE_BLESSING);
        else
            removeEffect(effect);
        updateAbnormalEffect();
    }

    public final boolean isPhoenixBlessed() {
        return _effects.isAffected(EffectFlag.PHOENIX_BLESSING);
    }

    public final void stopPhoenixBlessing(AbstractEffect effect) {
        if (effect == null)
            stopEffects(EffectType.PHOENIX_BLESSING);
        else
            removeEffect(effect);

        updateAbnormalEffect();
    }

    public boolean isSilentMoving() {
        return _effects.isAffected(EffectFlag.SILENT_MOVE);
    }

    public final boolean getProtectionBlessing() {
        return _effects.isAffected(EffectFlag.PROTECTION_BLESSING);
    }

    public void stopProtectionBlessing(AbstractEffect effect) {
        if (effect == null)
            stopEffects(EffectType.PROTECTION_BLESSING);
        else
            removeEffect(effect);

        updateAbnormalEffect();
    }

    public final boolean getCharmOfLuck() {
        return _effects.isAffected(EffectFlag.CHARM_OF_LUCK);
    }

    public final void stopCharmOfLuck(AbstractEffect effect) {
        if (effect == null)
            stopEffects(EffectType.CHARM_OF_LUCK);
        else
            removeEffect(effect);

        updateAbnormalEffect();
    }

    @Override
    public void updateEffectIcons(boolean partyOnly) {
        _effects.updateEffectIcons(partyOnly);
    }

    /**
     * This method allows to easily send relations. Overridden in L2Summon and Player.
     */
    public void broadcastRelationsChanges() {
    }

    @Override
    public boolean isInArena() {
        return isInsideZone(ZoneId.PVP) && !isInsideZone(ZoneId.SIEGE);
    }

    public void addItemSkillTimeStamp(L2Skill itemSkill, ItemInstance itemInstance) {
        final EtcItem etcItem = itemInstance.getEtcItem();
        final int reuseDelay = Math.max(itemSkill.getReuseDelay(), etcItem.getReuseDelay());

        addTimeStamp(itemSkill, reuseDelay);
        if (reuseDelay != 0)
            disableSkill(itemSkill, reuseDelay);

        final int group = etcItem.getSharedReuseGroup();
        if (group >= 0)
            sendPacket(new ExUseSharedGroupItem(etcItem.getItemId(), group, reuseDelay, reuseDelay));
    }

    /**
     * Disable this ItemInstance id for the duration of the delay in milliseconds.
     *
     * @param item
     * @param delay (seconds * 1000)
     */
    public void disableItem(ItemInstance item, long delay) {
        if (item == null)
            return;

        _disabledItems.put(item.getObjectId(), System.currentTimeMillis() + delay);
    }

    /**
     * Check if an item is disabled. All skills disabled are identified by their reuse objectIds in <B>_disabledItems</B>.
     *
     * @param item The ItemInstance to check
     * @return true if the item is currently disabled.
     */
    public boolean isItemDisabled(ItemInstance item) {
        if (_disabledItems.isEmpty())
            return false;

        if (item == null || isAllSkillsDisabled())
            return true;

        final int hashCode = item.getObjectId();

        final Long timeStamp = _disabledItems.get(hashCode);
        if (timeStamp == null)
            return false;

        if (timeStamp < System.currentTimeMillis()) {
            _disabledItems.remove(hashCode);
            return false;
        }

        return true;
    }

    /**
     * Check if the requested casting is a Pc->Pc skill cast and if it's a valid pvp condition
     *
     * @param target        WorldObject instance containing the target
     * @param skill         L2Skill instance with the skill being casted
     * @param isCtrlPressed Boolean signifying if the control key was used to cast
     * @return {@code false} if the skill is a pvpSkill and target is not a valid pvp target, {@code true} otherwise.
     */
    public boolean canCastOffensiveSkillOnPlayable(Playable target, L2Skill skill, boolean isCtrlPressed) {
        // No checks for players in Olympiad
        Player targetPlayer = target.getActingPlayer();
        Player player = getActingPlayer();

        if (player.isInOlympiadMode()
                && targetPlayer.isInOlympiadMode()
                && player.getOlympiadGameId() == targetPlayer.getOlympiadGameId()) {
            return true;
        }

        // No checks for players in Duel
        if (player.isInDuel() && targetPlayer.isInDuel()
                && player.getDuelId() == targetPlayer.getDuelId()) {
            return true;
        }

        if (player.isEventPlayer() && targetPlayer.isEventPlayer()) {
            return EventEngine.isCanAttack(player, targetPlayer);
        }

        boolean sameParty = (isInParty() && targetPlayer.isInParty() && getParty().getLeader() == targetPlayer.getParty().getLeader());
        boolean sameCommandChannel = (isInParty() && targetPlayer.isInParty() && getParty().getCommandChannel() != null && getParty().getCommandChannel().containsPlayer(targetPlayer));
        boolean sameClan = (player.getClanId() > 0 && player.getClanId() == targetPlayer.getClanId());
        boolean sameAlliance = (player.getAllyId() > 0 && player.getAllyId() == targetPlayer.getAllyId());
        boolean sameSiegeSide = false;
        Siege siege = CastleManager.getInstance().getActiveSiege(this);

        if (siege != null) {
            sameSiegeSide = ((siege.checkSides(targetPlayer.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER) && siege.checkSides(player.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER)) || (siege.checkSide(targetPlayer.getClan(), SiegeSide.ATTACKER) && siege.checkSide(player.getClan(), SiegeSide.ATTACKER)));
            sameSiegeSide &= target.isInsideZone(ZoneId.SIEGE) && player.isInsideZone(ZoneId.SIEGE);
        }

        // Players in the same CC/party/alliance/clan may only damage each other with ctrlPressed.
        // If it's an AOE skill, only the mainTarget will be hit. PvpFlag / Karma do not influence these checks.
        boolean isMainTarget = getAI().getCurrentIntention().getFinalTarget() == target;
        boolean isCtrlDamagingTheMainTarget = isCtrlPressed && skill.isDamage() && isMainTarget;

        if (sameParty || sameCommandChannel || sameClan || sameAlliance || sameSiegeSide) {
            return isCtrlDamagingTheMainTarget;
        }

        // If the target not from the same CC/party/alliance/clan/SiegeSide is in a PVP area, you can do anything.
        if (isInsideZone(ZoneId.PVP) && targetPlayer.isInsideZone(ZoneId.PVP)) {
            return true;
        }

        int playerLevel = player.getStatus().getLevel();
        int targetLevel = targetPlayer.getStatus().getLevel();

        if (targetPlayer.getProtectionBlessing()
                && (playerLevel - targetLevel >= 10)
                && player.getKarma() > 0) {
            return false;
        }

        if (player.getProtectionBlessing()
                && (targetLevel - playerLevel >= 10)
                && targetPlayer.getKarma() > 0) {
            return false;
        }

        if (targetPlayer.isCursedWeaponEquipped() && playerLevel <= 20) {
            return false;
        }

        if (player.isCursedWeaponEquipped() && targetLevel <= 20) {
            return false;
        }

        // If the target not from the same CC/party/alliance/clan/SiegeSide is flagged / PK, you can do anything.
        if (targetPlayer.getPvpFlag() > 0 || targetPlayer.getKarma() > 0) {
            return true;
        }

        // If the caster not from the same CC/party/alliance/clan is at war with the target, then With CTRL he may damage and debuff.
        // CTRL is still necessary for damaging. You can do anything so long as you have CTRL pressed.
        // pvpFlag / Karma do not influence these checks
        Clan aClan = player.getClan();
        Clan tClan = targetPlayer.getClan();

        if (aClan != null
                && tClan != null
                && aClan.isAtWarWith(tClan.getClanId())
                && tClan.isAtWarWith(aClan.getClanId())) {
            return isCtrlPressed;
        }

        return isCtrlDamagingTheMainTarget;
    }

    @Override
    public boolean isAttackableBy(Creature attacker) {
        if (!super.isAttackableBy(attacker))
            return false;

        // Attackables can attack Playables anytime, anywhere
        if (attacker instanceof Monster)
            return true;

        // SiegeGuards cannot attack defenders/owners
        if (attacker instanceof SiegeGuard) {
            if (getActingPlayer().getClan() != null) {
                final Siege siege = CastleManager.getInstance().getActiveSiege(this);
                if (siege != null && siege.checkSides(getActingPlayer().getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER))
                    return false;
            }

            return true;
        }

        if (attacker instanceof Playable) {
            final Playable attackerPlayable = (Playable) attacker;

            // You cannot be attacked by a Playable in Olympiad before the start of the game.
            if (getActingPlayer().isInOlympiadMode() && !getActingPlayer().isOlympiadStart())
                return false;

            if (isInsideZone(ZoneId.PVP))
                return true;

            // One cannot be attacked if any of the two has Blessing of Protection and the other is >=10 levels higher and is PK
            if (getProtectionBlessing() && (attackerPlayable.getStatus().getLevel() - getStatus().getLevel() >= 10) && attackerPlayable.getKarma() > 0)
                return false;

            if (attackerPlayable.getProtectionBlessing() && (getStatus().getLevel() - attackerPlayable.getStatus().getLevel() >= 10) && getKarma() > 0)
                return false;

            // One cannot be attacked if any of the two is wielding a Cursed Weapon and the other is under level 20
            if (getActingPlayer().isCursedWeaponEquipped() && attackerPlayable.getStatus().getLevel() <= 20)
                return false;

            if (attackerPlayable.getActingPlayer().isCursedWeaponEquipped() && getStatus().getLevel() <= 20)
                return false;
        }

        return true;
    }

    @Override
    public boolean isAttackableWithoutForceBy(Playable attacker) {
        Player attackerPlayer = attacker.getActingPlayer();
        Player player = getActingPlayer();

        if (attackerPlayer.isInOlympiadMode()
                && player.isInOlympiadMode()
                && player.isOlympiadStart()
                && attackerPlayer.getOlympiadGameId() == player.getOlympiadGameId()) {
            return true;
        }

        if (player.getDuelState() == DuelState.DUELLING
                && player.getDuelId() == attackerPlayer.getDuelId()) {
            return true;
        }

        if (player.isEventPlayer() && attackerPlayer.isEventPlayer()) {
            return EventEngine.isCanAttack(player, attackerPlayer);
        }

        boolean sameParty = (isInParty() && attackerPlayer.isInParty() && getParty().getLeader() == attackerPlayer.getParty().getLeader());
        boolean sameCommandChannel = (isInParty() && attackerPlayer.isInParty() && getParty().getCommandChannel() != null && getParty().getCommandChannel().containsPlayer(attackerPlayer));
        boolean sameClan = (player.getClanId() > 0 && player.getClanId() == attackerPlayer.getClanId());
        boolean sameAlliance = (player.getAllyId() > 0 && player.getAllyId() == attackerPlayer.getAllyId());
        boolean sameSiegeSide = false;
        Siege siege = CastleManager.getInstance().getActiveSiege(this);

        if (siege != null) {
            sameSiegeSide = ((siege.checkSides(attackerPlayer.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER) && siege.checkSides(player.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER)) || (siege.checkSide(attackerPlayer.getClan(), SiegeSide.ATTACKER) && siege.checkSide(player.getClan(), SiegeSide.ATTACKER)));
            sameSiegeSide &= attackerPlayer.isInsideZone(ZoneId.SIEGE) && player.isInsideZone(ZoneId.SIEGE);
        }

        // Players in the same CC/party/alliance/clan cannot attack without CTRL
        if (sameParty || sameCommandChannel || sameClan || sameAlliance || sameSiegeSide) {
            return false;
        }

        // CTRL is not needed if both are in a PVP area
        if (isInsideZone(ZoneId.PVP) && attacker.isInsideZone(ZoneId.PVP)) {
            return true;
        }

        // CTRL is not needed if the target (this) is flagged / PK
        if (getKarma() > 0 || getPvpFlag() > 0) {
            return true;
        }

        // Any other case returns false, even clan war. You need CTRL to attack.
        return false;
    }

    /**
     * @param target : The {@link Creature} used as target.
     * @return True if this {@link Playable} can continue to attack the {@link Creature} set as target, false otherwise.
     */
    public boolean canKeepAttacking(Creature target) {
        if (target == null)
            return false;

        if (target instanceof Playable) {
            final Player targetPlayer = target.getActingPlayer();

            // Playables in Olympiad continue the attack
            if (targetPlayer.isInOlympiadMode() && getActingPlayer().isInOlympiadMode() && getActingPlayer().isOlympiadStart() && targetPlayer.getOlympiadGameId() == getActingPlayer().getOlympiadGameId())
                return true;

            // Playables in Duel continue the attack
            if (getActingPlayer().getDuelState() == DuelState.DUELLING && getActingPlayer().getDuelId() == targetPlayer.getDuelId())
                return true;

            // Playables in a PVP area continue the attack
            if (isInsideZone(ZoneId.PVP) && target.isInsideZone(ZoneId.PVP))
                return true;

            // Betrayer Summon can continue the attack
            if (this instanceof Summon && isBetrayed())
                return true;

            return false;
        }
        return true;
    }

    @Override
    public boolean testCursesOnAttack(Npc npc, int npcId) {
        if (Config.RAID_DISABLE_CURSE || !(npc instanceof Attackable))
            return false;

        // Petrification curse.
        if (getStatus().getLevel() - npc.getStatus().getLevel() > 8) {
            final AggroInfo ai = ((Attackable) npc).getAggroList().get(this);
            if (ai != null && ai.getHate() > 100) {
                final L2Skill curse = FrequentSkill.RAID_CURSE2.getSkill();
                if (getFirstEffect(curse) == null) {
                    broadcastPacket(new MagicSkillUse(npc, this, curse.getId(), curse.getLevel(), 300, 0));
                    curse.getEffects(npc, this);

                    ((Attackable) npc).getAggroList().stopHate(this);
                    return true;
                }
            }
        }

        // Antistrider slow curse.
        if (npc.getNpcId() == npcId && this instanceof Player && ((Player) this).isMounted()) {
            final L2Skill curse = FrequentSkill.RAID_ANTI_STRIDER_SLOW.getSkill();
            if (getFirstEffect(curse) == null) {
                broadcastPacket(new MagicSkillUse(npc, this, curse.getId(), curse.getLevel(), 300, 0));
                curse.getEffects(npc, this);
            }
        }
        return false;
    }

    @Override
    public boolean testCursesOnAttack(Npc npc) {
        return testCursesOnAttack(npc, npc.getNpcId());
    }

    @Override
    public boolean testCursesOnAggro(Npc npc) {
        return testCursesOnAttack(npc, -1);
    }

    @Override
    public boolean testCursesOnSkillSee(L2Skill skill, Creature[] targets) {
        if (Config.RAID_DISABLE_CURSE)
            return false;

        final boolean isAggressive = skill.isOffensive() || skill.isDebuff();

        if (isAggressive) {
            // Petrification.
            for (final Creature target : targets) {
                // Must be called by a raid related Attackable.
                if (!(target instanceof Attackable) || !target.isRaidRelated())
                    continue;

                if (getStatus().getLevel() - target.getStatus().getLevel() > 8) {
                    final L2Skill curse = FrequentSkill.RAID_CURSE2.getSkill();
                    if (getFirstEffect(curse) == null) {
                        broadcastPacket(new MagicSkillUse(target, this, curse.getId(), curse.getLevel(), 300, 0));
                        curse.getEffects(target, this);

                        ((Attackable) target).getAggroList().stopHate(this);
                        return true;
                    }
                }
            }
            return false;
        }

        // Silence - must be called by a raid related, the target must be in aggrolist with hate > 0, the effect must be beneficial.
        final List<Attackable> list = getKnownTypeInRadius(Attackable.class, 1000);
        if (!list.isEmpty()) {
            for (final Creature target : targets) {
                // Tested target must be a Playable.
                if (!(target instanceof Playable))
                    continue;

                for (Attackable attackable : list) {
                    // Must be called by a raid related Attackable.
                    if (!attackable.isRaidRelated())
                        continue;

                    if (getStatus().getLevel() - attackable.getStatus().getLevel() > 8) {
                        final AggroInfo ai = attackable.getAggroList().get(target);
                        if (ai != null && ai.getHate() > 0) {
                            final L2Skill curse = FrequentSkill.RAID_CURSE.getSkill();
                            if (getFirstEffect(curse) == null) {
                                broadcastPacket(new MagicSkillUse(attackable, this, curse.getId(), curse.getLevel(), 300, 0));
                                curse.getEffects(attackable, this);

                                attackable.getAggroList().stopHate(this);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}