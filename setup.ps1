# Hotel Room Management System - 一键环境配置与启动脚本
# 用法: 右键 -> 使用 PowerShell 运行, 或
#       powershell -ExecutionPolicy Bypass -File setup.ps1

$PROJECT_ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path
$SQL_DIR = "$PROJECT_ROOT\src\main\resources\sql"
$START_BAT = "$PROJECT_ROOT\start.bat"

$null = $Host.UI.RawUI.WindowTitle 2>$null

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   酒店客房管理系统 - 一键环境配置" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ==================== 1. 检测 Java ====================
Write-Host "[1/5] 检测 Java 环境..." -ForegroundColor Yellow

$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
    try {
        $javaCmd = (Get-Command java -ErrorAction Stop).Source
        if ($javaCmd) {
            $javaHome = Split-Path -Parent (Split-Path -Parent $javaCmd)
        }
    } catch {}
}

if (-not $javaHome) {
    Write-Host "  [错误] 未找到 Java! 请安装 JDK 17+" -ForegroundColor Red
    Write-Host "  下载地址: https://adoptium.net/download/" -ForegroundColor Gray
    Read-Host "按 Enter 退出"
    exit 1
}

try {
    $javaVersion = (& "$javaHome\bin\java" -version 2>&1 | ForEach-Object { "$_" }) -join " "
} catch {
    Write-Host "  [错误] 无法执行 java -version, 请检查 JAVA_HOME" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}

Write-Host "  [OK] Java: $javaVersion" -ForegroundColor Green
Write-Host "  JAVA_HOME: $javaHome" -ForegroundColor Gray

$verMatch = [regex]::Match($javaVersion, 'version "(\d+)')
if ($verMatch.Success) {
    $majorVer = [int]$verMatch.Groups[1].Value
    if ($majorVer -lt 17) {
        Write-Host "  [错误] Java 版本过低 ($majorVer), 需要 JDK 17+" -ForegroundColor Red
        Read-Host "按 Enter 退出"
        exit 1
    }
}

# ==================== 2. 检测 MySQL ====================
Write-Host ""
Write-Host "[2/5] 检测 MySQL 数据库..." -ForegroundColor Yellow

$mysqlCmd = $null
try { $mysqlCmd = (Get-Command mysql -ErrorAction Stop).Source } catch {}

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
    Write-Host "  [警告] 未找到 mysql 客户端, 将跳过数据库初始化" -ForegroundColor DarkYellow
    Write-Host "  如需初始化数据库, 请手动执行 src/main/resources/sql/ 下的SQL文件" -ForegroundColor Gray
    $skipDb = $true
} else {
    Write-Host "  [OK] MySQL 客户端: $mysqlCmd" -ForegroundColor Green
    $skipDb = $false
}

# ==================== 3. 数据库初始化 ====================
if (-not $skipDb) {
    Write-Host ""
    Write-Host "[3/5] 配置数据库..." -ForegroundColor Yellow

    $dbHost = Read-Host "  MySQL 主机地址 (默认 localhost)"
    if ([string]::IsNullOrWhiteSpace($dbHost)) { $dbHost = "localhost" }

    $dbPort = Read-Host "  MySQL 端口 (默认 3306)"
    if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "3306" }

    $dbUser = Read-Host "  MySQL 用户名 (默认 root)"
    if ([string]::IsNullOrWhiteSpace($dbUser)) { $dbUser = "root" }

    $dbPassSecure = Read-Host "  MySQL 密码" -AsSecureString
    $dbPassPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassSecure)
    )

    $dbName = "hotel_db"
    $mysqlArgs = @("-u", $dbUser, "-p$dbPassPlain", "-h", $dbHost, "-P", $dbPort, "--default-character-set=utf8mb4")

    Write-Host "  正在连接 MySQL..." -ForegroundColor Gray

    $testResult = cmd /c "`"$mysqlCmd`" $mysqlArgs -e `"SELECT 1`" 2>&1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [错误] MySQL 连接失败!" -ForegroundColor Red
        Write-Host "  $testResult" -ForegroundColor Gray
        Write-Host "  请检查: 1) MySQL 服务是否已启动  2) 用户名/密码是否正确" -ForegroundColor Gray
        Read-Host "按 Enter 退出"
        exit 1
    }
    Write-Host "  [OK] MySQL 连接成功" -ForegroundColor Green

    # 创建数据库
    Write-Host "  正在创建数据库 $dbName..." -ForegroundColor Gray
    cmd /c "`"$mysqlCmd`" $mysqlArgs -e `"CREATE DATABASE IF NOT EXISTS $dbName DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci`" 2>&1" | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [警告] 数据库创建可能失败, 继续执行..." -ForegroundColor DarkYellow
    } else {
        Write-Host "  [OK] 数据库就绪" -ForegroundColor Green
    }

    # 执行 SQL 文件
    Write-Host ""
    $sqlFiles = @("schema.sql", "views.sql", "triggers.sql", "procedures.sql", "init_data.sql")
    foreach ($sqlFile in $sqlFiles) {
        $sqlPath = "$SQL_DIR\$sqlFile"
        if (Test-Path $sqlPath) {
            Write-Host "  执行: $sqlFile ..." -ForegroundColor Gray
            $execResult = cmd /c "`"$mysqlCmd`" $mysqlArgs $dbName < `"$sqlPath`" 2>&1"
            if ($LASTEXITCODE -ne 0) {
                if ($execResult -match "already exists") {
                    Write-Host "    [跳过] 已存在, 无需重复创建" -ForegroundColor DarkYellow
                } else {
                    Write-Host "    [警告] 可能有错误: $execResult" -ForegroundColor DarkYellow
                }
            }
        } else {
            Write-Host "  [跳过] $sqlFile 不存在" -ForegroundColor DarkYellow
        }
    }
    Write-Host "  [OK] 数据库初始化完成" -ForegroundColor Green

    # 更新 application.properties
    $propsPath = "$PROJECT_ROOT\src\main\resources\application.properties"
    if (Test-Path $propsPath) {
        $propsContent = Get-Content $propsPath -Raw -Encoding UTF8
        $propsContent = $propsContent -replace "jdbc:mysql://localhost:3306/hotel_db", "jdbc:mysql://${dbHost}:${dbPort}/${dbName}"
        [System.IO.File]::WriteAllText($propsPath, $propsContent.TrimEnd(), [System.Text.Encoding]::UTF8)
    }
}

