<template>
  <div class="app-shell">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-4">
      <div class="container-fluid p-0">
        <span class="navbar-brand fw-bold">TITAN-FORGE</span>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
          <span class="navbar-toggler-icon" />
        </button>
        <div id="mainNav" class="collapse navbar-collapse">
          <ul class="navbar-nav me-auto">
            <li class="nav-item"><RouterLink class="nav-link" to="/members">Members</RouterLink></li>
            <li class="nav-item"><RouterLink class="nav-link" to="/payments">Payments</RouterLink></li>
            <li v-if="isAdmin" class="nav-item"><RouterLink class="nav-link" to="/memberships">Memberships</RouterLink></li>
            <li v-if="isAdmin" class="nav-item"><RouterLink class="nav-link" to="/trainers">Trainers</RouterLink></li>
          </ul>
          <div class="d-flex align-items-center gap-3 text-light small">
            <span>{{ auth.state.user?.username }} ({{ auth.state.user?.role }})</span>
            <button class="btn btn-outline-light btn-sm" @click="logout">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const isAdmin = computed(() => auth.state.user?.role === 'ADMIN')

function logout() {
  auth.clearAuth()
  router.push('/login')
}
</script>


