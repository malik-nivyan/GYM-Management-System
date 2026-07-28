import http from './http'

export const membersApi = {
  list: () => http.get('/members').then((r) => r.data),
  get: (id) => http.get(`/members/${id}`).then((r) => r.data),
  create: (payload) => http.post('/members', payload).then((r) => r.data),
  update: (id, payload) => http.put(`/members/${id}`, payload).then((r) => r.data),
  remove: (id) => http.delete(`/members/${id}`)
}

