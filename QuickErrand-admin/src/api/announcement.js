import request from '@/utils/request'

/**
 * 获取公告列表
 */
export function getAnnouncementList(params) {
  return request({
    url: '/admin/announcement/list',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 */
export function getAnnouncementById(id) {
  return request({
    url: `/admin/announcement/${id}`,
    method: 'get'
  })
}

/**
 * 创建公告
 */
export function createAnnouncement(data) {
  return request({
    url: '/admin/announcement',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 */
export function updateAnnouncement(id, data) {
  return request({
    url: `/admin/announcement/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/admin/announcement/${id}`,
    method: 'delete'
  })
}

/**
 * 更新公告状态
 */
export function updateAnnouncementStatus(id, status) {
  return request({
    url: `/admin/announcement/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 更新公告置顶状态
 */
export function updateAnnouncementTop(id, isTop) {
  return request({
    url: `/admin/announcement/${id}/top`,
    method: 'put',
    params: { isTop }
  })
}

/**
 * 批量删除公告
 */
export function batchDeleteAnnouncements(ids) {
  return request({
    url: '/admin/announcement/batch',
    method: 'delete',
    data: { ids }
  })
}