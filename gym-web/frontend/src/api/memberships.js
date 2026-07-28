import http from './http'

export const membershipsApi = {
  list: () => http.get('/memberships').then((r) => r.data),
  create: (payload) => http.post('/memberships', payload).then((r) => r.data),
  update: (id, payload) => http.put(`/memberships/${id}`, payload).then((r) => r.data),
  remove: (id) => http.delete(`/memberships/${id}`)
}

