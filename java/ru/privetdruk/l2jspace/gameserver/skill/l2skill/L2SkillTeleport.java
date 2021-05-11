package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;

import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData;
import ru.privetdruk.l2jspace.gameserver.data.xml.MapRegionData.TeleportType;
import ru.privetdruk.l2jspace.gameserver.enums.ZoneId;
import ru.privetdruk.l2jspace.gameserver.enums.items.ShotType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillTeleport extends L2Skill {
    private final String _recallType;
    private final Location _loc;

    public L2SkillTeleport(StatSet set) {
        super(set);

        _recallType = set.getString("recallType", "");
        String coords = set.getString("teleCoords", null);
        if (coords != null) {
            String[] valuesSplit = coords.split(",");
            _loc = new Location(Integer.parseInt(valuesSplit[0]), Integer.parseInt(valuesSplit[1]), Integer.parseInt(valuesSplit[2]));
        } else
            _loc = null;
    }

    @Override
    public void useSkill(Creature activeChar, WorldObject[] targets) {
        if (activeChar instanceof Player) {
            // Check invalid states.
            if (activeChar.isAfraid() || ((Player) activeChar).isInOlympiadMode()) // || activeChar.isInsideZone(ZoneId.BOSS))
                return;
        }

        boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);

        for (WorldObject obj : targets) {
            if (!(obj instanceof Creature))
                continue;

            final Creature target = ((Creature) obj);

            if (target instanceof Player) {
                Player targetChar = (Player) target;

                // Check invalid states.
                if (targetChar.isFestivalParticipant()
                        || targetChar.isInJail()
                        || targetChar.isInDuel()
                        || targetChar.isEventPlayer()) {
                    continue;
                }

                if (targetChar != activeChar) {
                    if (targetChar.isInOlympiadMode())
                        continue;

                    if (targetChar.isInsideZone(ZoneId.BOSS))
                        continue;
                }
            }

            Location loc = null;
            if (getSkillType() == SkillType.TELEPORT) {
                if (_loc != null) {
                    if (!(target instanceof Player) || !target.isFlying())
                        loc = _loc;
                }
            } else {
                if (_recallType.equalsIgnoreCase("Castle"))
                    loc = MapRegionData.getInstance().getLocationToTeleport(target, TeleportType.CASTLE);
                else if (_recallType.equalsIgnoreCase("ClanHall"))
                    loc = MapRegionData.getInstance().getLocationToTeleport(target, TeleportType.CLAN_HALL);
                else
                    loc = MapRegionData.getInstance().getLocationToTeleport(target, TeleportType.TOWN);
            }

            if (loc != null) {
                if (target instanceof Player)
                    ((Player) target).setIsIn7sDungeon(false);

                target.teleportTo(loc, 20);
            }
        }

        activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
    }
}