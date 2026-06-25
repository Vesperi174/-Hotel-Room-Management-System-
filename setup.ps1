# Hotel Room Management System - 一键环境配置与启动脚本
# 用法: 右键 -> 使用PowerShell运行, 或 .\setup.ps1
# 首次运行可能需执行: Set-ExecutionPolicy -Scope CurrentUser RemoteSigned

$ErrorActionPreference = "Stop"
$Host.UI.RawUI.WindowTitle = "酒店客房管理系统 - 环境配置"

$PROJECT_ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path
$SQL_DIR = "$PROJECT_ROOT\src\main\resources\sql"
$START_BAT = "$PROJECT_ROOT\start.bat"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   酒店客房管理系统 - 一键环境配置" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ==================== 1. 检测 Java ====================
Write-Host "[1/5] 检测 Java 环境..." -ForegroundColor Yellow

$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
    $javaCmd = (Get-Command java -ErrorAction SilentlyContinue).Source
    if ($javaCmd) {
        $javaHome = Split-Path -Parent (Split-Path -Parent $javaCmd)
    }
}

if (-not $javaHome) {
    Write-Host "  [错误] 未找到 Java! 请安装 JDK 17+" -ForegroundColor Red
    Write-Host "  下载地址: https://adoptium.net/download/" -ForegroundColor Gray
    pause
    exit 1
}

$javaVersion = & "$javaHome\bin\java" -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
Write-Host "  [OK] Java: $javaVersion" -ForegroundColor Green
Write-Host "  JAVA_HOME: $javaHome" -ForegroundColor Gray

# 校验版本 >= 17
$verMatch = [regex]::Match($javaVersion, 'version "(\d+)')
if ($verMatch.Success) {
    $majorVer = [int]$verMatch.Groups[1].Value
    if ($majorVer -lt 17) {
        Write-Host "  [错误] Java 版本过低 ($majorVer), 需要 JDK 17+" -ForegroundColor Red
        pause
        exit 1
    }
}

# ==================== 2. 检测 MySQL ====================
Write-Host ""
Write-Host "[2/5] 检测 MySQL 数据库..." -ForegroundColor Yellow

$mysqlCmd = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysqlCmd) {
    $mysqlService = Get-Service -Name "*mysql*" -ErrorAction SilentlyContinue
    if ($mysqlService) {
        $mysqlBin = (Get-ItemProperty "HKLM:\SYSTEM\CurrentControlSet\Services\$($mysqlService.Name)").ImagePath
        if ($mysqlBin -match '"([^"]+)"') {
            $mysqlDir = Split-Path -Parent $Matches[1]
            $mysqlCmd = "$mysqlDir\mysql.exe"
        }
    }
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
}

