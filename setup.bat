@echo off
chcp 65001 >nul
title 酒店客房管理系统 - 环境配置

echo ========================================
echo    酒店客房管理系统 - 一键环境配置
echo ========================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" 2>&1

echo.
echo ========================================
echo    脚本执行完毕
echo ========================================
pause