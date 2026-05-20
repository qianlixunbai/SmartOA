<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import StatusTag from '@/components/StatusTag.vue'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'
import { useAuthStore } from '@/stores/auth'
import { useApprovalStore } from '@/stores/approval'
import { STEP_LABELS, STATUS_MAP } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const store = useApprovalStore()
const comment = ref('')
const submitting = ref(false)

const id = computed(() => Number(route.params.id))

const canApprove = computed(() => {
  const detail = store.currentDetail
  if (!detail) return false
  return detail.status === 'PENDING' && detail.currentApproverId === auth.user?.id
})

const activeStep = computed(() => {
  const detail = store.currentDetail
  if (!detail) return 0
  if (detail.status === 'REJECTED') return detail.approvalStep
  if (detail.status === 'APPROVED') return 2
  return detail.approvalStep
})

const stepStatus = (step) => {
  const detail = store.currentDetail
  if (!detail) return 'wait'
  if (detail.status === 'REJECTED') {
    if (step < detail.approvalStep) return 'finish'
    if (step === detail.approvalStep) return 'error'
    return 'wait'
  }
  if (detail.status === 'APPROVED') return step <= 2 ? 'finish' : 'wait'
  // PENDING
  if (step < detail.approvalStep) return 'finish'
  if (step === detail.approvalStep) return 'process'
  return 'wait'
}

async function handleApprove(action) {
  submitting.value = true
  try {
    await store.approve(id.value, action, comment.value)
    ElMessage.success(action === 'APPROVE' ? '已通过' : '已驳回')
    router.push('/my-approvals')
  } catch {
    // handled by interceptor
  } finally {
    submitting.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(async () => {
  await Promise.all([store.fetchDetail(id.value), store.fetchRecords(id.value)])
})
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>请假单详情</span>
          <el-button @click="router.back()">返回</el-button>
        </div>
      </template>

      <!-- 审批进度 -->
      <el-steps :active="activeStep" finish-status="success" align-center class="mb-20">
        <el-step
          v-for="(label, idx) in STEP_LABELS"
          :key="idx"
          :title="label"
          :status="stepStatus(idx)"
        />
      </el-steps>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请人">{{ store.currentDetail?.applicant?.realName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ store.currentDetail?.applicant?.department }}</el-descriptions-item>
        <el-descriptions-item label="请假类型">{{ store.currentDetail?.leaveType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag v-if="store.currentDetail" :status="store.currentDetail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ store.currentDetail?.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ store.currentDetail?.endDate }}</el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">{{ store.currentDetail?.reason }}</el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ formatDate(store.currentDetail?.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="canApprove" shadow="never" class="mb-20">
      <template #header><span>审批操作</span></template>
      <el-input
        v-model="comment"
        type="textarea"
        :rows="3"
        placeholder="请输入审批意见（可选）"
        class="mb-16"
      />
      <div class="approve-actions">
        <el-button type="success" :loading="submitting" @click="handleApprove('APPROVE')">通过</el-button>
        <el-button type="danger" :loading="submitting" @click="handleApprove('REJECT')">驳回</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header><span>审批记录</span></template>
      <ApprovalTimeline :records="store.currentRecords" />
    </el-card>
  </div>
</template>

<style scoped>
.page { max-width: 800px; margin: 0 auto; }
.mb-20 { margin-bottom: 20px; }
.mb-16 { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.approve-actions { display: flex; gap: 12px; justify-content: flex-end; }
</style>
