@echo on
echo Checking Gradle wrapper...
dir gradlew.bat
echo.

echo Testing Gradle version...
call gradlew.bat -v
echo.

echo Testing Gradle status...
call gradlew.bat --status
pause
