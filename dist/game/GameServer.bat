@echo off
title L2jSpace game server console
:start
REM -------------------------------------
REM Default parameters for a basic server.
java -Xmx2G -cp ../lib/l2jspace.jar ru.privetdruk.l2jspace.gameserver.GameServer
REM -------------------------------------
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin have restarted, please wait.
echo.
goto start
:error
echo.
echo Server have terminated abnormally.
echo.
:end
echo.
echo Server terminated.
echo.
pause
