import request from '@/utils/request'

/**
 * 获取收益结算记录列表（管理员）
 */
export function getEarningsList(params) {
  return request({
    url: '/admin/earnings/list',
    method: 'get',
    params
  })
}

/**
 * 手动结算收益记录
 */
export function settleEarnings(ids) {
  return request({
    url: '/admin/earnings/settle',
    method: 'post',
    data: { ids }
  })
}

/**
 * 删除单条收益记录
 */
export function deleteEarningsById(id) {
  return request({
    url: `/admin/earnings/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除收益记录
 */
export function deleteEarningsBatch(ids) {
  return request({
    url: '/admin/earnings/delete',
    method: 'post',
    data: { ids }
  })
}

