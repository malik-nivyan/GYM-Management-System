import http from './http'

export const paymentsApi = {
  list: () => http.get('/payments').then((r) => r.data),
  create: (payload) => http.post('/payments', payload).then((r) => r.data),
  update: (id, payload) => http.put(`/payments/${id}`, payload).then((r) => r.data),
  remove: (id) => http.delete(`/payments/${id}`)
}

