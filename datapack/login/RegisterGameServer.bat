@echo off
title L2jSpace game server registration console
@java -Djava.util.logging.config.file=config/console.cfg -cp ../lib/l2jspace.jar ru.privetdruk.l2jspace.tools.GameServerRegister
@pause