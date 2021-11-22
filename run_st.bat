@ECHO OFF
javac -d "bin/ST" Keys/*.java ST/*.java
ECHO ST Compiled and About to Start ...
java -cp "bin/ST" ST.Server