@echo on
echo [Step 1] Running with debug options...
call gradlew.bat bootRun --debug --stacktrace --info

if errorlevel 1 (
    echo Failed to start server
    echo Check logs above for details
    pause
    exit /b 1
)
pause
