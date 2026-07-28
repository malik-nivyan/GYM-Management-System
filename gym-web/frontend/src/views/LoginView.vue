<template>
  <div class="container py-5" style="max-width: 420px">
    <div class="card shadow-sm border-0">
      <div class="card-body p-4">
        <h1 class="h4 fw-bold mb-1">Gym Admin Login</h1>
        <p class="text-secondary small mb-4">Sign in to continue.</p>

        <form @submit.prevent="submit">
          <div class="mb-3">
            <label class="form-label">Username</label>
            <input v-model.trim="form.username" class="form-control" required />
          </div>

          <div class="mb-3">
            <label class="form-label">Password</label>
            <input v-model="form.password" type="password" class="form-control" required />
          </div>

          <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

          <button class="btn btn-success w-100" :disabled="busy">
            {{ busy ? 'Signing in...' : 'Login' }}
          </button>
        </form>

        <p class="small text-secondary mt-3 mb-0">
          Demo: <code>admin / nivyan</code> or <code>staff / staffpass</code>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ username: '', password: '' })
const error = ref('')
const busy = ref(false)

async function submit() {
  error.value = ''
  busy.value = true
  try {
    const data = await login(form)
    auth.setAuth(data)
    router.push('/members')
  } catch (err) {
    error.value = err?.response?.data?.error || 'Invalid username or password'
  } finally {
    busy.value = false
  }
}
</script>

