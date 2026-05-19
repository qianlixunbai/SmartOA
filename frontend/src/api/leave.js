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

export function getRequestDetail(id) {
  return api.get(`/leave/${id}`)
}

export function getApprovalRecords(id) {
  return api.get(`/leave/${id}/records`)
}
