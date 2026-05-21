import api from './index'

export function submitLeave(data) {
  return api.post('/leave/submit', data)
}

export function approveLeave(requestId, action, comment) {
  return api.post('/leave/approve', { requestId, action, comment })
}

export function getMyRequests() {
  return api.get('/leave/my-requests')
}

export function getPendingRequests() {
  return api.get('/leave/pending')
}

export function getDoneRequests() {
  return api.get('/leave/done')
}

export function getRequestDetail(id) {
  return api.get(`/leave/${id}`)
}

export function getApprovalRecords(id) {
  return api.get(`/leave/${id}/records`)
}

export function withdrawLeave(id) {
  return api.post(`/leave/${id}/withdraw`)
}

export function transferLeave(id, toUserId) {
  return api.post(`/leave/${id}/transfer`, { toUserId })
}