if (-not $mysqlCmd -or -not (Test-Path $mysqlCmd)) {
    Write-Host "  [警告] 未找到 mysql 客户端, 将跳过数据库初始化" -ForegroundColor DarkYellow
    Write-Host "  请手动执行 src/main/resources/sql/ 下的SQL文件" -ForegroundColor Gray
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

    $dbPass = Read-Host "  MySQL 密码" -AsSecureString
    $dbPassPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPass)
    )

    $dbName = "hotel_db"

    # 构建 mysql 命令参数
    $mysqlArgs = "-u", $dbUser, "-p$dbPassPlain", "-h", $dbHost, "-P", $dbPort, "--default-character-set=utf8mb4"

    Write-Host "  正在连接 MySQL..." -ForegroundColor Gray

    # 测试连接
    $testResult = cmd /c "`"$mysqlCmd`" $mysqlArgs -e `"SELECT 1`" 2>&1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [错误] MySQL 连接失败: $testResult" -ForegroundColor Red
        Write-Host "  请检查用户名/密码, 或确保 MySQL 服务已启动" -ForegroundColor Gray
        pause
        exit 1
    }
    Write-Host "  [OK] MySQL 连接成功" -ForegroundColor Green

    # 创建数据库
    Write-Host "  正在创建数据库 $dbName..." -ForegroundColor Gray
    cmd /c "`"$mysqlCmd`" $mysqlArgs -e `"CREATE DATABASE IF NOT EXISTS $dbName DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci`" 2>&1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [警告] 数据库创建可能失败, 尝试继续..." -ForegroundColor DarkYellow
    }

    # 执行 SQL 文件
    $sqlFiles = @("schema.sql", "views.sql", "triggers.sql", "procedures.sql", "init_data.sql")
    foreach ($sqlFile in $sqlFiles) {
        $sqlPath = "$SQL_DIR\$sqlFile"
        if (Test-Path $sqlPath) {
            Write-Host "  执行: $sqlFile ..." -ForegroundColor Gray
            # 使用 cmd /c 来正确处理 mysql 的 DELIMITER 命令
            $execResult = cmd /c "`"$mysqlCmd`" $mysqlArgs $dbName < `"$sqlPath`" 2>&1"
            if ($LASTEXITCODE -ne 0) {
                Write-Host "  [警告] $sqlFile 执行可能有警告: $execResult" -ForegroundColor DarkYellow
            }
        } else {
            Write-Host "  [跳过] $sqlFile 不存在" -ForegroundColor DarkYellow
        }
    }
    Write-Host "  [OK] 数据库初始化完成" -ForegroundColor Green

    # 更新 application.properties 中的数据库配置
    $propsPath = "$PROJECT_ROOT\src\main\resources\application.properties"
    if (Test-Path $propsPath) {
        $propsContent = Get-Content $propsPath -Raw -Encoding UTF8
        $propsContent = $propsContent -replace "jdbc:mysql://localhost:3306/hotel_db", "jdbc:mysql://${dbHost}:${dbPort}/${dbName}"
        Set-Content $propsPath $propsContent -Encoding UTF8 -NoNewline
    }
}

# ==================== 4. 构建项目 ====================
Write-Host ""
Write-Host "[4/5] 构建项目..." -ForegroundColor Yellow

# 检测 Maven
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    $mvnHome = $env:MAVEN_HOME
    if ($mvnHome) {
        $mvnCmd = "$mvnHome\bin\mvn.cmd"
    }
    if (-not $mvnCmd -or -not (Test-Path $mvnCmd)) {
        $mvnWrapper = "$PROJECT_ROOT\mvnw.cmd"
        if (Test-Path $mvnWrapper) {
            $mvnCmd = $mvnWrapper
        } else {
            Write-Host "  [错误] 未找到 Maven! 请安装 Maven 3.6+" -ForegroundColor Red
            Write-Host "  下载地址: https://maven.apache.org/download.cgi" -ForegroundColor Gray
            pause
            exit 1
        }
    }
}

Write-Host "  使用 Maven: $mvnCmd" -ForegroundColor Gray
Write-Host "  正在编译打包 (首次可能较慢, 请耐心等待)..." -ForegroundColor Gray

Push-Location $PROJECT_ROOT
$buildResult = & cmd /c "`"$mvnCmd`" clean package -DskipTests -q 2>&1"
$buildExit = $LASTEXITCODE
Pop-Location

if ($buildExit -ne 0) {
    Write-Host "  [错误] 项目构建失败!" -ForegroundColor Red
    Write-Host $buildResult
    pause
    exit 1
}
Write-Host "  [OK] 项目构建成功" -ForegroundColor Green

# ==================== 5. 生成启动脚本 ====================
Write-Host ""
Write-Host "[5/5] 生成启动脚本..." -ForegroundColor Yellow

$jarPath = "$PROJECT_ROOT\target\hotel-room-management-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "  [错误] 未找到 jar 包: $jarPath" -ForegroundColor Red
    pause
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

REM 自动检测 JAVA_HOME
if not defined JAVA_HOME (
    for /f "tokens=*" %%i in ('where java 2^>nul') do (
        set JAVA_EXE=%%i
        goto :found_java
    )
    echo [错误] 未找到 Java! 请安装 JDK 17+
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

Set-Content $START_BAT $startBatContent -Encoding UTF8
Write-Host "  [OK] 启动脚本已生成: $START_BAT" -ForegroundColor Green

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