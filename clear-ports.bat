@echo on
echo [Step 1] Checking ports...
netstat -ano | findstr :8081

echo [Step 2] Killing processes...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
    echo Killing process with PID: %%a
    taskkill /F /PID %%a
)

echo [Step 3] Verifying ports are clear...
timeout /t 2 /nobreak >nul
netstat -ano | findstr :8081

echo Done!
pause
