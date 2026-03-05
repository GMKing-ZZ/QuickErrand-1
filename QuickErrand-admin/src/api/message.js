import request from '@/utils/request'

/**
 * 获取消息列表（分页）
 */
export function getMessagePage(params) {
  return request({
    url: '/admin/message/page',
    method: 'get',
    params
  })
}

/**
 * 获取消息统计
 */
export function getMessageStats() {
  return request({
    url: '/admin/message/stats',
    method: 'get'
  })
}

/**
 * 发送系统消息给所有用户
 */
export function sendMessageToAll(data) {
  return request({
    url: '/admin/message/send-all',
    method: 'post',
    data
  })
}

/**
 * 发送系统消息给指定用户
 */
export function sendMessageToUser(data) {
  return request({
    url: '/admin/message/send-user',
    method: 'post',
    data
  })
}

/**
 * 发送系统消息给多个用户
 */
export function sendMessageToUsers(data) {
  return request({
    url: '/admin/message/send-users',
    method: 'post',
    data
  })
}

/**
 * 删除消息
 */
export function deleteMessage(id) {
  return request({
    url: `/admin/message/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除消息
 */
export function batchDeleteMessages(ids) {
  return request({
    url: '/admin/message/batch',
    method: 'delete',
    data: ids
  })
}
