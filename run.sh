#!/bin/sh
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -d out src/medialert/*.java || { echo "Compilation failed. Make sure the Java JDK is installed."; exit 1; }
java -Dfile.encoding=UTF-8 -cp out medialert.Main "$@"
