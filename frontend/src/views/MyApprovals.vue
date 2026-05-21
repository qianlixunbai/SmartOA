<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LeaveTable from '@/components/LeaveTable.vue'
import { useAuthStore } from '@/stores/auth'
import { useApprovalStore } from '@/stores/approval'
import { useUserStore } from '@/stores/users'

const auth = useAuthStore()
const store = useApprovalStore()
const userStore = useUserStore()
const router = useRouter()
const activeTab = ref(auth.isManager ? 'pending' : 'my')

const pendingColumns = [
  { prop: 'applicantId', label: '申请人', width: 100, isUser: true },
  { prop: 'leaveType', label: '类型', width: 80 },
  { prop: 'dates', label: '日期范围', width: 200 },
  { prop: 'reason', label: '原因', minWidth: 150 },
  { prop: 'stepLabel', label: '当前节点', width: 140 },
  { prop: 'createTime', label: '提交时间', width: 160 },
  { prop: 'status', label: '状态', width: 100 }
]

const doneColumns = [
  { prop: 'applicantId', label: '申请人', width: 100, isUser: true },
  { prop: 'leaveType', label: '类型', width: 80 },
  { prop: 'dates', label: '日期范围', width: 200 },
  { prop: 'reason', label: '原因', minWidth: 150 },
  { prop: 'myAction', label: '我的审批', width: 100 },
  { prop: 'createTime', label: '提交时间', width: 160 },
  { prop: 'status', label: '状态', width: 100 }
]

function handleApprove(id) {
  router.push(`/approval/${id}`)
}

function handleView(id) {
  router.push(`/approval/${id}`)
}

onMounted(async () => {
  await userStore.fetchUsers()
  store.fetchMyRequests()
  if (auth.isManager) {
    store.fetchPendingRequests()
    store.fetchDoneRequests()
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
            :show-actions="true"
            action-type="approve"
            @action="handleApprove"
          >
            <template #stepLabel="{ row }">
              <el-tag size="small" type="warning">第{{ row.approvalStep + 1 }}步</el-tag>
            </template>
            <template #myAction="{ row }">
              <el-tag size="small" type="info">待处理</el-tag>
            </template>
          </LeaveTable>
        </el-tab-pane>
        <el-tab-pane v-if="auth.isManager" label="已处理" name="done">
          <LeaveTable
            :data="store.doneRequests"
            :show-actions="true"
            action-type="view"
            @action="handleView"
          >
            <template #myAction="{ row }">
              <el-tag size="small" :type="row.status === 'APPROVED' ? 'success' : 'danger'">
                {{ row.status === 'APPROVED' ? '已通过' : '已驳回' }}
              </el-tag>
            </template>
          </LeaveTable>
        </el-tab-pane>
        <el-tab-pane label="我的申请" name="my">
          <LeaveTable
            :data="store.myRequests"
            :show-actions="true"
            action-type="view"
            @action="handleView"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; }
</style>
