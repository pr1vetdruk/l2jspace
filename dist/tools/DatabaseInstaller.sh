trap finish 2

configure() {
  # Login server
  LSDBHOST="localhost"
  LSDB="l2jspace-login-server"
  LSUSER="root"
  LSPASS="root"

  # Game server
  GSDBHOST="localhost"
  GSDB="l2jspace-game-server"
  GSUSER="root"
  GSPASS="root"

  MYSQLDUMPPATH=$(which mysqldump 2>/dev/null)
  MYSQLPATH=$(which mysql 2>/dev/null)
  if [ $? -ne 0 ]; then
    echo "We were unable to find MySQL binaries on your path"
    while :; do
      echo -ne "\nPlease enter MySQL binaries directory (no trailing slash): "
      read MYSQLBINPATH
      if [ -e "$MYSQLBINPATH" ] && [ -d "$MYSQLBINPATH" ] && [ -e "$MYSQLBINPATH/mysqldump" ] && [ -e "$MYSQLBINPATH/mysql" ]; then
        MYSQLDUMPPATH="$MYSQLBINPATH/mysqldump"
        MYSQLPATH="$MYSQLBINPATH/mysql"
        break
      else
        echo "The data you entered is invalid. Please verify and try again."
        exit 1
      fi
    done
  fi
  MYL="$MYSQLPATH -h $LSDBHOST -u $LSUSER --password=$LSPASS -D $LSDB"
  MYG="$MYSQLPATH -h $GSDBHOST -u $GSUSER --password=$GSPASS -D $GSDB"

  echo "Install database y/n? "
  read PROMPT
  case "$PROMPT" in
  "y" | "Y")
    fullInstall
    ;;
  "n" | "N") finish ;;
  *) configure ;;
  esac
}

fullInstall() {
  echo "Deleting all tables for new content."
  $MYG <./sql/drop-table.sql &>/dev/null
  $MYL <./sql/drop-table.sql &>/dev/null
  echo "Done."

  echo "Installing server tables."
  $MYL <./sql/login/accounts.sql &>/dev/null
  $MYL <./sql/login/gameservers.sql &>/dev/null
  $MYG <./sql/game/account_premium.sql &>/dev/null
  $MYG <./sql/game/auctions.sql &>/dev/null
  $MYG <./sql/game/augmentations.sql &>/dev/null
  $MYG <./sql/game/bbs_favorite.sql &>/dev/null
  $MYG <./sql/game/bbs_forum.sql &>/dev/null
  $MYG <./sql/game/bbs_mail.sql &>/dev/null
  $MYG <./sql/game/bbs_post.sql &>/dev/null
  $MYG <./sql/game/bbs_topic.sql &>/dev/null
  $MYG <./sql/game/bookmarks.sql &>/dev/null
  $MYG <./sql/game/buffer_schemes.sql &>/dev/null
  $MYG <./sql/game/buylists.sql &>/dev/null
  $MYG <./sql/game/castle.sql &>/dev/null
  $MYG <./sql/game/castle_doorupgrade.sql &>/dev/null
  $MYG <./sql/game/castle_manor_procure.sql &>/dev/null
  $MYG <./sql/game/castle_manor_production.sql &>/dev/null
  $MYG <./sql/game/castle_siege_guards.sql &>/dev/null
  $MYG <./sql/game/castle_trapupgrade.sql &>/dev/null
  $MYG <./sql/game/character_data.sql &>/dev/null
  $MYG <./sql/game/character_friends.sql &>/dev/null
  $MYG <./sql/game/character_hennas.sql &>/dev/null
  $MYG <./sql/game/character_macroses.sql &>/dev/null
  $MYG <./sql/game/character_memo.sql &>/dev/null
  $MYG <./sql/game/character_quests.sql &>/dev/null
  $MYG <./sql/game/character_raid_points.sql &>/dev/null
  $MYG <./sql/game/character_recipebook.sql &>/dev/null
  $MYG <./sql/game/character_recommends.sql &>/dev/null
  $MYG <./sql/game/character_shortcuts.sql &>/dev/null
  $MYG <./sql/game/character_skills.sql &>/dev/null
  $MYG <./sql/game/character_skills_save.sql &>/dev/null
  $MYG <./sql/game/character_subclasses.sql &>/dev/null
  $MYG <./sql/game/characters.sql &>/dev/null
  $MYG <./sql/game/clan_data.sql &>/dev/null
  $MYG <./sql/game/clan_privs.sql &>/dev/null
  $MYG <./sql/game/clan_skills.sql &>/dev/null
  $MYG <./sql/game/clan_subpledges.sql &>/dev/null
  $MYG <./sql/game/clan_wars.sql &>/dev/null
  $MYG <./sql/game/clanhall.sql &>/dev/null
  $MYG <./sql/game/clanhall_functions.sql &>/dev/null
  $MYG <./sql/game/clanhall_siege_attackers.sql &>/dev/null
  $MYG <./sql/game/clanhall_siege_guards.sql &>/dev/null
  $MYG <./sql/game/cursed_weapons.sql &>/dev/null
  $MYG <./sql/game/fishing_championship.sql &>/dev/null
  $MYG <./sql/game/games.sql &>/dev/null
  $MYG <./sql/game/grandboss_data.sql &>/dev/null
  $MYG <./sql/game/grandboss_list.sql &>/dev/null
  $MYG <./sql/game/heroes_diary.sql &>/dev/null
  $MYG <./sql/game/heroes.sql &>/dev/null
  $MYG <./sql/game/items.sql &>/dev/null
  $MYG <./sql/game/items_on_ground.sql &>/dev/null
  $MYG <./sql/game/mdt_bets.sql &>/dev/null
  $MYG <./sql/game/mdt_history.sql &>/dev/null
  $MYG <./sql/game/mods_wedding.sql &>/dev/null
  $MYG <./sql/game/offline_trade.sql &>/dev/null
  $MYG <./sql/game/olympiad_data.sql &>/dev/null
  $MYG <./sql/game/olympiad_fights.sql &>/dev/null
  $MYG <./sql/game/olympiad_nobles_eom.sql &>/dev/null
  $MYG <./sql/game/olympiad_nobles.sql &>/dev/null
  $MYG <./sql/game/petition.sql &>/dev/null
  $MYG <./sql/game/petition_message.sql &>/dev/null
  $MYG <./sql/game/pets.sql &>/dev/null
  $MYG <./sql/game/raidboss_spawnlist.sql &>/dev/null
  $MYG <./sql/game/rainbowsprings_attacker_list.sql &>/dev/null
  $MYG <./sql/game/random_spawn.sql &>/dev/null
  $MYG <./sql/game/random_spawn_loc.sql &>/dev/null
  $MYG <./sql/game/server_memo.sql &>/dev/null
  $MYG <./sql/game/seven_signs.sql &>/dev/null
  $MYG <./sql/game/seven_signs_festival.sql &>/dev/null
  $MYG <./sql/game/seven_signs_status.sql &>/dev/null
  $MYG <./sql/game/siege_clans.sql &>/dev/null
  $MYG <./sql/game/spawnlist_4s.sql &>/dev/null
  $MYG <./sql/game/spawnlist.sql &>/dev/null
  echo "Done."
}

finish() {
  echo ""
  echo "Script execution finished."
  exit 0
}

clear
configure
