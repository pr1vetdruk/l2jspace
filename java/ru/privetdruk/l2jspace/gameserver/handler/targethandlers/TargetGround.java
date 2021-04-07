package ru.privetdruk.l2jspace.gameserver.handler.targethandlers;

import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ITargetHandler;
import ru.privetdruk.l2jspace.gameserver.model.WorldRegion;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ValidateLocation;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class TargetGround implements ITargetHandler {
    @Override
    public SkillTargetType getTargetType() {
        return SkillTargetType.GROUND;
    }

    @Override
    public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill) {
        return new Creature[]
                {
                        caster
                };
    }

    @Override
    public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill) {
        return caster;
    }

    @Override
    public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed) {
        final WorldRegion region = caster.getRegion();
        if (region == null || !(caster instanceof Player))
            return false;

        final Player player = (Player) caster;

        final Location signetLocation = player.getCast().getSignetLocation();
        if (!GeoEngine.getInstance().canSeeLocation(player, signetLocation)) {
            player.sendPacket(SystemMessageId.CANT_SEE_TARGET);
            return false;
        }

        if (!region.checkEffectRangeInsidePeaceZone(skill, signetLocation)) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
            return false;
        }

        player.getPosition().setHeadingTo(signetLocation);
        player.broadcastPacket(new ValidateLocation(player));
        return true;
    }
}