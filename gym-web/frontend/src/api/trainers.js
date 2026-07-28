import http from './http'

export const trainersApi = {
  list: () => http.get('/trainers').then((r) => r.data),
  create: (payload) => http.post('/trainers', payload).then((r) => r.data),
  update: (id, payload) => http.put(`/trainers/${id}`, payload).then((r) => r.data),
  remove: (id) => http.delete(`/trainers/${id}`)
}

