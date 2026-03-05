import request from '@/utils/request'

/**
 * 获取跑腿员信用等级列表
 */
export function getRunnerCreditList(params) {
  return request({
    url: '/admin/creditLevel/list',
    method: 'get',
    params
  })
}

/**
 * 手动更新跑腿员信用等级
 */
export function updateCreditLevel(runnerId, creditLevel) {
  return request({
    url: `/admin/creditLevel/${runnerId}`,
    method: 'put',
    params: { creditLevel }
  })
}

/**
 * 重新计算单个跑腿员信用等级
 */
export function recalculateCreditLevel(runnerId) {
  return request({
    url: `/admin/creditLevel/recalculate/${runnerId}`,
    method: 'post'
  })
}

/**
 * 批量重新计算所有跑腿员信用等级
 */
export function recalculateAllCreditLevels() {
  return request({
    url: '/admin/creditLevel/recalculateAll',
    method: 'post'
  })
}

/**
 * 批量删除信用等级记录
 */
export function batchDeleteCreditLevels(ids) {
  return request({
    url: '/admin/creditLevel/batch',
    method: 'delete',
    data: { ids }
  })
}