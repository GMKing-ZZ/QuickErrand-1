# 复制项目文件，排除 uni-im 相关内容
$sourceDir = "..\QuickErrand-app"
$targetDir = "."

# 排除的目录
$excludeDirs = @(
    "uni_modules\uni-im",
    "uni_modules\uni-im-msg-reader", 
    "uni_modules\uni-im.disabled",
    "uni_modules\uni-im-msg-reader.disabled",
    "unpackage",
    ".git"
)

# 排除的文件模式
$excludeFiles = @("*.log", "*.tmp")

Write-Host "开始复制项目文件..."

# 复制所有文件和目录
Get-ChildItem -Path $sourceDir -Recurse | ForEach-Object {
    $relativePath = $_.FullName.Substring($sourceDir.Length + 1)
    $targetPath = Join-Path $targetDir $relativePath
    
    # 检查是否在排除列表中
    $shouldExclude = $false
    foreach ($excludeDir in $excludeDirs) {
        if ($relativePath -like "*\$excludeDir\*" -or $relativePath -like "$excludeDir\*") {
            $shouldExclude = $true
            break
        }
    }
    
    if (-not $shouldExclude) {
        if ($_.PSIsContainer) {
            # 创建目录
            if (-not (Test-Path $targetPath)) {
                New-Item -ItemType Directory -Path $targetPath -Force | Out-Null
            }
        } else {
            # 创建父目录并复制文件
            $parentDir = Split-Path $targetPath -Parent
            if (-not (Test-Path $parentDir)) {
                New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
            }
            Copy-Item $_.FullName -Destination $targetPath -Force
        }
    }
}

Write-Host "文件复制完成！"
