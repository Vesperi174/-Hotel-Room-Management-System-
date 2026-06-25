# Hotel Room Management System - 一键环境配置脚本
# 请通过 setup.bat 启动, 不要直接双击此文件

$PROJECT_ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path
$SQL_DIR = "$PROJECT_ROOT\src\main\resources\sql"
$START_BAT = "$PROJECT_ROOT\start.bat"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Hotel Room Management System" -ForegroundColor Cyan
Write-Host "    One-Click Environment Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ==================== 1. Check Java ====================
Write-Host "[1/5] Checking Java..." -ForegroundColor Yellow

$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
    try {
        $javaCmd = (Get-Command java -ErrorAction Stop).Source
        if ($javaCmd) {
            $javaHome = Split-Path -Parent (Split-Path -Parent $javaCmd)
        }
    } catch { }
}

if (-not $javaHome) {
    Write-Host "  [ERROR] Java not found! Please install JDK 17+" -ForegroundColor Red
    Write-Host "  Download: https://adoptium.net/download/" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}

try {
    $javaVersion = (& "$javaHome\bin\java" -version 2>&1 | ForEach-Object { "$_" }) -join " "
} catch {
    Write-Host "  [ERROR] Cannot run java -version, check JAVA_HOME" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "  [OK] Java: $javaVersion" -ForegroundColor Green
Write-Host "  JAVA_HOME: $javaHome" -ForegroundColor Gray

$verMatch = [regex]::Match($javaVersion, 'version "(\d+)')
if ($verMatch.Success) {
    $majorVer = [int]$verMatch.Groups[1].Value
    if ($majorVer -lt 17) {
        Write-Host "  [ERROR] Java version too low ($majorVer), need JDK 17+" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# ==================== 2. Check MySQL ====================
Write-Host ""
Write-Host "[2/5] Checking MySQL..." -ForegroundColor Yellow

$mysqlCmd = $null
try { $mysqlCmd = (Get-Command mysql -ErrorAction Stop).Source } catch { }

if (-not $mysqlCmd) {
    $defaultPaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
    )
    foreach ($p in $defaultPaths) {
        if (Test-Path $p) { $mysqlCmd = $p; break }
    }
}

if (-not $mysqlCmd -or -not (Test-Path $mysqlCmd)) {
    Write-Host "  [WARN] mysql client not found, skipping DB init" -ForegroundColor DarkYellow
    Write-Host "  Run SQL files in src/main/resources/sql/ manually if needed" -ForegroundColor Gray
    $skipDb = $true
} else {
    Write-Host "  [OK] MySQL client: $mysqlCmd" -ForegroundColor Green
    $skipDb = $false
}

# ==================== 3. Database Init ====================
if (-not $skipDb) {
    Write-Host ""
    Write-Host "[3/5] Database Setup..." -ForegroundColor Yellow

    $dbHost = Read-Host "  MySQL host (default localhost)"
    if ([string]::IsNullOrWhiteSpace($dbHost)) { $dbHost = "localhost" }

    $dbPort = Read-Host "  MySQL port (default 3306)"
    if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "3306" }

    $dbUser = Read-Host "  MySQL user (default root)"
    if ([string]::IsNullOrWhiteSpace($dbUser)) { $dbUser = "root" }

    $dbPassSecure = Read-Host "  MySQL password" -AsSecureString
    $dbPassPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassSecure)
    )

    $dbName = "hotel_db"
    $mysqlBaseArgs = @("-u", $dbUser, "-p$dbPassPlain", "-h", $dbHost, "-P", $dbPort, "--default-character-set=utf8mb4")

    Write-Host "  Connecting to MySQL..." -ForegroundColor Gray

    $testResult = & cmd /c "`"$mysqlCmd`" -u $dbUser -p$dbPassPlain -h $dbHost -P $dbPort -e `"SELECT 1`" 2>&1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [ERROR] MySQL connection failed!" -ForegroundColor Red
        Write-Host "  $testResult" -ForegroundColor Gray
        Write-Host "  Check: 1) MySQL service is running  2) username/password correct" -ForegroundColor Gray
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "  [OK] MySQL connected" -ForegroundColor Green

    # Create database
    Write-Host "  Creating database $dbName..." -ForegroundColor Gray
    & cmd /c "`"$mysqlCmd`" -u $dbUser -p$dbPassPlain -h $dbHost -P $dbPort -e `"CREATE DATABASE IF NOT EXISTS $dbName DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci`" 2>&1" | Out-Null
    Write-Host "  [OK] Database ready" -ForegroundColor Green

    # Execute SQL files
    Write-Host ""
    $sqlFiles = @("schema.sql", "views.sql", "triggers.sql", "procedures.sql", "init_data.sql")
    foreach ($sqlFile in $sqlFiles) {
        $sqlPath = "$SQL_DIR\$sqlFile"
        if (Test-Path $sqlPath) {
            Write-Host "  Running: $sqlFile ..." -ForegroundColor Gray
            $execResult = & cmd /c "type `"$sqlPath`" | `"$mysqlCmd`" -u $dbUser -p$dbPassPlain -h $dbHost -P $dbPort --default-character-set=utf8mb4 $dbName 2>&1"
            if ($LASTEXITCODE -ne 0) {
                if ($execResult -match "already exists") {
                    Write-Host "    [SKIP] Already exists" -ForegroundColor DarkYellow
                } else {
                    Write-Host "    [WARN] $execResult" -ForegroundColor DarkYellow
                }
            }
        } else {
            Write-Host "  [SKIP] $sqlFile not found" -ForegroundColor DarkYellow
        }
    }
    Write-Host "  [OK] Database init done" -ForegroundColor Green

    # Update application.properties
    $propsPath = "$PROJECT_ROOT\src\main\resources\application.properties"
    if (Test-Path $propsPath) {
        $propsContent = Get-Content $propsPath -Raw -Encoding UTF8
        $propsContent = $propsContent -replace "jdbc:mysql://localhost:3306/hotel_db", "jdbc:mysql://${dbHost}:${dbPort}/${dbName}"
        [System.IO.File]::WriteAllText($propsPath, $propsContent.TrimEnd(), [System.Text.Encoding]::UTF8)
    }
}

