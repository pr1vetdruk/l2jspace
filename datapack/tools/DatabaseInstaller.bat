@echo off

REM MYSQL BIN PATH
set mysqlBinPath=E:\Program Files\MariaDB 10.5\bin
set mysqldumpPath="%mysqlBinPath%\mysqldump"
set mysqlPath="%mysqlBinPath%\mysql"

REM LOGIN SERVER
set loginServerUser=root
set loginServerPassword=root
set loginServerDatabase=l2jspace-login-server
set loginServerHost=localhost

REM GAME SERVER
set gameServerUser=root
set gameServerPassword=root
set gameDatabase=l2jspace-game-server
set gameServerHost=localhost

:askActionType
set action=x
set /p action="Install database y/n? "
if /i %action%==y goto fullInstall
if /i %action%==n goto end
goto askActionType

:fullInstall
echo.
echo Deleting server tables.
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/drop-table.sql
echo Done.

echo.
echo Installing tables:
echo - accounts
%mysqlPath% -h %loginServerHost% -u %loginServerUser% --password=%loginServerPassword% -D %loginServerDatabase% < ./sql/login/accounts.sql
echo - gameservers
%mysqlPath% -h %loginServerHost% -u %loginServerUser% --password=%loginServerPassword% -D %loginServerDatabase% < ./sql/login/gameservers.sql
echo - account_premium
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/account_premium.sql
echo - auctions
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/auctions.sql
echo - augmentations
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/augmentations.sql
echo - bbs_favorite
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_favorite.sql
echo - bbs_forum
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_forum.sql
echo - bbs_mail
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_mail.sql
echo - bbs_post
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_post.sql
echo - bbs_topic
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_topic.sql
echo - bookmarks
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bookmarks.sql
echo - buffer_schemes
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/buffer_schemes.sql
echo - buylists
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/buylists.sql
echo - castle_doorupgrade
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_doorupgrade.sql
echo - castle_functions
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_functions.sql
echo - castle_manor_procure
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_manor_procure.sql
echo - castle_manor_production
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_manor_production.sql
echo - castle_trapupgrade
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_trapupgrade.sql
echo - character_data
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_data.sql
echo - character_friends
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_friends.sql
echo - character_hennas
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_hennas.sql
echo - character_macroses
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_macroses.sql
echo - character_memo
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_memo.sql
echo - character_quests
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_quests.sql
echo - character_raid_points
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_raid_points.sql
echo - character_recipebook
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_recipebook.sql
echo - character_recommends
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_recommends.sql
echo - character_shortcuts
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_shortcuts.sql
echo - character_skills
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_skills.sql
echo - character_skills_save
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_skills_save.sql
echo - character_subclasses
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_subclasses.sql
echo - characters
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/characters.sql
echo - clan_data
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_data.sql
echo - clan_privs
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_privs.sql
echo - clan_skills
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_skills.sql
echo - clan_subpledges
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_subpledges.sql
echo - clan_wars
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_wars.sql
echo - clanhall
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall.sql
echo - clanhall_functions
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_functions.sql
echo - clanhall_siege_attackers
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_siege_attackers.sql
echo - clanhall_siege_guards
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_siege_guards.sql
echo - cursed_weapons
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/cursed_weapons.sql
echo - fishing_championship
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/fishing_championship.sql
echo - games
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/games.sql
echo - grandboss_list
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/grandboss_list.sql
echo - heroes_diary
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/heroes_diary.sql
echo - heroes
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/heroes.sql
echo - items
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/items.sql
echo - items_on_ground
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/items_on_ground.sql
echo - mdt_bets
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mdt_bets.sql
echo - mdt_history
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mdt_history.sql
echo - mods_wedding
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mods_wedding.sql
echo - offline_trade
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/offline_trade.sql
echo - olympiad_data
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_data.sql
echo - olympiad_fights
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_fights.sql
echo - olympiad_nobles_eom
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_nobles_eom.sql
echo - olympiad_nobles
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_nobles.sql
echo - petition
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/petition.sql
echo - petition_message
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/petition_message.sql
echo - pets
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/pets.sql
echo - rainbowsprings_attacker_list
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/rainbowsprings_attacker_list.sql
echo - server_memo
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/server_memo.sql
echo - seven_signs
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs.sql
echo - seven_signs_festival
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs_festival.sql
echo - seven_signs_status
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs_status.sql
echo - siege_clans
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/siege_clans.sql
echo - castle
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle.sql
echo - castle_siege_guards
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_siege_guards.sql
echo - grandboss_data
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/grandboss_data.sql
echo - raidboss_spawnlist
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/raidboss_spawnlist.sql
echo - random_spawn
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/random_spawn.sql
echo - random_spawn_loc
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/random_spawn_loc.sql
echo - spawnlist_4s
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/spawnlist_4s.sql
echo - spawnlist
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/spawnlist.sql
echo Done.
echo.
echo Installing custom tables:
echo - event
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/custom/event/event.sql
echo - event_ctf_team_setting
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/custom/event/event_ctf_team_setting.sql
echo - event_last_emperor_team_setting
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/custom/event/event_last_emperor_team_setting.sql
echo Done.
echo.
echo Script execution finished.
pause
