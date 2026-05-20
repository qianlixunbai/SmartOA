<script setup>
import { ACTION_MAP, STEP_MAP } from '@/utils/constants'

defineProps({
  records: { type: Array, default: () => [] }
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<template>
  <el-timeline v-if="records.length > 0">
    <el-timeline-item
      v-for="r in records"
      :key="r.id"
      :timestamp="formatDate(r.createTime)"
      placement="top"
      :color="r.action === 'APPROVE' ? '#67c23a' : '#f56c6c'"
    >
      <p>
        <strong>{{ r.approver?.realName }}</strong>
        <el-tag size="small" type="info" style="margin-left:8px;margin-right:6px">
          {{ STEP_MAP[r.approvalStep]?.label || '未知节点' }}
        </el-tag>
        <el-tag :type="ACTION_MAP[r.action]?.type || 'info'" size="small">
          {{ ACTION_MAP[r.action]?.label || r.action }}
        </el-tag>
      </p>
      <p v-if="r.comment" class="timeline-comment">{{ r.comment }}</p>
    </el-timeline-item>
  </el-timeline>
  <el-empty v-else description="暂无审批记录" />
</template>

<style scoped>
.timeline-comment {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
</style>
