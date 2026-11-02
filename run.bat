@echo off
cd /d "%~dp0"
title MediAlert

rem --- Turn on colours in the classic Windows console (one-time, Windows 10+).
rem     Windows Terminal, VS Code and IntelliJ already support colours.
reg query HKCU\Console /v VirtualTerminalLevel >nul 2>&1
if errorlevel 1 (
    reg add HKCU\Console /v VirtualTerminalLevel /t REG_DWORD /d 1 /f >nul
    start "MediAlert" cmd /c ""%~f0" %*"
    exit /b
)

rem --- UTF-8 so the boxes and logo draw correctly
chcp 65001 >nul

if not exist out mkdir out
javac -encoding UTF-8 -d out src\medialert\*.java
if errorlevel 1 (
    echo Compilation failed. Make sure the Java JDK is installed.
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -cp out medialert.Main %*
pause
