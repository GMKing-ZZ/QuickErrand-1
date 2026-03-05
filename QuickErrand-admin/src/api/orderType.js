import request from '@/utils/request'

/**
 * 获取订单类型列表
 */
export function getOrderTypeList(params) {
  return request({
    url: '/admin/orderType/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单类型详情
 */
export function getOrderTypeDetail(id) {
  return request({
    url: `/admin/orderType/${id}`,
    method: 'get'
  })
}

/**
 * 创建订单类型
 */
export function createOrderType(data) {
  return request({
    url: '/admin/orderType',
    method: 'post',
    data
  })
}

/**
 * 更新订单类型
 */
export function updateOrderType(id, data) {
  return request({
    url: `/admin/orderType/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除订单类型
 */
export function deleteOrderType(id) {
  return request({
    url: `/admin/orderType/${id}`,
    method: 'delete'
  })
}

/**
 * 更新订单类型状态
 */
export function updateOrderTypeStatus(id, status) {
  return request({
    url: `/admin/orderType/status/${id}`,
    method: 'post',
    params: { status }
  })
}

/**
 * 批量删除订单类型
 */
export function batchDeleteOrderTypes(ids) {
  return request({
    url: '/admin/orderType/batch',
    method: 'delete',
    data: { ids }
  })
}