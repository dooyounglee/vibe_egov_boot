@echo off
rem Apply the project JDK to the current cmd session.  usage: env.cmd
set /p "JAVA_HOME="<"%~dp0.mvn\jdk-home"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME=%JAVA_HOME%
