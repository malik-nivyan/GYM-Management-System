import { reactive, computed } from 'vue'

const TOKEN_KEY = 'gym_token'
const USER_KEY = 'gym_user'

const state = reactive({
  token: localStorage.getItem(TOKEN_KEY) || '',
  user: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
})

function setAuth(payload) {
  state.token = payload.token
  state.user = {
    username: payload.username,
    role: payload.role
  }
  localStorage.setItem(TOKEN_KEY, state.token)
  localStorage.setItem(USER_KEY, JSON.stringify(state.user))
}

function clearAuth() {
  state.token = ''
  state.user = null
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function useAuthStore() {
  return {
    state,
    isAuthenticated: computed(() => Boolean(state.token)),
    setAuth,
    clearAuth
  }
}

