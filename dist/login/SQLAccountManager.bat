@echo off
title L2jSpace account manager console
@java -Djava.util.logging.config.file=config/console.cfg -cp ../lib/l2jspace.jar ru.privetdruk.l2jspace.tools.SQLAccountManager
@pause
