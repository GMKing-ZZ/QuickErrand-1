import request from '@/utils/request'

/**
 * 获取认证申请列表
 */
export function getAuthApplicationList(params) {
  return request({
    url: '/admin/runnerAuth/list',
    method: 'get',
    params
  })
}

/**
 * 审核认证申请
 */
export function approveAuth(data) {
  return request({
    url: '/admin/runnerAuth/approve',
    method: 'post',
    data
  })
}

/**
 * 删除认证申请记录
 */
export function deleteAuthApplication(id) {
  return request({
    url: `/admin/runnerAuth/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除认证申请
 */
export function batchDeleteAuthApplications(ids) {
  return request({
    url: '/admin/runnerAuth/batch',
    method: 'delete',
    data: { ids }
  })
}