@echo off
rem Auto-types the sample clinic data so you can present without typing.
cd /d "%~dp0"
chcp 65001 >nul
if not exist out mkdir out
javac -encoding UTF-8 -d out src\medialert\*.java || (pause & exit /b 1)
if exist data rmdir /s /q data
java -Dfile.encoding=UTF-8 -cp out medialert.Main --demo < sample_input.txt
pause
