import request from '@/utils/request'

/**
 * 获取轮播图列表
 */
export function getBannerList(params) {
  return request({
    url: '/admin/banner/list',
    method: 'get',
    params
  })
}

/**
 * 获取轮播图详情
 */
export function getBannerById(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'get'
  })
}

/**
 * 创建轮播图
 */
export function createBanner(data) {
  return request({
    url: '/admin/banner',
    method: 'post',
    data
  })
}

/**
 * 更新轮播图
 */
export function updateBanner(id, data) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除轮播图
 */
export function deleteBanner(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'delete'
  })
}

/**
 * 更新轮播图状态
 */
export function updateBannerStatus(id, status) {
  return request({
    url: `/admin/banner/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 批量删除轮播图
 */
export function batchDeleteBanners(ids) {
  return request({
    url: '/admin/banner/batch',
    method: 'delete',
    data: ids
  })
}
