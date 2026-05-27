import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { layout: 'auth', guest: true, title: '登录' }
  },
  {
    path: '/',
    redirect: '/submit-application'
  },
  {
    path: '/submit-application',
    name: 'SubmitApplication',
    component: () => import('@/views/SubmitApplicationPage.vue'),
    meta: { title: '提交申请', requiresAuth: true }
  },
  {
    path: '/my-approvals',
    name: 'MyApprovals',
    component: () => import('@/views/MyApprovalsPage.vue'),
    meta: { title: '我的审批', requiresAuth: true }
  },
  {
    path: '/approval/:id',
    name: 'ApprovalDetail',
    component: () => import('@/views/ApprovalDetailPage.vue'),
    meta: { title: '审批详情', requiresAuth: true }
  },
  {
    path: '/templates',
    name: 'TemplateList',
    component: () => import('@/views/TemplateListPage.vue'),
    meta: { title: '审批模板', requiresAuth: true, role: 'MANAGER' }
  },
  {
    path: '/templates/edit/:id?',
    name: 'TemplateEdit',
    component: () => import('@/views/TemplateEditPage.vue'),
    meta: { title: '编辑模板', requiresAuth: true, role: 'MANAGER' }
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import('@/views/StatsPage.vue'),
    meta: { title: '统计报表', requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundPage.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()

  if (auth.token && !auth.user) {
    try {
      await auth.fetchUser()
    } catch {
      auth.logout()
      return next('/login')
    }
  }

  if (to.meta.requiresAuth && !auth.token) {
    return next('/login')
  }

  if (to.meta.guest && auth.token) {
    return next('/submit-application')
  }

  if (to.meta.role && auth.user?.role !== to.meta.role) {
    return next('/submit-application')
  }

  next()
})

export default router
