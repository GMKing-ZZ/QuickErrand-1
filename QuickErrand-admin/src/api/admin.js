import request from '@/utils/request'

/**
 * 管理员登录
 */
export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

/**
 * 获取管理员信息
 */
export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

/**
 * 管理员退出登录
 */
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

/**
 * 更新管理员信息
 */
export function updateAdminInfo(data) {
  return request({
    url: '/admin/info',
    method: 'put',
    data
  })
}

/**
 * 修改管理员密码
 */
export function updateAdminPassword(data) {
  return request({
    url: '/admin/password',
    method: 'put',
    data
  })
}