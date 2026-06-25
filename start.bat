@echo off
chcp 65001 >nul
title Hotel Room Management System

echo ========================================
echo     Hotel Room Management System
echo ========================================
echo.

REM Auto-detect Java
if not defined JAVA_HOME (
    for /f "tokens=*" %%i in (
'
where java 2^>nul
'
) do (
        set JAVA_EXE=%%i
        goto :found_java
    )
    echo [ERROR] Java not found! Install JDK 17+
    echo Download: https://adoptium.net/download/
    pause
    exit /b 1
)
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:found_java
echo Starting...
echo.

"%JAVA_EXE%" -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar "%~dp0target\hotel-room-management-1.0.0.jar"

pause