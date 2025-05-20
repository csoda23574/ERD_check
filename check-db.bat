@echo off
echo Checking database status...
echo.

mysql -u root -p recipematedb -e "SHOW TABLES; SELECT 'USER TABLE:' as ''; SELECT * FROM user; SELECT 'FOOD TABLE:' as ''; SELECT * FROM food;"

pause
