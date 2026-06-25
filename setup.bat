@echo off
chcp 65001 >nul
title 酒店客房管理系统 - 环境配置


powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" 2>&1