# ==================== 4. Build Project ====================
Write-Host ""
Write-Host "[4/5] Building project..." -ForegroundColor Yellow

$mvnCmd = $null
try { $mvnCmd = (Get-Command mvn -ErrorAction Stop).Source } catch { }

if (-not $mvnCmd) {
    $mvnHome = $env:MAVEN_HOME
    if ($mvnHome -and (Test-Path "$mvnHome\bin\mvn.cmd")) {
        $mvnCmd = "$mvnHome\bin\mvn.cmd"
    }
}

if (-not $mvnCmd) {
    Write-Host "  [ERROR] Maven not found! Please install Maven 3.6+" -ForegroundColor Red
    Write-Host "  Download: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "  Maven: $mvnCmd" -ForegroundColor Gray
Write-Host "  Compiling & packaging (may take a while)..." -ForegroundColor Gray

Push-Location $PROJECT_ROOT
$buildResult = & cmd /c "`"$mvnCmd`" clean package -DskipTests -q 2>&1"
$buildExit = $LASTEXITCODE
Pop-Location

if ($buildExit -ne 0) {
    Write-Host "  [ERROR] Build failed!" -ForegroundColor Red
    Write-Host "  --- Error details ---" -ForegroundColor Gray
    Write-Host $buildResult
    Write-Host "  --- Check Maven & Java config ---" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "  [OK] Build success" -ForegroundColor Green

# ==================== 5. Generate start.bat ====================
Write-Host ""
Write-Host "[5/5] Generating start script..." -ForegroundColor Yellow

$jarPath = "$PROJECT_ROOT\target\hotel-room-management-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "  [ERROR] Jar not found: $jarPath" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

$startBatLines = @(
    '@echo off',
    'chcp 65001 >nul',
    'title Hotel Room Management System',
    '',
    'echo ========================================',
    'echo     Hotel Room Management System',
    'echo ========================================',
    'echo.',
    '',
    'REM Auto-detect Java',
    'if not defined JAVA_HOME (',
    '    for /f "tokens=*" %%i in (' + "'" + 'where java 2^>nul' + "'" + ') do (',
    '        set JAVA_EXE=%%i',
    '        goto :found_java',
    '    )',
    '    echo [ERROR] Java not found! Install JDK 17+',
    '    echo Download: https://adoptium.net/download/',
    '    pause',
    '    exit /b 1',
    ')',
    'set JAVA_EXE=%JAVA_HOME%\bin\java.exe',
    '',
    ':found_java',
    'echo Starting...',
    'echo.',
    '',
    '"%JAVA_EXE%" -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar "%~dp0target\hotel-room-management-1.0.0.jar"',
    '',
    'pause'
)

$startBatContent = $startBatLines -join "`r`n"
[System.IO.File]::WriteAllText($START_BAT, $startBatContent, [System.Text.Encoding]::ASCII)
Write-Host "  [OK] start.bat generated" -ForegroundColor Green

# ==================== Done ====================
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Setup complete! Double-click start.bat" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Default account: admin" -ForegroundColor White
Write-Host "  Default password: admin123" -ForegroundColor White
Write-Host ""

$startNow = Read-Host "  Start now? (y/n, default y)"
if ([string]::IsNullOrWhiteSpace($startNow) -or $startNow -eq "y" -or $startNow -eq "Y") {
    Write-Host "  Launching..." -ForegroundColor Green
    Start-Process cmd -ArgumentList "/c", "`"$START_BAT`"" -WindowStyle Normal
}

pause