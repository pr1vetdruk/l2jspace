@echo off
title L2jSpace game server registration console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; ru.privetdruk.l2jspace.tool.GameServerRegister
@pause