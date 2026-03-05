import request from '@/utils/request'

/**
 * 获取订单列表
 */
export function getOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(orderId) {
  return request({
    url: `/admin/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 更新订单状态
 */
export function updateOrderStatus(orderId, status) {
  return request({
    url: `/admin/order/status/${orderId}`,
    method: 'post',
    params: { status }
  })
}

/**
 * 删除单个订单
 */
export function deleteOrder(orderId) {
  return request({
    url: `/admin/order/${orderId}`,
    method: 'delete'
  })
}

/**
 * 批量删除订单
 */
export function batchDeleteOrders(ids) {
  return request({
    url: '/admin/order/batch',
    method: 'delete',
    data: { ids }
  })
}