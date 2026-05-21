<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTemplateById, createTemplate, updateTemplate, getTemplateNodes, saveTemplateNodes } from '@/api/template'
import { useUserStore } from '@/stores/users'
import { APPROVER_TYPES } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)

const form = reactive({
  name: '',
  description: '',
  enabled: true
})

const nodes = ref([])

const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
}

onMounted(async () => {
  await userStore.fetchUsers()
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const data = await getTemplateById(Number(id))
    form.name = data.name
    form.description = data.description
    form.enabled = data.enabled
    nodes.value = await getTemplateNodes(Number(id))
  }
})

function addNode() {
  nodes.value.push({
    nodeName: '新节点',
    sortOrder: nodes.value.length,
    approverType: 'DIRECT_LEADER',
    approverId: null
  })
}

function removeNode(index) {
  nodes.value.splice(index, 1)
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const data = { ...form }
    if (isEdit.value) {
      await updateTemplate(Number(route.params.id), data)
      await saveTemplateNodes(Number(route.params.id), nodes.value)
      ElMessage.success('更新成功')
    } else {
      await createTemplate(data)
      // 新建后跳回列表（id 未知，暂不保存节点）
      ElMessage.success('创建成功，请编辑模板来配置审批节点')
    }
    router.push('/templates')
  } catch {
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑模板' : '新建模板' }}</span>
          <el-button @click="router.push('/templates')">返回列表</el-button>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="form">
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入模板描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.enabled" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审批节点配置（仅编辑模式） -->
    <el-card v-if="isEdit" shadow="never" class="mb-20">
      <template #header>
        <div class="card-header">
          <span>审批节点（{{ nodes.length }} 级）</span>
          <el-button type="primary" size="small" @click="addNode">添加节点</el-button>
        </div>
      </template>
      <el-empty v-if="nodes.length === 0" description="暂无审批节点，请点击添加" />
      <div v-else class="node-list">
        <div v-for="(node, idx) in nodes" :key="idx" class="node-item">
          <el-tag type="info" size="small" class="node-order">第 {{ idx + 1 }} 步</el-tag>
          <el-input v-model="node.nodeName" placeholder="节点名称" style="width:180px" />
          <el-select v-model="node.approverType" style="width:140px">
            <el-option v-for="at in APPROVER_TYPES" :key="at.value" :label="at.label" :value="at.value" />
          </el-select>
          <el-select
            v-if="node.approverType === 'SPECIFIC_USER'"
            v-model="node.approverId"
            placeholder="选择用户"
            style="width:150px"
          >
            <el-option v-for="u in userStore.users" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
          <el-button type="danger" size="small" @click="removeNode(idx)">删除</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      <el-button @click="router.push('/templates')">取消</el-button>
    </el-card>
  </div>
</template>

<style scoped>
.page { max-width: 800px; margin: 0 auto; }
.mb-20 { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.form { margin-top: 10px; }
.node-list { display: flex; flex-direction: column; gap: 10px; }
.node-item { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.node-order { width: 60px; text-align: center; }
</style>
