@echo off
rem Project JDK wrapper: runs java from the path in .mvn\jdk-home
setlocal
set /p "PROJECT_JAVA_HOME="<"%~dp0.mvn\jdk-home"
"%PROJECT_JAVA_HOME%\bin\java.exe" %*
exit /b %ERRORLEVEL%
