/**
 * 构建脚本：从manifest.json中读取高德地图key并生成配置文件
 * 使用方法：node scripts/read-manifest-key.js
 */

const fs = require('fs')
const path = require('path')

// 读取manifest.json
const manifestPath = path.join(__dirname, '../manifest.json')
const manifest = JSON.parse(fs.readFileSync(manifestPath, 'utf8'))

// 获取高德地图key
const amapKey = manifest?.web?.sdkConfigs?.maps?.amap?.key || ''

if (!amapKey) {
  console.warn('警告：manifest.json中未找到高德地图key配置')
  process.exit(1)
}

// 生成配置文件内容
const configContent = `/**
 * 地图配置（自动生成，请勿手动修改）
 * 此文件由 scripts/read-manifest-key.js 自动生成
 * 如需修改key，请修改 manifest.json 中的配置，然后重新运行构建脚本
 */

/**
 * 获取高德地图Key
 * 从App.uvue的globalData中读取，如果未设置则返回空字符串
 */
export function getAmapKey(): string {
  try {
    // 从App实例的globalData中获取
    const app = getApp<{ globalData: { amapKey?: string } }>()
    if (app && app.globalData && app.globalData.amapKey) {
      return app.globalData.amapKey
    }
  } catch (e) {
    console.warn('从globalData读取高德地图Key失败:', e)
  }
  
  // 如果globalData中没有，尝试从本地存储读取
  try {
    const storedKey = uni.getStorageSync('amapKey')
    if (storedKey) {
      return storedKey
    }
  } catch (e) {
    // 忽略错误
  }
  
  // 如果都失败，返回从manifest.json读取的值
  return '${amapKey}'
}

/**
 * 设置高德地图Key（用于应用启动时从manifest.json读取并设置）
 * 通常在App.uvue的onLaunch中调用
 */
export function setAmapKey(key: string) {
  try {
    const app = getApp<{ globalData: { amapKey?: string } }>()
    if (app && app.globalData) {
      app.globalData.amapKey = key
    }
    // 同时保存到本地存储，以便后续使用
    uni.setStorageSync('amapKey', key)
  } catch (e) {
    console.warn('设置高德地图Key失败:', e)
  }
}

// 导出配置对象（保持向后兼容）
export const MAP_CONFIG = {
  get amapKey() {
    return getAmapKey()
  }
}
`

// 写入配置文件
const configPath = path.join(__dirname, '../common/config/map.uts')
fs.writeFileSync(configPath, configContent, 'utf8')

console.log('✓ 成功从manifest.json读取高德地图key并生成配置文件')
console.log(`  Key: ${amapKey.substring(0, 10)}...`)
