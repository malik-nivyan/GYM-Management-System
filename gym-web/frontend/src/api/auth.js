import http from './http'

export async function login(credentials) {
  const { data } = await http.post('/auth/login', credentials)
  return data
}

