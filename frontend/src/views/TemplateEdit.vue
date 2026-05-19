<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTemplateById, createTemplate, updateTemplate } from '@/api/template'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  name: '',
  description: '',
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
}

onMounted(async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const data = await getTemplateById(Number(id))
    form.name = data.name
    form.description = data.description
    form.enabled = data.enabled
  }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await updateTemplate(Number(route.params.id), { ...form })
      ElMessage.success('更新成功')
    } else {
      await createTemplate({ ...form })
      ElMessage.success('创建成功')
    }
    router.push('/templates')
  } catch {
    // handled by interceptor
  }
}
</script>

<template>
  <div class="page">
    <el-card shadow="never">
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
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="router.push('/templates')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page { max-width: 600px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.form { margin-top: 10px; }
</style>
