@ECHO OFF
javac -d "bin/Client" Client/*.java
ECHO Client Compiled and About to Start ...
java -cp "bin/Client" Client.Client