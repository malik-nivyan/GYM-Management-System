import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import LoginView from '../views/LoginView.vue'
import MembersView from '../views/MembersView.vue'
import MembershipsView from '../views/MembershipsView.vue'
import PaymentsView from '../views/PaymentsView.vue'
import TrainersView from '../views/TrainersView.vue'

const routes = [
  { path: '/login', name: 'login', component: LoginView, meta: { public: true } },
  { path: '/', redirect: '/members' },
  { path: '/members', name: 'members', component: MembersView },
  { path: '/memberships', name: 'memberships', component: MembershipsView, meta: { roles: ['ADMIN'] } },
  { path: '/payments', name: 'payments', component: PaymentsView },
  { path: '/trainers', name: 'trainers', component: TrainersView, meta: { roles: ['ADMIN'] } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthenticated.value) {
    return { name: 'login' }
  }
  if (to.name === 'login' && auth.isAuthenticated.value) {
    return { name: 'members' }
  }
  const allowedRoles = to.meta.roles
  const currentRole = auth.state.user?.role
  if (allowedRoles && !allowedRoles.includes(currentRole)) {
    return { name: 'members' }
  }
  return true
})

export default router


