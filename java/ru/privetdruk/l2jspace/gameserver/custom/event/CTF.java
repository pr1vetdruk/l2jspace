package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.builder.EventSettingBuilder;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.Reward;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfEventPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Flag;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Throne;
import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.enums.AuraTeamType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.container.player.RadarOnPlayer;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.common.util.StringUtil.secondWords;
import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Mount.Wyvern.WYVERN_BREATH;
import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Prophet.MAGIC_BARRIER;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN_TEAM;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.ERROR;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.FINISH;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.IN_PROGRESS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.REGISTRATION;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.TELEPORTATION;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.BALANCE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.NO;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.SHUFFLE;
import static ru.privetdruk.l2jspace.gameserver.model.item.kind.Item.SLOT_LR_HAND;

public class CTF extends EventEngine {
    private EventBorder eventBorder;
    private final List<CtfTeamSetting> ctfTeamSettings = new ArrayList<>();

    public CTF() {
        super(
                EventType.CTF,
                EventConfig.CTF.TEAM_MODE,
                EventConfig.CTF.UNSUMMON_PET,
                EventConfig.CTF.REMOVE_ALL_EFFECTS,
                EventConfig.CTF.JOIN_CURSED_WEAPON,
                EventConfig.CTF.REMOVE_BUFFS_ON_DIE
        );

        eventTaskList.add(this);
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE id = ? AND type = ?");
            statement.setInt(1, eventId);
            statement.setString(2, eventType.name());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                LOGGER.warning("Setting ctf not found!");
                return;
            }

            settings = new EventSettingBuilder()
                    .setName(resultSet.getString("name"))
                    .setDescription(resultSet.getString("description"))
                    .setRegistrationLocationName(resultSet.getString("registration_location"))
                    .setMinLevel(resultSet.getInt("min_level"))
                    .setMaxLevel(resultSet.getInt("max_level"))
                    .setNpc(new NpcInfoShort(
                            resultSet.getInt("npc_id"),
                            new SpawnLocation(
                                    resultSet.getInt("npc_x"),
                                    resultSet.getInt("npc_y"),
                                    resultSet.getInt("npc_z"),
                                    resultSet.getInt("npc_heading")
                            )
                    ))
                    .setReward(new Reward(
                            resultSet.getInt("reward_id"),
                            resultSet.getInt("reward_amount")
                    ))
                    .setTimeRegistration(resultSet.getInt("time_registration"))
                    .setDurationTime(resultSet.getInt("duration_event"))
                    .setMinPlayers(resultSet.getInt("min_players"))
                    .setMaxPlayers(resultSet.getInt("max_players"))
                    .setIntervalBetweenMatches(resultSet.getLong("delay_next_event"))
                    .build();

            statement.close();
            resultSet.close();

            statement = connection.prepareStatement("SELECT * FROM event_ctf_team_setting WHERE event_id = ?");
            statement.setInt(1, eventId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ctfTeamSettings.add(
                        new CtfTeamSetting(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getInt("name_color"),
                                resultSet.getInt("offset"),
                                new Location(
                                        resultSet.getInt("position_x"),
                                        resultSet.getInt("position_y"),
                                        resultSet.getInt("position_z")
                                ),
                                new Flag(
                                        new NpcInfoShort(
                                                resultSet.getInt("flag_npc_id"),
                                                new SpawnLocation(
                                                        resultSet.getInt("flag_position_x"),
                                                        resultSet.getInt("flag_position_y"),
                                                        resultSet.getInt("flag_position_z")
                                                )
                                        ),
                                        resultSet.getInt("flag_item_id"),
                                        false
                                ),
                                new Throne(
                                        new NpcInfoShort(
                                                resultSet.getInt("throne_npc_id"),
                                                new SpawnLocation(
                                                        resultSet.getInt("flag_position_x"),
                                                        resultSet.getInt("flag_position_y"),
                                                        resultSet.getInt("flag_position_z") - resultSet.getInt("offset_throne_position_z")
                                                )
                                        ),
                                        resultSet.getInt("offset_throne_position_z")
                                )
                        )
                );
            }

            teamSettings.addAll(ctfTeamSettings);

