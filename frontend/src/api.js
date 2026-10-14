import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
})

export const shortenUrl = (payload) => api.post('/api/shorten', payload).then((r) => r.data)
export const fetchRecent = () => api.get('/api/urls').then((r) => r.data)
export const deleteUrl = (code) => api.delete(`/api/urls/${code}`)
