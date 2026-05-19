<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { DocumentAdd, List, Setting, Plus } from '@element-plus/icons-vue'

const route = useRoute()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

const menuItems = [
  { path: '/submit-application', title: '提交申请', icon: DocumentAdd },
  { path: '/my-approvals', title: '我的审批', icon: List }
]
</script>

<template>
  <div class="sidebar">
    <div class="sidebar-logo">SmartOA</div>
    <el-menu
      :default-active="activeMenu"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
    >
      <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.title }}</span>
      </el-menu-item>

      <el-sub-menu v-if="auth.isManager" index="/templates-group">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>模板管理</span>
        </template>
        <el-menu-item index="/templates">
          <el-icon><List /></el-icon>
          <span>模板列表</span>
        </el-menu-item>
        <el-menu-item index="/templates/edit">
          <el-icon><Plus /></el-icon>
          <span>新建模板</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<style scoped>
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  border-bottom: 1px solid rgba(255,255,255,.1);
}
.el-menu {
  border-right: none;
}
</style>