# ==================== 4. 构建项目 ====================
Write-Host ""
Write-Host "[4/5] 构建项目..." -ForegroundColor Yellow

$mvnCmd = $null
try { $mvnCmd = (Get-Command mvn -ErrorAction Stop).Source } catch {}

if (-not $mvnCmd) {
    $mvnHome = $env:MAVEN_HOME
    if ($mvnHome -and (Test-Path "$mvnHome\bin\mvn.cmd")) {
        $mvnCmd = "$mvnHome\bin\mvn.cmd"
    }
}

if (-not $mvnCmd) {
    Write-Host "  [错误] 未找到 Maven! 请安装 Maven 3.6+" -ForegroundColor Red
    Write-Host "  下载地址: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Read-Host "按 Enter 退出"
    exit 1
}

Write-Host "  使用 Maven: $mvnCmd" -ForegroundColor Gray
Write-Host "  正在编译打包 (首次可能较慢, 请耐心等待)..." -ForegroundColor Gray

Push-Location $PROJECT_ROOT
$buildResult = cmd /c "`"$mvnCmd`" clean package -DskipTests -q 2>&1"
$buildExit = $LASTEXITCODE
Pop-Location

if ($buildExit -ne 0) {
    Write-Host "  [错误] 项目构建失败!" -ForegroundColor Red
    Write-Host "  --- 错误详情 ---" -ForegroundColor Gray
    Write-Host $buildResult
    Write-Host "  --- 请检查 Maven 和 Java 配置 ---" -ForegroundColor Gray
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Host "  [OK] 项目构建成功" -ForegroundColor Green

# ==================== 5. 生成启动脚本 ====================
Write-Host ""
Write-Host "[5/5] 生成启动脚本..." -ForegroundColor Yellow

$jarPath = "$PROJECT_ROOT\target\hotel-room-management-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "  [错误] 未找到 jar 包: $jarPath" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}

$startBatContent = @"
@echo off
chcp 65001 >nul
title 酒店客房管理系统

echo ========================================
echo    酒店客房管理系统
echo ========================================
echo.

REM 自动检测 Java
if not defined JAVA_HOME (
    for /f "tokens=*" %%i in ('where java 2^>nul') do (
        set JAVA_EXE=%%i
        goto :found_java
    )
    echo [错误] 未找到 Java! 请安装 JDK 17+
    echo 下载地址: https://adoptium.net/download/
    pause
    exit /b 1
)
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:found_java
echo 启动服务中...
echo.

"%JAVA_EXE%" -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar "%~dp0target\hotel-room-management-1.0.0.jar"

pause
"@

[System.IO.File]::WriteAllText($START_BAT, $startBatContent, [System.Text.Encoding]::UTF8)
Write-Host "  [OK] 启动脚本已生成: start.bat" -ForegroundColor Green

# ==================== 完成 ====================
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   配置完成! 请双击 start.bat 启动系统" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  默认账号: admin" -ForegroundColor White
Write-Host "  默认密码: admin123" -ForegroundColor White
Write-Host ""
Write-Host "  如需重新配置, 请再次运行此脚本" -ForegroundColor Gray
Write-Host ""

$startNow = Read-Host "  是否现在启动系统? (y/n, 默认 y)"
if ([string]::IsNullOrWhiteSpace($startNow) -or $startNow -eq "y" -or $startNow -eq "Y") {
    Write-Host "  正在启动系统..." -ForegroundColor Green
    Start-Process cmd -ArgumentList "/c", "`"$START_BAT`"" -WindowStyle Normal
}

pause