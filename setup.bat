@echo off
chcp 65001 >nul
title 酒店客房管理系统 - 环境配置

echo ========================================
echo    酒店客房管理系统 - 一键环境配置
echo ========================================
echo.

:: ==================== 1. Check Java ====================
echo [1/5] Checking Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo   [ERROR] Java not found! Please install JDK 17+
    echo   Download: https://adoptium.net/download/
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1') do set JAVA_VER=%%i
echo   [OK] %JAVA_VER%
echo.

:: ==================== 2. Check MySQL ====================
echo [2/5] Checking MySQL...

set MYSQL_EXE=
where mysql >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=*" %%i in ('where mysql 2^>nul') do set MYSQL_EXE=%%i
) else (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
    if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
    if exist "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe" set MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe
)

if "%MYSQL_EXE%"=="" (
    echo   [WARN] mysql client not found, skipping DB init
    set SKIP_DB=1
) else (
    echo   [OK] MySQL client: %MYSQL_EXE%
    set SKIP_DB=0
)

:: ==================== 3. Database Init ====================
if %SKIP_DB% equ 0 (
    echo.
    echo [3/5] Database Setup...

    set DB_HOST=localhost
    set /p DB_HOST="  MySQL host (default localhost): "
    if "%DB_HOST%"=="" set DB_HOST=localhost

    set DB_PORT=3306
    set /p DB_PORT="  MySQL port (default 3306): "
    if "%DB_PORT%"=="" set DB_PORT=3306

    set DB_USER=root
    set /p DB_USER="  MySQL user (default root): "
    if "%DB_USER%"=="" set DB_USER=root

    set /p DB_PASS="  MySQL password: "

    echo.
    echo   Connecting to MySQL...

    "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% -e "SELECT 1" >nul 2>&1
    if %errorlevel% neq 0 (
        echo   [ERROR] MySQL connection failed!
        echo   Check: 1^) MySQL service is running  2^) username/password correct
        pause
        exit /b 1
    )
    echo   [OK] MySQL connected

    echo   Creating database hotel_db...
    "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% -e "CREATE DATABASE IF NOT EXISTS hotel_db DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci" >nul 2>&1
    echo   [OK] Database ready

    echo.
    set SQL_DIR=src\main\resources\sql

    echo   Running: schema.sql ...
    type "%SQL_DIR%\schema.sql" | "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% --default-character-set=utf8mb4 hotel_db 2>&1
    if %errorlevel% neq 0 echo     [WARN] May have errors (already exists is OK)

    echo   Running: views.sql ...
    type "%SQL_DIR%\views.sql" | "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% --default-character-set=utf8mb4 hotel_db 2>&1
    if %errorlevel% neq 0 echo     [WARN] May have errors (already exists is OK)

    echo   Running: triggers.sql ...
    type "%SQL_DIR%\triggers.sql" | "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% --default-character-set=utf8mb4 hotel_db 2>&1
    if %errorlevel% neq 0 echo     [WARN] May have errors (already exists is OK)

    echo   Running: procedures.sql ...
    type "%SQL_DIR%\procedures.sql" | "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% --default-character-set=utf8mb4 hotel_db 2>&1
    if %errorlevel% neq 0 echo     [WARN] May have errors (already exists is OK)

    echo   Running: init_data.sql ...
    type "%SQL_DIR%\init_data.sql" | "%MYSQL_EXE%" -u %DB_USER% -p%DB_PASS% -h %DB_HOST% -P %DB_PORT% --default-character-set=utf8mb4 hotel_db 2>&1
    if %errorlevel% neq 0 echo     [WARN] May have errors (already exists is OK)

    echo   [OK] Database init done
)

:: ==================== 4. Build Project ====================
echo.
echo [4/5] Building project...

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    if defined MAVEN_HOME (
        set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
    ) else (
        echo   [ERROR] Maven not found! Please install Maven 3.6+
        echo   Download: https://maven.apache.org/download.cgi
        pause
        exit /b 1
    )
) else (
    for /f "tokens=*" %%i in ('where mvn 2^>nul') do set MVN_CMD=%%i
)

echo   Maven: %MVN_CMD%
echo   Compiling ^& packaging (may take a while)...
call "%MVN_CMD%" clean package -DskipTests -q 2>&1
if %errorlevel% neq 0 (
    echo   [ERROR] Build failed! Check Maven ^& Java config.
    pause
    exit /b 1
)
echo   [OK] Build success

:: ==================== 5. Generate start.bat ====================
echo.
echo [5/5] Generating start script...

if not exist "target\hotel-room-management-1.0.0.jar" (
    echo   [ERROR] Jar not found: target\hotel-room-management-1.0.0.jar
    pause
    exit /b 1
)

(
echo @echo off
echo chcp 65001 ^>nul
echo title Hotel Room Management System
echo.
echo echo ========================================
echo echo     Hotel Room Management System
echo echo ========================================
echo echo.
echo.
echo REM Auto-detect Java
echo if not defined JAVA_HOME ^(
echo     for /f "tokens=*" %%%%i in ^('where java 2^^^>nul'^) do ^(
echo         set JAVA_EXE=%%%%i
echo         goto :found_java
echo     ^)
echo     echo [ERROR] Java not found! Install JDK 17+
echo     echo Download: https://adoptium.net/download/
echo     pause
echo     exit /b 1
echo ^)
echo set JAVA_EXE=%%JAVA_HOME%%\bin\java.exe
echo.
echo :found_java
echo echo Starting...
echo echo.
echo.
echo "%%JAVA_EXE%%" -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar "%%~dp0target\hotel-room-management-1.0.0.jar"
echo.
echo pause
) > start.bat

echo   [OK] start.bat generated

:: ==================== Done ====================
echo.
echo ========================================
echo    Setup complete! Double-click start.bat
echo ========================================
echo.
echo   Default account: admin
echo   Default password: admin123
echo.

set /p STARTNOW="  Start now? (y/n, default y): "
if "%STARTNOW%"=="" set STARTNOW=y
if /i "%STARTNOW%"=="y" start cmd /k start.bat

pause