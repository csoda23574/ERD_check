@echo on
echo [Step 1] Current working directory:
cd
echo.

echo [Step 2] Checking Java version:
java -version
echo.

echo [Step 3] Checking Gradle files:
dir /b gradle*
echo.

echo [Step 4] Killing any running Java processes...
taskkill /F /IM java.exe
timeout /t 2 /nobreak >nul
echo.

echo [Step 5] Starting server with debug options...
set GRADLE_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
call gradlew.bat bootRun --debug --stacktrace --info --console=plain

if errorlevel 1 (
    echo ERROR: Server failed to start!
    pause
    exit /b 1
)

pause
