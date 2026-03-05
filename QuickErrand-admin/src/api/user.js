import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

/**
 * 更新用户状态
 */
export function updateUserStatus(userId, status) {
  return request({
    url: `/admin/user/status/${userId}`,
    method: 'post',
    params: { status }
  })
}

/**
 * 创建用户
 */
export function createUser(data) {
  return request({
    url: '/admin/user',
    method: 'post',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(userId) {
  return request({
    url: `/admin/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户
 */
export function batchDeleteUsers(ids) {
  return request({
    url: '/admin/user/batch',
    method: 'delete',
    data: { ids }
  })
}
