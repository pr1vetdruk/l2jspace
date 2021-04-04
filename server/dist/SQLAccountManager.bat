@echo off
title L2jSpace account manager console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; ru.privetdruk.l2jspace.tool.SQLAccountManager
@pause
