import request from '@/utils/request'

/**
 * 获取提现申请列表（管理员）
 */
export function getWithdrawalList(params) {
  return request({
    url: '/admin/withdrawal/list',
    method: 'get',
    params
  })
}

/**
 * 审核提现申请（通过 / 驳回）
 */
export function auditWithdrawal(data) {
  return request({
    url: '/admin/withdrawal/audit',
    method: 'post',
    data
  })
}

/**
 * 标记提现已到账
 */
export function markWithdrawalTransferred(id) {
  return request({
    url: `/admin/withdrawal/transfer/${id}`,
    method: 'post'
  })
}

/**
 * 删除单条提现记录
 */
export function deleteWithdrawalById(id) {
  return request({
    url: `/admin/withdrawal/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除提现记录
 */
export function deleteWithdrawalBatch(ids) {
  return request({
    url: '/admin/withdrawal/delete',
    method: 'post',
    data: { ids }
  })
}

