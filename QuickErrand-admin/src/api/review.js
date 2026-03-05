import request from '@/utils/request'

/**
 * 获取评价列表
 */
export function getReviewList(params) {
  return request({
    url: '/admin/review/list',
    method: 'get',
    params
  })
}

/**
 * 获取评价详情
 */
export function getReviewById(id) {
  return request({
    url: `/admin/review/${id}`,
    method: 'get'
  })
}

/**
 * 更新评价状态
 */
export function updateReviewStatus(id, status) {
  return request({
    url: `/admin/review/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除评价
 */
export function deleteReview(id) {
  return request({
    url: `/admin/review/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除评价
 */
export function batchDeleteReviews(ids) {
  return request({
    url: '/admin/review/batch',
    method: 'delete',
    data: { ids }
  })
}