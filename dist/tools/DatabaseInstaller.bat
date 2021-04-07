@echo off

REM MYSQL BIN PATH
set mysqlBinPath=F:\Program Files\MariaDB 10.5\bin
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
echo Installing empty character-related tables.
%mysqlPath% -h %loginServerHost% -u %loginServerUser% --password=%loginServerPassword% -D %loginServerDatabase% < ./sql/login/account_premium.sql
%mysqlPath% -h %loginServerHost% -u %loginServerUser% --password=%loginServerPassword% -D %loginServerDatabase% < ./sql/login/accounts.sql
%mysqlPath% -h %loginServerHost% -u %loginServerUser% --password=%loginServerPassword% -D %loginServerDatabase% < ../sql/gameservers.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/auctions.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/augmentations.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_favorite.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_forum.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_mail.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_post.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bbs_topic.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/bookmarks.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/buffer_schemes.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/buylists.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_doorupgrade.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_manor_procure.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_manor_production.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_trapupgrade.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_data.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_friends.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_hennas.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_macroses.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_memo.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_quests.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_raid_points.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_recipebook.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_recommends.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_shortcuts.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_skills.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_skills_save.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/character_subclasses.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/characters.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_data.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_privs.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_skills.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_subpledges.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clan_wars.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_functions.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_siege_attackers.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/clanhall_siege_guards.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/cursed_weapons.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/fishing_championship.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/games.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/grandboss_list.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/heroes_diary.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/heroes.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/items.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/items_on_ground.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mdt_bets.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mdt_history.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/mods_wedding.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/offline_trade.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_data.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_fights.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_nobles_eom.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/olympiad_nobles.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/petition.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/petition_message.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/pets.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/rainbowsprings_attacker_list.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/server_memo.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs_festival.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/seven_signs_status.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/siege_clans.sql
echo Done.

echo.
echo Installing server tables.
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/castle_siege_guards.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/grandboss_data.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/raidboss_spawnlist.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/random_spawn.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/random_spawn_loc.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/spawnlist_4s.sql
%mysqlPath% -h %gameServerHost% -u %gameServerUser% --password=%gameServerPassword% -D %gameDatabase% < ./sql/game/spawnlist.sql
echo Done.

echo.
echo Script execution finished.
pause
