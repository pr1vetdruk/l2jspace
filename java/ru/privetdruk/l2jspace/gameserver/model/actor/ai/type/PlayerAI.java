package ru.privetdruk.l2jspace.gameserver.model.actor.ai.type;

import ru.privetdruk.l2jspace.common.pool.ThreadPool;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.CursedWeaponManager;
import ru.privetdruk.l2jspace.gameserver.enums.AiEventType;
import ru.privetdruk.l2jspace.gameserver.enums.IntentionType;
import ru.privetdruk.l2jspace.gameserver.enums.LootRule;
import ru.privetdruk.l2jspace.gameserver.enums.items.ArmorType;
import ru.privetdruk.l2jspace.gameserver.enums.items.EtcItemType;
import ru.privetdruk.l2jspace.gameserver.enums.items.WeaponType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.IItemHandler;
import ru.privetdruk.l2jspace.gameserver.handler.ItemHandler;
import ru.privetdruk.l2jspace.gameserver.handler.admincommandhandlers.AdminInfo;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Boat;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Chest;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Door;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.FestivalMonster;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.GrandBoss;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Monster;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.RaidBoss;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.StaticObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Walker;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.location.BoatEntrance;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.clientpackets.RequestBypassToServer;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ActionFailed;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.AutoAttackStart;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ChairSit;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MoveToLocationInVehicle;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MoveToPawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.StopMove;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.taskmanager.AttackStanceTaskManager;
import ru.privetdruk.l2jspace.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class PlayerAI extends PlayableAI {
    public PlayerAI(Player player) {
        super(player);
    }

    @Override
    protected void onEvtArrived() {
        if (_currentIntention.getType() == IntentionType.MOVE_TO) {
            final Boat boat = _currentIntention.getBoat();
            if (boat != null) {
                final BoatEntrance closestEntrance = boat.getClosestEntrance(getActor().getPosition());

                getActor().getBoatPosition().set(closestEntrance.getInnerLocation());

                // Since we're close enough to the boat we just send client onboarding packet without any movement on the server.
                getActor().broadcastPacket(new MoveToLocationInVehicle(getActor(), boat, closestEntrance.getInnerLocation(), getActor().getPosition()));
            }
        }

        super.onEvtArrived();
    }

    @Override
    protected void onEvtArrivedBlocked() {
        if (_currentIntention.getType() == IntentionType.INTERACT) {
            clientActionFailed();

            final WorldObject target = _currentIntention.getTarget();
            if (getActor().getAI().canDoInteract(target)) {
                getActor().broadcastPacket(new StopMove(getActor()));

                target.onInteract(getActor());
            } else
                super.onEvtArrivedBlocked();

            doIdleIntention();
        } else
            super.onEvtArrivedBlocked();
    }

    @Override
    protected void onEvtSatDown(WorldObject target) {
        if (_nextIntention.isBlank())
            doIdleIntention();
        else
            doIntention(_nextIntention);
    }

    @Override
    protected void onEvtStoodUp() {
        if (getActor().getThroneId() != 0) {
            final WorldObject object = World.getInstance().getObject(getActor().getThroneId());
            if (object instanceof StaticObject)
                ((StaticObject) object).setBusy(false);

            getActor().setThroneId(0);
        }

        if (_nextIntention.isBlank())
            doIdleIntention();
        else
            doIntention(_nextIntention);
    }

    @Override
    protected void onEvtBowAttackReuse() {
        if (getActor().getAttackType() == WeaponType.BOW) {
            // Attacks can be scheduled while isAttackingNow
            if (_nextIntention.getType() == IntentionType.ATTACK) {
                doIntention(_nextIntention);
                return;
            }

            if (_currentIntention.getType() == IntentionType.ATTACK) {
                if (getActor().canKeepAttacking(_currentIntention.getFinalTarget()))
                    notifyEvent(AiEventType.THINK, null, null);
                else
                    doIdleIntention();
            }
        }
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {
        if (getActor().getTamedBeast() != null)
            getActor().getTamedBeast().getAI().notifyEvent(AiEventType.OWNER_ATTACKED, attacker, null);

        if (getActor().isSitting())
            doStandIntention();

        super.onEvtAttacked(attacker);
    }

    @Override
    protected void onEvtCancel() {
        getActor().getCast().stop();
        getActor().getMove().cancelFollowTask();

        doIdleIntention();
    }

    @Override
    public synchronized void doActiveIntention() {
        doIdleIntention();
    }

    @Override
    public synchronized void tryToActive() {
        tryToIdle();
    }

    @Override
    protected void thinkActive() {
        thinkIdle();
    }

    @Override
    protected void thinkAttack() {
        final Creature target = _currentIntention.getFinalTarget();
        final boolean isShiftPressed = _currentIntention.isShiftPressed();

        if (tryShiftClick(target, isShiftPressed))
            return;

        if (getActor().denyAiAction() || getActor().isSitting()) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        if (isTargetLost(target)) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        if (getActor().getMove().maybeMoveToPawn(target, getActor().getStatus().getPhysicalAttackRange(), isShiftPressed)) {
            if (isShiftPressed) {
                doIdleIntention();
                clientActionFailed();
            }

            return;
        }

        getActor().getMove().stop();

        if ((getActor().getAttackType() == WeaponType.BOW && getActor().getAttack().isBowCoolingDown()) || getActor().getAttack().isAttackingNow()) {
            setNextIntention(_currentIntention);
            clientActionFailed();
            return;
        }

        if (!getActor().getAttack().canDoAttack(target)) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        getActor().getAttack().doAttack(target);
        if (!Config.ATTACK_PTS)
            setNextIntention(_currentIntention);
    }

    @Override
    protected void thinkCast() {
        Player actor = getActor();

        if (actor.denyAiAction() || actor.getAllSkillsDisabled() || actor.getCast().isCastingNow()) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        Creature target = _currentIntention.getFinalTarget();

        if (target == null) {
            doIdleIntention();
            return;
        }

        L2Skill skill = _currentIntention.getSkill();

        if (isTargetLost(target, skill)) {
            doIdleIntention();
            return;
        }

        if (!actor.getCast().canAttemptCast(target, skill)) {
            doIdleIntention();
            return;
        }

        boolean isShiftPressed = _currentIntention.isShiftPressed();

        if (skill.getTargetType() == SkillTargetType.GROUND) {
            if (actor.getMove().maybeMoveToLocation(actor.getCast().getSignetLocation(), skill.getCastRange(), false, isShiftPressed)) {
                if (isShiftPressed) {
                    actor.sendPacket(SystemMessageId.TARGET_TOO_FAR);
                    doIdleIntention();
                }

                return;
            }
        } else {
            if (actor.getMove().maybeMoveToPawn(target, skill.getCastRange(), isShiftPressed)) {
                if (isShiftPressed) {
                    actor.sendPacket(SystemMessageId.TARGET_TOO_FAR);
                    doIdleIntention();
                }

                return;
            }
        }


        if (skill.isToggle()) {
            actor.getMove().stop();
            actor.getCast().doToggleCast(skill, target);
        } else {
            boolean isCtrlPressed = _currentIntention.isCtrlPressed();
            int itemObjectId = _currentIntention.getItemObjectId();

            if (!actor.getCast().canDoCast(target, skill, isCtrlPressed, itemObjectId)) {
                if ((skill.nextActionIsAttack() && target.isAttackableWithoutForceBy(actor))) {
                    doAttackIntention(target, isCtrlPressed, isShiftPressed);
                } else {
                    actor.sendPacket(new StopMove(actor));
                    doIdleIntention();
                }

                return;
            }

            if (skill.getHitTime() > 50) {
                actor.getMove().stop();
            }

            if (skill.getSkillType() == SkillType.FUSION || skill.getSkillType() == SkillType.SIGNET_CASTTIME) {
                actor.getCast().doFusionCast(skill, target);
            } else {
                actor.getCast().doCast(skill, target, _actor.getInventory().getItemByObjectId(itemObjectId));
            }
        }
    }

    @Override
    protected void thinkFakeDeath() {
        if (getActor().denyAiAction() || getActor().isMounted()) {
            clientActionFailed();
            return;
        }

        // Start fake death hidden in isCtrlPressed.
        if (_currentIntention.isCtrlPressed()) {
            getActor().getMove().stop();
            getActor().startFakeDeath();
        } else
            getActor().stopFakeDeath(false);
    }

    @Override
    protected ItemInstance thinkPickUp() {
        final ItemInstance item = super.thinkPickUp();
        if (item == null)
            return null;

        synchronized (item) {
            if (!item.isVisible())
                return null;

            if (((getActor().isInParty() && getActor().getParty().getLootRule() == LootRule.ITEM_LOOTER) || !getActor().isInParty()) && !getActor().getInventory().validateCapacity(item)) {
                getActor().sendPacket(SystemMessageId.SLOTS_FULL);
                return null;
            }

            if (getActor().getActiveTradeList() != null) {
                getActor().sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
                return null;
            }

            if (item.getOwnerId() != 0 && !getActor().isLooterOrInLooterParty(item.getOwnerId())) {
                if (item.getItemId() == 57)
                    getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
                else if (item.getCount() > 1)
                    getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item).addNumber(item.getCount()));
                else
                    getActor().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item));

                return null;
            }

            if (item.hasDropProtection())
                item.removeDropProtection();

            item.pickupMe(getActor());

            ItemsOnGroundTaskManager.getInstance().remove(item);
        }

        if (item.getItemType() == EtcItemType.HERB) {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
            if (handler != null)
                handler.useItem(getActor(), item, false);

            item.destroyMe("Consume", getActor(), null);
        } else if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
            getActor().addItem("Pickup", item, null, true);
        else {
            if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType) {
                SystemMessage sm;
                if (item.getEnchantLevel() > 0)
                    sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2_S3).addString(getActor().getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
                else
                    sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2).addString(getActor().getName()).addItemName(item.getItemId());

                getActor().broadcastPacketInRadius(sm, 1400);
            }

            if (getActor().isInParty())
                getActor().getParty().distributeItem(getActor(), item);
            else if (item.getItemId() == 57 && getActor().getInventory().getAdenaInstance() != null) {
                getActor().addAdena("Pickup", item.getCount(), null, true);
                item.destroyMe("Pickup", getActor(), null);
            } else
                getActor().addItem("Pickup", item, null, true);
        }

        ThreadPool.schedule(() -> getActor().setIsParalyzed(false), 200);
        getActor().setIsParalyzed(true);

        return item;
    }

    @Override
    protected void thinkInteract() {
        final WorldObject target = _currentIntention.getTarget();
        final boolean isShiftPressed = _currentIntention.isShiftPressed();

        if (tryShiftClick(target, isShiftPressed))
            return;

        clientActionFailed();

        if (getActor().denyAiAction() || getActor().isSitting() || getActor().isFlying()) {
            doIdleIntention();
            return;
        }

        if (isTargetLost(target)) {
            doIdleIntention();
            return;
        }

        if (!getActor().getAI().canAttemptInteract()) {
            doIdleIntention();
            return;
        }

        if (getActor().getAttackType() == WeaponType.BOW && target instanceof Monster && ((Monster) target).isDead()) {
            if (getActor().getMove().maybeMoveToPawn(target, getActor().getStatus().getPhysicalAttackRange(), isShiftPressed))
                doIdleIntention();
            return;
        }

        if (getActor().getMove().maybeMoveToPawn(target, 100, isShiftPressed)) {
            if (isShiftPressed)
                doIdleIntention();

            return;
        }

        if (!getActor().getAI().canDoInteract(target)) {
            doIdleIntention();
            return;
        }

        if (target instanceof Walker)
            getActor().broadcastPacket(new StopMove(getActor()));
        else
            getActor().broadcastPacket(new MoveToPawn(_actor, target, Npc.INTERACTION_DISTANCE));

        target.onInteract(getActor());

        doIdleIntention();
    }

    private boolean tryShiftClick(final WorldObject target, final boolean isShiftPressed) {
        if (isShiftPressed) {
            if (getActor().isGM()) {
                if (target instanceof Npc) {
                    var html = new NpcHtmlMessage(0);
                    AdminInfo.sendGeneralInfos((Npc) target, html);
                    getActor().sendPacket(html);
                    doIdleIntention();
                    clientActionFailed();
                    return true;
                } else if (target instanceof Door) {
                    var html = new NpcHtmlMessage(0);
                    AdminInfo.showDoorInfo((Door) target, html);
                    getActor().sendPacket(html);
                    doIdleIntention();
                    clientActionFailed();
                    return true;
                }
            } else if (Config.SHOW_NPC_INFO) {
                if (target instanceof Monster || target instanceof RaidBoss || target instanceof GrandBoss || target instanceof FestivalMonster || target instanceof Chest) {
                    var html = new NpcHtmlMessage(0);
                    RequestBypassToServer.showNpcStatsInfos((Npc) target, html);
                    getActor().sendPacket(html);
                    doIdleIntention();
                    clientActionFailed();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void thinkSit() {
        if (getActor().denyAiAction() || getActor().isSitting() || getActor().isOperating() || getActor().isMounted()) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        getActor().getMove().stop();

        // sitDown sends the ChangeWaitType packet, which MUST precede the ChairSit packet (sent in this function) in order to properly sit on the throne.
        getActor().sitDown();

        final WorldObject target = _currentIntention.getTarget();
        final boolean isThrone = target instanceof StaticObject && ((StaticObject) target).getType() == 1;
        if (isThrone && !((StaticObject) target).isBusy() && getActor().isIn3DRadius(target, Npc.INTERACTION_DISTANCE)) {
            getActor().setThroneId(target.getObjectId());

            ((StaticObject) target).setBusy(true);
            getActor().broadcastPacket(new ChairSit(getActor().getObjectId(), ((StaticObject) target).getStaticObjectId()));
        }
    }

    @Override
    protected void thinkStand() {
        // no need to getActor().isOperating() here, because it is included in the Player overriden denyAiAction
        if (getActor().denyAiAction() || !getActor().isSitting() || getActor().isMounted()) {
            doIdleIntention();
            clientActionFailed();
            return;
        }

        if (getActor().isFakeDeath())
            getActor().stopFakeDeath(true);
        else
            getActor().standUp();
    }

    @Override
    protected void thinkUseItem() {
        final ItemInstance itemToTest = getActor().getInventory().getItemByObjectId(_currentIntention.getItemObjectId());
        if (itemToTest == null)
            return;

        // Equip or unequip the related ItemInstance.
        getActor().useEquippableItem(itemToTest, false);

        // Resolve previous intention
        doIntention(_previousIntention);
    }

    @Override
    public boolean canAttemptInteract() {
        if (getActor().isOperating() || getActor().isProcessingTransaction())
            return false;

        return true;
    }

    @Override
    public boolean canDoInteract(WorldObject target) {
        // Can't interact in shop mode, or during a transaction or a request.
        if (getActor().isOperating() || getActor().isProcessingTransaction())
            return false;

        // Can't interact if regular distance doesn't match.
        return target.isIn3DRadius(getActor(), Npc.INTERACTION_DISTANCE);
    }

    @Override
    public void startAttackStance() {
        if (!AttackStanceTaskManager.getInstance().isInAttackStance(getActor())) {
            final Summon summon = getActor().getSummon();
            if (summon != null)
                summon.broadcastPacket(new AutoAttackStart(summon.getObjectId()));

            getActor().broadcastPacket(new AutoAttackStart(getActor().getObjectId()));
        }

        AttackStanceTaskManager.getInstance().add(getActor());
    }

    @Override
    public void clientActionFailed() {
        getActor().sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public Player getActor() {
        return (Player) _actor;
    }
}