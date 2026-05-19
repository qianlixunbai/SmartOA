<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import LeaveTable from '@/components/LeaveTable.vue'
import { useAuthStore } from '@/stores/auth'
import { useApprovalStore } from '@/stores/approval'

const auth = useAuthStore()
const store = useApprovalStore()
const router = useRouter()
const activeTab = ref(auth.isManager ? 'pending' : 'my')

const pendingColumns = [
  { prop: 'applicant.realName', label: '申请人', width: 100 },
  { prop: 'applicant.department', label: '部门', width: 100 },
  { prop: 'leaveType', label: '类型', width: 80 },
  { prop: 'dates', label: '日期范围', width: 200 },
  { prop: 'reason', label: '原因', minWidth: 150 },
  { prop: 'createTime', label: '提交时间', width: 160 },
  { prop: 'status', label: '状态', width: 100 }
]

function handleApprove(id) {
  router.push(`/approval/${id}`)
}

onMounted(() => {
  store.fetchMyRequests()
  if (auth.isManager) {
    store.fetchPendingRequests()
  }
})
</script>

<template>
  <div class="page">
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane v-if="auth.isManager" label="待审批" name="pending">
          <LeaveTable
            :data="store.pendingRequests"
            :columns="pendingColumns"
            :show-actions="true"
            action-type="approve"
            @action="handleApprove"
          />
        </el-tab-pane>
        <el-tab-pane v-if="auth.isManager" label="已处理" name="done">
          <LeaveTable :data="store.doneRequests" :show-actions="true" action-type="view" />
        </el-tab-pane>
        <el-tab-pane label="我的申请" name="my">
          <LeaveTable :data="store.myRequests" :show-actions="true" action-type="view" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; }
</style>
