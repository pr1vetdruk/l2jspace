@echo off
title l2jspace loginserver console
:start
java -Xmx32m -cp ../lib/l2jspace.jar ru.privetdruk.l2jspace.loginserver.LoginServer
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
