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