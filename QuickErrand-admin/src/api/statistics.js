import request from '@/utils/request'

/**
 * 获取数据看板统计数据
 */
export function getDashboardStatistics() {
  return request({
    url: '/admin/statistics/dashboard',
    method: 'get'
  })
}
