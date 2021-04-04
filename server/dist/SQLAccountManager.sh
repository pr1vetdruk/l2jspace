#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*:l2jserver.jar:mariadb-java-client-2.5.2 ru.privetdruk.l2jspace.tool.SQLAccountManager
