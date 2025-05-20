@echo on
echo [Step 1] Environment Check
echo Current directory: %CD%
echo Java version:
java -version
echo JAVA_HOME: %JAVA_HOME%

echo [Step 2] Gradle Clean
call gradlew.bat clean --info

echo [Step 3] Dependency Check
call gradlew.bat dependencies --info

echo [Step 4] Running with full debug...
call gradlew.bat bootRun --debug --stacktrace --info --console=plain

pause
