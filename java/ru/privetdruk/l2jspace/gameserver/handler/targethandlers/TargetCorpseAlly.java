package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.scripting.script.ai.boss.Baium;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

import java.util.ArrayList;
import java.util.List;

public class TargetCorpseAlly implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.CORPSE_ALLY;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        final List<Player> list = new ArrayList<>();
        if (caster instanceof Player) {
            final boolean castInsideBossZone = Baium.BAIUM_LAIR.isInsideZone(caster);

            final Player player = caster.getActingPlayer();
            if (player.getClan() != null) {
                for (Player targetPlayer : player.getKnownTypeInRadius(Player.class, skill.getSkillRadius())) {
                    if (!targetPlayer.isDead() || targetPlayer.getClan() == null || castInsideBossZone != Baium.BAIUM_LAIR.isInsideZone(targetPlayer))
                        continue;

                    // Avoid select player that are not in same clan.
                    if (player.getClanId() != targetPlayer.getClanId() && player.getAllyId() > 0 && player.getAllyId() != targetPlayer.getAllyId())
                        continue;

                    // Do not select player from opposing duel side
                    if (player.isInDuel() && (player.getDuelId() != targetPlayer.getDuelId() || player.getTeam() != targetPlayer.getTeam()))
                        continue;

                    list.add(targetPlayer);
                }
            }
        }

        if (list.isEmpty())
            return new Creature[]
                    {
                            caster
                    };

        return list.toArray(new Creature[list.size()]);
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        return caster;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        final Player player = caster.getActingPlayer();
        if (player.isInOlympiadMode()) {
            caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
            return false;
        }
        return true;
    }
}