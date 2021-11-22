@ECHO OFF
javac -d "bin/SI" Keys/*.java SI/*.java
ECHO SI Compiled and About to Start ...
java -cp "bin/SI" SI.Server