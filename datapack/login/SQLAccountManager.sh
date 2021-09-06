#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ../lib/*:l2jspace.jar:mariadb-java-client-2.5.2 ru.privetdruk.l2jspace.tools.SQLAccountManager
