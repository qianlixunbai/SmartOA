import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as leaveApi from '@/api/leave'

export const useApprovalStore = defineStore('approval', () => {
  const myRequests = ref([])
  const pendingRequests = ref([])
  const doneRequests = ref([])
  const currentDetail = ref(null)
  const currentRecords = ref([])
  const loading = ref(false)

  async function fetchMyRequests() {
    myRequests.value = await leaveApi.getMyRequests()
  }

  async function fetchPendingRequests() {
    pendingRequests.value = await leaveApi.getPendingRequests()
  }

  async function fetchDoneRequests() {
    const all = await leaveApi.getPendingRequests()
    doneRequests.value = all.filter(r => r.status !== 'PENDING')
  }

  async function fetchDetail(id) {
    currentDetail.value = await leaveApi.getRequestDetail(id)
  }

  async function fetchRecords(id) {
    currentRecords.value = await leaveApi.getApprovalRecords(id)
  }

  async function submitLeave(formData) {
    await leaveApi.submitLeave(formData)
    await fetchMyRequests()
  }

  async function approve(requestId, action, comment) {
    await leaveApi.approveLeave(requestId, action, comment)
  }

  return {
    myRequests, pendingRequests, doneRequests,
    currentDetail, currentRecords, loading,
    fetchMyRequests, fetchPendingRequests, fetchDoneRequests,
    fetchDetail, fetchRecords, submitLeave, approve
  }
})
