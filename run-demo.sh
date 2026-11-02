#!/bin/sh
# Auto-types the sample clinic data so you can present without typing.
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -d out src/medialert/*.java || exit 1
rm -rf data
java -Dfile.encoding=UTF-8 -cp out medialert.Main --demo < sample_input.txt
