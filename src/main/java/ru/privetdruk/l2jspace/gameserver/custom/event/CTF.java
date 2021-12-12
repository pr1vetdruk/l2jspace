package ru.privetdruk.l2jspace.gameserver.custom.event;

import ru.privetdruk.l2jspace.common.pool.ConnectionPool;
import ru.privetdruk.l2jspace.common.pool.ThreadPool;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.common.util.StringUtil;
import ru.privetdruk.l2jspace.config.custom.EventConfig;
import ru.privetdruk.l2jspace.gameserver.custom.engine.EventEngine;
import ru.privetdruk.l2jspace.gameserver.custom.model.NpcInfoShort;
import ru.privetdruk.l2jspace.gameserver.custom.model.enums.SocialActionEnum;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.*;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfPlayer;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.CtfTeamSetting;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Flag;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.ctf.Throne;
import ru.privetdruk.l2jspace.gameserver.custom.service.EventService;
import ru.privetdruk.l2jspace.gameserver.data.sql.SpawnTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.ItemData;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.enums.TeamAura;
import ru.privetdruk.l2jspace.gameserver.enums.SayType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.container.player.RadarOnPlayer;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;
import ru.privetdruk.l2jspace.gameserver.model.location.Location;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.model.spawn.Spawn;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.privetdruk.l2jspace.common.util.StringUtil.declensionWords;
import static ru.privetdruk.l2jspace.common.util.StringUtil.SECOND_WORDS;
import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Mount.Wyvern.WYVERN_BREATH;
import static ru.privetdruk.l2jspace.gameserver.custom.model.SkillEnum.Prophet.MAGIC_BARRIER;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.JOIN_TEAM;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventBypass.LEAVE;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventState.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.EventTeamType.*;
import static ru.privetdruk.l2jspace.gameserver.custom.model.event.ResultEvent.*;

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
    public boolean allowPotions() {
        return EventConfig.CTF.ALLOW_POTIONS;
    }

    @Override
    protected void startEventCustom() {
        allowPlayersToMove();
        sitPlayers();
    }

    @Override
    protected void announceStart() {
        announceCritical("Вперед!");
        announceCritical("Если кто-то сбежит с ивента, прихватив с собой флаг, то просто нажмите на любой имеющийся флаг, либо трон под ним, чтобы вернуть беглеца и флаг на место.");
    }

    @Override
    public void loadData(int eventId) {
        try (Connection connection = ConnectionPool.getConnection()) {
            settings = EventService.getInstance().findEventSetting(eventId, eventType);

            if (settings == null) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM event_ctf_team_setting WHERE event_id = ?");
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ctfTeamSettings.add(
                        new CtfTeamSetting(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("name_color"),
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
            logError("loadData", e);
        }
    }

    public void restoreFlags() {
        try {
            int numberFlagsTaken = 0;

            for (EventPlayer eventPlayer : players.values()) {
                if (numberFlagsTaken == ctfTeamSettings.size()) {
                    break;
                }

                CtfPlayer ctfPlayer = (CtfPlayer) eventPlayer;

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

                        CtfTeamSetting enemyTeam = ((CtfPlayer) eventPlayer).getEnemyFlag();

                        if (enemyTeam.getFlag().isTaken()) {
                            enemyTeam.getFlag().setTaken(false);

                            spawnFlag(enemyTeam);

                            announceCritical("Флаг команды " + enemyTeam.getName() + " был возвращен на место!");
                        }

                        removeFlagFromPlayer(ctfPlayer);

                        Location spawnLocation = team.getSpawnLocation();

                        player.teleToLocation(spawnLocation);

                        sendPlayerMessage(player, "Вы возвращены на место респавна вашей команды.");
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
            logError("restoreFlags", e);
        }
    }

    public void restoreFlagOnPlayerDie(CtfPlayer eventPlayer) {
        CtfTeamSetting team = eventPlayer.getEnemyFlag();

        team.getFlag().setTaken(false);
        spawnFlag(team);
        removeFlagFromPlayer(eventPlayer);

        announceCritical("Флаг команды " + team.getName() + " возвращен на базу!");
    }

    public void processInFlagRange(CtfPlayer eventPlayer) {
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

    private void pickUpFlag(CtfPlayer eventPlayer, CtfTeamSetting enemyTeam) {
        enemyTeam.getFlag().setTaken(true);

        unspawn(enemyTeam.getFlag().getSpawn());
        addFlagToPlayer(eventPlayer, enemyTeam);
        displayRadar(eventPlayer.getPlayer(), enemyTeam);

        announceCritical(format("Игрок %s забрал флаг команды %s!", eventPlayer.getPlayer().getName(), enemyTeam.getName()));
    }

    private void returnFlag(CtfPlayer eventPlayer) {
        CtfTeamSetting enemyTeam = eventPlayer.getEnemyFlag();

        enemyTeam.getFlag().setTaken(false);
        spawnFlag(enemyTeam);

        // Remove the flag from this player
        Player player = eventPlayer.getPlayer();
        player.broadcastPacket(new SocialAction(player, SocialActionEnum.AMAZING_GLOW.getId()));
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
                StringUtil.declensionWords(team.getPoints(), StringUtil.POINT_WORDS)
        ));
    }

    private void displayRadar(Player targetPlayer, CtfTeamSetting teamOurFlag) {
        try {
            players.values().stream()
                    .filter(eventPlayer -> eventPlayer.getTeamSettings() == teamOurFlag
                            && eventPlayer.getPlayer().isOnline())
                    .forEach(eventPlayer -> {
                        CtfPlayer ctfPlayer = (CtfPlayer) eventPlayer;
                        Player player = ctfPlayer.getPlayer();

                        sendPlayerMessage(player, "Игрок " + targetPlayer.getName() + " взял ваш флаг!");

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

    public void addFlagToPlayer(CtfPlayer eventPlayer, CtfTeamSetting team) {
        Player player = eventPlayer.getPlayer();
        int flagItemId = team.getFlag().getItemId();

        ItemInstance activeWeapon = player.getActiveWeaponInstance();

        if (activeWeapon != null) {
            player.getInventory().unequipItemInBodySlotAndRecord(Item.Slot.LEFT_RIGHT_HAND);
        }

        // Add the flag in his hands
        eventPlayer.setEnemyFlag(team);
        player.getInventory().equipItem(ItemInstance.create(flagItemId, 1, player, null));
        player.broadcastPacket(new SocialAction(player, SocialActionEnum.AMAZING_GLOW.getId()));
        player.broadcastUserInfo();
        player.sendPacket(new CreatureSay(player.getId(), SayType.PARTYROOM_COMMANDER, "Event Manager", "Отлично! Теперь отнеси флаг на свою базу!"));
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
            Spawn flagSpawn = configureSpawn(flagTemplate, flagNpc.getSpawnLocation(), team.getName() + "'s Flag");
            team.getFlag().setSpawn(flagSpawn);
        } catch (Exception e) {
            logError("spawnFlag", e);
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
            player.setTeamAura(TeamAura.NONE);
        }

        player.broadcastUserInfo();

        if (teamMode == NO || teamMode == BALANCE) {
            eventPlayer.getTeamSettings().removePlayer();
        }

        removeFlagFromPlayer((CtfPlayer) eventPlayer);
    }

    @Override
    protected void updatePlayerEventDataCustom(EventPlayer e) {
        CtfPlayer eventPlayer = (CtfPlayer) e;
        Player player = eventPlayer.getPlayer();
        TeamSetting team = eventPlayer.getTeamSettings();

        player.getAppearance().setNameColor(team.getColor());
        if (player.getKarma() > 0) {
            player.setKarma(0);
        }

        if (EventConfig.CTF.AURA && teamSettings.size() == 2) {
            player.setTeamAura(TeamAura.fromId(teamSettings.indexOf(team) + 1));
        }

        if (player.isMounted()) {
            if (player.isFlying()) {
                player.removeSkill(WYVERN_BREATH.getId(), true);
            }

            player.broadcastPacket(new Ride(player.getId(), Ride.ACTION_DISMOUNT, 0));
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
            } catch (Exception e) {
                logError("spawnOtherNpc", e);
            }
        });

        calculateOutSide(); // Sets event boundaries so players don't run with the flag.
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
        try {
            for (CtfTeamSetting team : ctfTeamSettings) {
                unspawn(team.getThrone().getSpawn());
                unspawn(team.getFlag().getSpawn());
            }
        } catch (Exception e) {
            logError("unspawnNpcCustom", e);
        }
    }

    @Override
    protected void cancelEventCustom() {
    }

    @Override
    protected void determineWinner() {
        Map.Entry<Integer, List<CtfTeamSetting>> winningTeamEntry = ctfTeamSettings.stream()
                .filter(team -> team.getPoints() > 0)
                .collect(
                        Collectors.groupingBy(
                                CtfTeamSetting::getPoints,
                                TreeMap::new,
                                Collectors.toList()
                        )
                ).lastEntry();

        if (winningTeamEntry != null) {
            List<CtfTeamSetting> winningTeamList = winningTeamEntry.getValue();

            for (EventPlayer eventPlayer : players.values()) {
                boolean isWinner = winningTeamList.contains((CtfTeamSetting) eventPlayer.getTeamSettings());
                boolean isTie = winningTeamEntry.getValue().size() > 1;

                playAnimation(eventPlayer.getPlayer(), isWinner);
                giveReward(eventPlayer.getPlayer(), isTie ? TIE : isWinner ? WON : LOST);
            }


            if (EventConfig.CTF.ANNOUNCE_TEAM_STATS) {
                announceCritical("Статистика:");
                for (CtfTeamSetting team : ctfTeamSettings) {
                    announceCritical("Команда: " + team.getName() + " - принесла флагов: " + team.getPoints());
                }
            }

            CtfTeamSetting winningTeam = winningTeamList.get(0);

            if (winningTeamList.size() == 1) {
                announceCritical("Поздравляем! Победила команда - " + winningTeam.getName() + "!");
            } else {
                announceCritical("Ивент завершился в ничью!");
            }
        } else {
            announceCritical("Ни одной команде не удалось принести флаг на свою базу!");
        }
    }

    @Override
    public void register(Player player) {
    }

    @Override
    public void addDisconnectedPlayer(Player player) {
        switch (eventState) {
            case TELEPORTATION, PREPARE_FOR_START, IN_PROGRESS -> {
                EventPlayer eventPlayer = allPlayers.get(player.getId());

                if (eventPlayer != null) {
                    eventPlayer.setPlayer(player);
                    player.setEventPlayer(eventPlayer);
                    players.put(player.getId(), eventPlayer); // adding new objectId to vector

                    updatePlayerEventDataCustom(eventPlayer);
                    teleport(eventPlayer);
                }
            }
        }
    }

    private void giveReward(Player player, ResultEvent result) {
        if (player == null || !player.isOnline() || !player.isEventPlayer()) {
            return;
        }

        if (result == WON) {
            settings.getRewards().forEach(reward ->
                    player.addItem(
                            settings.getEventName(),
                            reward.getId(),
                            reward.getAmount(),
                            player,
                            true
                    )
            );

            player.sendPacket(new CreatureSay(player.getId(), SayType.PARTYROOM_COMMANDER, settings.getEventName(), result.name()));
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    @Override
    public String configureMainPageContent(Player player) {
        int playerLevel = player.getStatus().getLevel();

        StringBuilder content = new StringBuilder();

        if (eventState != IN_PROGRESS && teamMode == SHUFFLE && players.size() >= settings.getMaxPlayers()) {
            content.append("Участников: <font color=\"00FF00\">").append(players.size()).append(".</font><br>");
            content.append("Максимум игроков: <font color=\"00FF00\">").append(settings.getMaxPlayers()).append("</font><br><br>");
            content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте.</font><br>");
        } else if (player.isCursedWeaponEquipped() && !JOIN_CURSED_WEAPON) {
            content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте с проклятым оружием.</font><br>");
        } else {
            if (eventState == REGISTRATION
                    && playerLevel >= settings.getMinLevel() && playerLevel <= settings.getMaxLevel()) {
                EventPlayer eventPlayer = players.get(player.getId());

                if (eventPlayer != null) {
                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("Вы уже участвуете в команде <font color=\"LEVEL\">").append(eventPlayer.getTeamSettings().getName()).append("</font><br><br>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center><font color=\"3366CC\">Вы уже принимаете участие!</font></center><br><br>");
                    }

                    content.append("<center>Участников: <font color=\"00FF00\">").append(players.size()).append("</font></center><br>");
                    content.append("<center><font color=\"3366CC\">Дождитесь начала ивента или откажитесь от участия!</font><center>");
                    content.append("<center><button value=\"Покинуть\" action=\"bypass -h npc_%objectId%_")
                            .append(LEAVE.getBypass())
                            .append("\" width=\"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                } else {
                    content.append("<center><font color=\"3366CC\">Вы хотите принять участие в ивенте?</font></center><br>");
                    content.append("<center><td width=\"200\">Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font></center></td><br>");
                    content.append("<center><td width=\"200\">Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font></center></td><br><br>");
                    content.append("<center><font color=\"3366CC\">Команды:</font></center><br>");

                    if (teamMode == NO || teamMode == BALANCE) {
                        content.append("<center><table border=\"0\">");
                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">")
                                    .append(team.getName()).append("</font>&nbsp;(").append(team.getPlayers()).append(" участников)</td>");
                            content.append("<center><td width=\"90\"><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                                    .append(JOIN_TEAM.getBypass())
                                    .append(" ")
                                    .append(team.getName())
                                    .append("\" width=\"60\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center></td></tr>");
                        }
                        content.append("</table></center>");
                    } else if (teamMode == SHUFFLE) {
                        content.append("<center>");

                        for (TeamSetting team : teamSettings) {
                            content.append("<tr><td width=\"100\"><font color=\"LEVEL\">").append(team.getName()).append("</font> &nbsp;</td>");
                        }

                        content.append("</center><br>");

                        content.append("<center><button value=\"Участвовать\" action=\"bypass -h npc_%objectId%_")
                                .append(JOIN_TEAM.getBypass())
                                .append(" eventShuffle\" \"90\" height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></center>");
                        content.append("<center><font color=\"3366CC\">Команды будут выбраны случайным образом!</font></center><br>");
                        content.append("<center>Участников:</font> <font color=\"LEVEL\">").append(players.size()).append("</center></font><br>");
                        content.append("<center>Награда:</center>");

                        settings.getRewards().forEach(reward -> {
                            content.append("<center><font color=\"LEVEL\">").append(reward.getAmount())
                                    .append(" ").append(ItemData.getInstance().getTemplate(reward.getId()).getName()).append("</center></font>");
                        });
                    }
                }
            } else if (eventState == IN_PROGRESS) {
                content.append("<center>К сожалению ивент ").append(settings.getEventName()).append(" уже начался.</center>");
            } else if (playerLevel < settings.getMinLevel() || playerLevel > settings.getMaxLevel()) {
                content.append("Ваш уровень: <font color=\"00FF00\">").append(playerLevel).append("</font><br>");
                content.append("Минимальный уровень: <font color=\"00FF00\">").append(settings.getMinLevel()).append("</font><br>");
                content.append("Максимальный уровень: <font color=\"00FF00\">").append(settings.getMaxLevel()).append("</font><br><br>");
                content.append("<font color=\"FFFF00\">Вы не можете участвовать в этом ивенте.</font><br>");
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

        players.put(player.getId(), new CtfPlayer(player, team));

        sendPlayerMessage(player, "Вы успешно зарегистрировались на ивент.");
    }

    @Override
    public void doDie(Player player, Player playerKiller) {
        sendPlayerMessage(player, String.format(
                "Вы будете воскрешены и перемещены к флагу команды через %d %s!",
                EventConfig.CTF.DELAY_BEFORE_REVIVE,
                declensionWords(EventConfig.CTF.DELAY_BEFORE_REVIVE, SECOND_WORDS)
        ));

        CtfPlayer eventPlayer = (CtfPlayer) player.getEventPlayer();

        if (eventPlayer.isHasFlag()) {
            restoreFlagOnPlayerDie(eventPlayer);
        }

        player.broadcastUserInfo();

        ThreadPool.schedule(() -> {
            player.doRevive();
            player.teleToLocation(eventPlayer.getTeamSettings().getSpawnLocation());
        }, TimeUnit.SECONDS.toMillis(EventConfig.CTF.DELAY_BEFORE_REVIVE));
    }

    @Override
    public boolean isAllowedTeleportAfterDeath() {
        return false;
    }

    @Override
    protected void announceRewardsAfter() {

    }

    private void removeFlagFromPlayer(CtfPlayer eventPlayer) {
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