            resultSet.close();
        } catch (Exception e) {
            eventState = ERROR;
            logError("loadData", e.getMessage());
        }
    }

    public void restoreFlags() {
        try {
            int numberFlagsTaken = 0;

            for (EventPlayer eventPlayer : players.values()) {
                if (numberFlagsTaken == ctfTeamSettings.size()) {
                    break;
                }

                CtfEventPlayer ctfPlayer = (CtfEventPlayer) eventPlayer;

                if (!ctfPlayer.isHasFlag()) {
                    continue;
                }

                Player player = ctfPlayer.getPlayer();
                CtfTeamSetting team = ctfPlayer.getTeamSettings();


                if (player.isOnline()) {
                    if (inRange(player, team.getThrone().getNpc().getSpawnLocation(), 150)) {
                        returnFlag(ctfPlayer);
                    } else if (isOutsideArea(player)) {
                        announceCritical("Игрок " + player.getName() + " сбежал с мероприятия с флагом!");

                        CtfTeamSetting enemyTeam = ((CtfEventPlayer) eventPlayer).getEnemyFlag();

                        if (enemyTeam.getFlag().isTaken()) {
                            enemyTeam.getFlag().setTaken(false);

                            spawnFlag(enemyTeam);

                            announceCritical("Флаг команды " + enemyTeam.getName() + " был возвращен на место!");
                        }

                        removeFlagFromPlayer(ctfPlayer);

                        Location spawnLocation = team.getSpawnLocation();

                        player.teleToLocation(spawnLocation);

                        player.sendMessage("Вы возвращены на место респавна вашей команды.");
                    }
                } else {
                    team.getFlag().setTaken(false);

                    removeFlagFromPlayer(ctfPlayer);
                    spawnFlag(team);

                    announceCritical("Игрок " + player.getName() + " покинул игру с флагом!");
                    announceCritical("Флаг команды " + team.getName() + " был возвращен на место!");
                }

                numberFlagsTaken++;
            }
        } catch (Exception e) {
            logError("restoreFlags", e.getMessage());
        }
    }

    public void restoreFlagOnPlayerDie(CtfEventPlayer eventPlayer) {
        CtfTeamSetting team = eventPlayer.getEnemyFlag();

        team.getFlag().setTaken(false);
        spawnFlag(team);
        removeFlagFromPlayer(eventPlayer);

        announceCritical("Флаг команды " + team.getName() + " возвращен на базу!");
    }

    public void processInFlagRange(CtfEventPlayer eventPlayer) {
        try {
            restoreFlags();

            ctfTeamSettings.stream()
                    .filter(setting -> inRange(
                            eventPlayer.getPlayer(),
                            setting.getFlag().getNpc().getSpawnLocation(),
                            150
                    ))
                    .forEach(team -> {
                        Player player = eventPlayer.getPlayer();

                        // If player is near his team flag holding the enemy flag
                        if (eventPlayer.getTeamSettings() == team && eventPlayer.isHasFlag()) {
                            returnFlag(eventPlayer);
                        } else if (eventPlayer.getTeamSettings() != team // If the player is near a enemy flag
                                && !eventPlayer.isHasFlag()
                                && !player.isDead()
                                && !team.getFlag().isTaken()) {
                            pickUpFlag(eventPlayer, team);
                        }
                    });
        } catch (Exception e) {
            LOGGER.warning(e.toString());
        }
    }

    private void pickUpFlag(CtfEventPlayer eventPlayer, CtfTeamSetting enemyTeam) {
        enemyTeam.getFlag().setTaken(true);

        unspawn(enemyTeam.getFlag().getSpawn());
        addFlagToPlayer(eventPlayer, enemyTeam);
        displayRadar(eventPlayer.getPlayer(), enemyTeam);

        announceCritical(format("Игрок %s забрал флаг команды %s!", eventPlayer.getPlayer().getName(), enemyTeam.getName()));
    }

    private void returnFlag(CtfEventPlayer eventPlayer) {
        CtfTeamSetting enemyTeam = eventPlayer.getEnemyFlag();

        enemyTeam.getFlag().setTaken(false);
        spawnFlag(enemyTeam);

        // Remove the flag from this player
        Player player = eventPlayer.getPlayer();
        player.broadcastPacket(new SocialAction(player, 16)); // Amazing glow TODO id
        player.broadcastUserInfo();
        player.broadcastPacket(new SocialAction(player, 3)); // Victory TODO id
        player.broadcastUserInfo();

        removeFlagFromPlayer(eventPlayer);

        CtfTeamSetting team = eventPlayer.getTeamSettings();
        team.addPoint();

        announceCritical("Игрок " + player.getName() + " заработал 1 очко для своей команды.");
        announceCritical(format(
                "Теперь у команды %s: %d %s.",
                team.getName(),
                team.getPoints(),
                StringUtil.declensionWords(team.getPoints(), StringUtil.pointWords)
        ));
    }

    private void displayRadar(Player targetPlayer, CtfTeamSetting teamOurFlag) {
        try {
            players.values().stream()
                    .filter(eventPlayer -> eventPlayer.getTeamSettings() == teamOurFlag
                            && eventPlayer.getPlayer().isOnline())
                    .forEach(eventPlayer -> {
                        CtfEventPlayer ctfPlayer = (CtfEventPlayer) eventPlayer;
                        Player player = ctfPlayer.getPlayer();

                        player.sendMessage("Игрок " + targetPlayer.getName() + " взял ваш флаг!");

                        if (!ctfPlayer.isHasFlag()) {
                            player.sendPacket(new RadarControl(0, 1, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ()));
                            RadarOnPlayer radarOnPlayer = new RadarOnPlayer(player, targetPlayer);
                            ThreadPool.schedule(radarOnPlayer, 10000 + Rnd.get(30000));
                        }
                    });
        } catch (Exception e) {
            LOGGER.severe(e.toString());
        }
    }

    private boolean inRange(Player player, Location flag, int offset) {
        return player.getX() > (flag.getX() - offset) &&
                player.getX() < (flag.getX() + offset) &&
                player.getY() > (flag.getY() - offset) &&
                player.getY() < (flag.getY() + offset) &&
                player.getZ() > (flag.getZ() - offset) &&
                player.getZ() < (flag.getZ() + offset);
    }

    public void addFlagToPlayer(CtfEventPlayer eventPlayer, CtfTeamSetting team) {
        Player player = eventPlayer.getPlayer();
        int flagItemId = team.getFlag().getItemId();

        ItemInstance activeWeapon = player.getActiveWeaponInstance();

        if (activeWeapon != null) {
            player.getInventory().unequipItemInBodySlotAndRecord(SLOT_LR_HAND);
        }

        // Add the flag in his hands
        eventPlayer.setEnemyFlag(team);
        player.getInventory().equipItem(ItemInstance.create(flagItemId, 1, player, null));
        player.broadcastPacket(new SocialAction(player, 16)); // Amazing glow
        player.broadcastUserInfo();
        player.sendPacket(new CreatureSay(player.getObjectId(), SayType.PARTYROOM_COMMANDER, "", "Отлично! Теперь отнеси флаг на свою базу!"));
    }

    private void unspawn(Spawn spawn) {
        if (spawn != null) {
            spawn.getNpc().deleteMe();
            SpawnTable.getInstance().deleteSpawn(spawn, true);
        }
    }

    private boolean isOutsideArea(Player player) {
        Location center = eventBorder.getCenter();

        return player.getX() <= (center.getX() - eventBorder.getOffset()) ||
                player.getX() >= (center.getX() + eventBorder.getOffset()) ||
                player.getY() <= (center.getY() - eventBorder.getOffset()) ||
                player.getY() >= (center.getY() + eventBorder.getOffset()) ||
                player.getZ() <= (center.getZ() - eventBorder.getOffset()) ||
                player.getZ() >= (center.getZ() + eventBorder.getOffset());
    }

    private void spawnFlag(CtfTeamSetting team) {
        NpcInfoShort flagNpc = team.getFlag().getNpc();
        NpcTemplate flagTemplate = NpcData.getInstance().getTemplate(flagNpc.getId());

        try {
            Spawn flagSpawn = configureSpawn(flagTemplate, flagNpc.getSpawnLocation(), team.getName() + "' Flag");
            team.getFlag().setSpawn(flagSpawn);
        } catch (Exception e) {
            logError("spawnFlag", e.getMessage());
        }

    }

    @Override
    protected boolean preLaunchChecksCustom() {
        return true;
    }

    @Override
    public boolean isSitForced() {
        return eventState == TELEPORTATION || eventState == FINISH;
    }

    @Override
    protected void restorePlayerDataCustom(EventPlayer eventPlayer) {
        Player player = eventPlayer.getPlayer();

        player.getAppearance().setNameColor(eventPlayer.getOriginalColorName());
        player.setTitle(eventPlayer.getOriginalTitle());

        if (eventPlayer.getOriginalKarma() > 0) {
            player.setKarma(eventPlayer.getOriginalKarma());
        }

        if (EventConfig.CTF.AURA) {
            player.setAura(AuraTeamType.NONE);
        }

        player.broadcastUserInfo();

        if (teamMode == NO || teamMode == BALANCE) {
            eventPlayer.getTeamSettings().removePlayer();
        }

        removeFlagFromPlayer((CtfEventPlayer) eventPlayer);
    }

    @Override
    protected void updatePlayerEventDataCustom(EventPlayer e) {
        CtfEventPlayer eventPlayer = (CtfEventPlayer) e;
        Player player = eventPlayer.getPlayer();
        TeamSetting team = eventPlayer.getTeamSettings();

        player.getAppearance().setNameColor(team.getColor());
        if (player.getKarma() > 0) {
            player.setKarma(0);
        }

        if (EventConfig.CTF.AURA && teamSettings.size() == 2) {
            player.setAura(AuraTeamType.fromId(teamSettings.indexOf(team) + 1));
        }

        if (player.isMounted()) {
            if (player.isFlying()) {
                player.removeSkill(WYVERN_BREATH.getId(), true);
            }

            player.broadcastPacket(new Ride(player.getObjectId(), Ride.ACTION_DISMOUNT, 0));
            player.dismount();
        }

        player.broadcastUserInfo();
    }

    @Override
    protected void spawnOtherNpc() {
        teamSettings.forEach(generalSettings -> {
            CtfTeamSetting team = (CtfTeamSetting) generalSettings;
            Throne throne = team.getThrone();
            NpcTemplate throneTemplate = NpcData.getInstance().getTemplate(throne.getNpc().getId());
            Flag flag = team.getFlag();
            NpcTemplate flagTemplate = NpcData.getInstance().getTemplate(flag.getNpc().getId());

            try {
                Spawn throneSpawn = configureSpawn(throneTemplate, throne.getNpc().getSpawnLocation(), team.getName() + "'s  Throne");
                throneSpawn.getLoc().setZ(throneSpawn.getLoc().getZ() - throne.getOffsetZ());
                Npc throneNpc = throneSpawn.getNpc();
                throneNpc.broadcastPacket(new MagicSkillUse(
                        throneNpc,
                        throneNpc,
                        MAGIC_BARRIER.getId(),
                        1,
                        5500,
                        1
                ));
                throne.setSpawn(throneSpawn);

                Spawn flagSpawn = configureSpawn(flagTemplate, flag.getNpc().getSpawnLocation(), team.getName() + "'s Flag");
                flag.setSpawn(flagSpawn);

                calculateOutSide(); // Sets event boundaries so players don't run with the flag.
            } catch (Exception e) {
                logError("spawnOtherNpc", e.getMessage());
            }
        });
    }

    private void calculateOutSide() {
        if (eventBorder != null) {
            return;
        }

        eventBorder = new EventBorder();

        int division = ctfTeamSettings.size() * 2;

        int pos = 0;
        final int[] locX = new int[division];
        final int[] locY = new int[division];
        final int[] locZ = new int[division];

        // Get all coordinates inorder to create a polygon:
        for (CtfTeamSetting team : ctfTeamSettings) {
            Spawn flag = team.getFlag().getSpawn();
            if (flag == null) {
                continue;
            }

            locX[pos] = flag.getLocX();
            locY[pos] = flag.getLocY();
            locZ[pos] = flag.getLocZ();
            pos++;
            if (pos > (division / 2)) {
                break;
            }
        }

        for (CtfTeamSetting team : ctfTeamSettings) {
            Location spawnLocation = team.getSpawnLocation();
            locX[pos] = spawnLocation.getX();
            locY[pos] = spawnLocation.getY();
            locZ[pos] = spawnLocation.getZ();

            pos++;

            if (pos > division) {
                break;
            }
        }

        // Find the polygon center, note that it's not the mathematical center of the polygon,
        // Rather than a point which centers all coordinates:
        int centerX = 0;
        int centerY = 0;
        int centerZ = 0;
        for (int x = 0; x < pos; x++) {
            centerX += (locX[x] / division);
            centerY += (locY[x] / division);
            centerZ += (locZ[x] / division);
        }

        // Now let's find the furthest distance from the "center" to the egg shaped sphere
        // Surrounding the polygon, size x1.5 (for maximum logical area to wander...):
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
        for (int x = 0; x < pos; x++) {
            maxX = Math.max(maxX, 2 * Math.abs(centerX - locX[x]));
            maxY = Math.max(maxY, 2 * Math.abs(centerY - locY[x]));
            maxZ = Math.max(maxZ, 2 * Math.abs(centerZ - locZ[x]));
        }

        // CenterX,centerY,centerZ are the coordinates of the "event center".
        // So let's save those coordinates to check on the players:
        Location center = new Location(centerX, centerY, centerZ);
        eventBorder.setCenter(center);
        eventBorder.setOffset(Math.max(Math.max(maxX, maxY), maxZ));
    }

    private Spawn configureSpawn(NpcTemplate npcTemplate, SpawnLocation spawnLocation, String title) throws NoSuchMethodException, ClassNotFoundException {
        Spawn spawn = new Spawn(npcTemplate);

        spawn.setLoc(spawnLocation);

        spawn.setRespawnDelay(1);

        SpawnTable.getInstance().addSpawn(spawn, false);
        spawn.doSpawn(true);

        Npc npc = spawn.getNpc();
        npc.getStatus().setHp(999999999);
        npc.setInvul(true);
        npc.decayMe();
        npc.spawnMe(npc.getX(), npc.getY(), npc.getZ());
        npc.setTitle(title);

        return spawn;
    }

    @Override
    protected void unspawnNpcCustom() {

    }

    @Override
    protected void abortCustom() {

    }

    @Override
    protected void determineWinner() {

    }

    @Override
    public String configureMainPageContent(Player player) {
        int playerLevel = player.getStatus().getLevel();

        StringBuilder content = new StringBuilder();

        if (eventState != IN_PROGRESS && eventState != REGISTRATION) {
            content.append("<center>Wait till the admin/gm start the participation.</center>");
        } else if (eventState != IN_PROGRESS && teamMode == SHUFFLE && players.size() >= settings.getMaxPlayers()) {
            content.append("Currently participated: <font color=\"00FF00\">").append(players.size()).append(".</font><br>");
            content.append("Max players: <font color=\"00FF00\">").append(settings.getMaxPlayers()).append("</font><br><br>");
            content.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
        } else if (player.isCursedWeaponEquipped() && !JOIN_CURSED_WEAPON) {
            content.append("<font color=\"FFFF00\">You can't participate to this event with a cursed Weapon.</font><br>");
        } else {
            if (eventState == REGISTRATION
                    && playerLevel >= settings.getMinLevel() && playerLevel <= settings.getMaxLevel()) {
                EventPlayer eventPlayer = players.get(player.getObjectId());

                if (eventPlayer != null) {
                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("You participated already in team <font color=\"LEVEL\">").append(eventPlayer.getTeamSettings().getName()).append("</font><br><br>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center><font color=\"3366CC\">You participated already!</font></center><br><br>");
                    }

                    content.append("<center>Joined Players: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                    content.append("<center><font color=\"3366CC\">Wait till event start or remove your participation!</font><center>");
                    content.append("<center><button value=\"Remove\" action=\"bypass -h npc_%objectId%_")
                            .append(LEAVE.getBypass())
                            .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                } else {
                    content.append("<center><font color=\"3366CC\">You want to participate in the event?</font></center><br>");
                    content.append("<center><td width=\"200\">Min lvl: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                    content.append("<center><td width=\"200\">Max lvl: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                    content.append("<center><font color=\"3366CC\">Teams:</font></center><br>");

                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("<center><table border=\"0\">");
                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                    .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" joined)</td>");
                            content.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_%objectId%_")
                                    .append(JOIN_TEAM.getBypass())
                                    .append(" ")
                                    .append(team.getName())
                                    .append("\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                        }
                        content.append("</table></center>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center>");

                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                        }

                        content.append("</center><br>");

                        content.append("<center><button value=\"Join Event\" action=\"bypass -h npc_%objectId%_")
                                .append(JOIN_TEAM.getBypass())
                                .append(" eventShuffle\" width=85 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                        content.append("<center><font color=\"3366CC\">Teams will be randomly generated!</font></center><br>");
                        content.append("<center>Joined Players:</font> <font color=\"LEVEL\">").append(players.size()).append("</center></font><br>");
                        content.append("<center>Reward: <font color=\"LEVEL\">").append(settings.getReward().getAmount())
                                .append(" ").append(ItemData.getInstance().getTemplate(settings.getReward().getId()).getName()).append("</center></font>");
                    }
                }
            } else if (eventState == IN_PROGRESS) {
                content.append("<center>").append(settings.getEventName()).append(" match is in progress.</center>");
            } else if (playerLevel < settings.getMinLevel() || playerLevel > settings.getMaxLevel()) {
                content.append("Your lvl: <font color=\"00FF00\">").append(playerLevel).append("</font><br>");
                content.append("Min lvl: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font><br>");
                content.append("Max lvl: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font><br><br>");
                content.append("<font color=\"FFFF00\">You can't participate to this event.</font><br>");
            }
        }

        return content.toString();
    }

    @Override
    public void register(Player player, String teamName) {
        if (!checkPlayerBeforeRegistration(player) && !checkTeamBeforeRegistration(player, teamName)) {
            return;
        }

        TeamSetting team = null;

        if (teamMode == NO || teamMode == BALANCE) {
            team = findTeam(teamName);
            team.addPlayer();
        }

        players.put(player.getObjectId(), new CtfEventPlayer(player, team));

        sendPlayerMessage(player, "Вы успешно зарегистрировались на ивент.");
    }

    @Override
    public void revive(Player player, Player playerKiller) {
        player.sendMessage(String.format(
                "Вы будете воскрешены и перемещены к флагу команды через %d %s!",
                EventConfig.CTF.DELAY_BEFORE_REVIVE,
                declensionWords(EventConfig.CTF.DELAY_BEFORE_REVIVE, secondWords)
        ));

        CtfEventPlayer eventPlayer = (CtfEventPlayer) player.getEventPlayer();

        if (eventPlayer.isHasFlag()) {
            restoreFlagOnPlayerDie(eventPlayer);
        }

        player.broadcastUserInfo();

        ThreadPool.schedule(() -> {
            player.teleToLocation(eventPlayer.getTeamSettings().getSpawnLocation());
            waiter(1); // q
            player.doRevive();
        }, TimeUnit.SECONDS.toMillis(EventConfig.CTF.DELAY_BEFORE_REVIVE));
    }

    private void removeFlagFromPlayer(CtfEventPlayer eventPlayer) {
        int flagItemId = eventPlayer.getTeamSettings().getFlag().getItemId();

        Player player = eventPlayer.getPlayer();

        if (!eventPlayer.isHasFlag()) {
            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);
            return;
        }

        eventPlayer.setEnemyFlag(null);

        ItemInstance weaponEquipped = player.getActiveWeaponInstance();

        // Get your weapon back now ...
        if (weaponEquipped != null) {
            ItemInstance[] unequipped = player.getInventory().unequipItemInBodySlotAndRecord(weaponEquipped);

            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);

            InventoryUpdate inventoryUpdate = new InventoryUpdate();

            for (ItemInstance element : unequipped) {
                inventoryUpdate.addModifiedItem(element);
            }

            player.sendPacket(inventoryUpdate);
        } else {
            player.getInventory().destroyItemByItemId("", flagItemId, 1, player, null);
        }

        player.sendPacket(new ItemList(player, true)); // Get your weapon back now ...
        player.getAttack().stop();
        player.broadcastUserInfo();
    }
}
