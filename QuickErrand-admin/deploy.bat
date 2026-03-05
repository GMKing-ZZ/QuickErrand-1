@echo off
echo ========================================
echo QuickErrand 管理后台部署脚本
echo ========================================
echo.
echo 请按照以下步骤手动部署：
echo.
echo 1. 打开云开发控制台
echo 2. 进入"静态网站托管"
echo 3. 点击"上传文件"
echo 4. 选择以下目录中的所有文件和文件夹：
echo    %~dp0dist
echo.
echo 5. 上传到根目录 /
echo 6. 设置默认文档为 index.html
echo.
echo ========================================
echo 部署文件位置：%~dp0dist
echo ========================================
echo.
pause
