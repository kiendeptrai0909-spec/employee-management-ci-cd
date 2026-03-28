const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function getErrorMessage(payload) {
  if (!payload) return 'Có lỗi xảy ra'
  if (typeof payload.message === 'string') return payload.message
  if (payload.error && typeof payload.error.message === 'string') return payload.error.message
  return 'Có lỗi xảy ra'
}

function unwrapData(payload) {
  if (payload && typeof payload === 'object' && 'data' in payload) return payload.data
  return payload
}

export function getStoredAuth() {
  return {
    token: localStorage.getItem('token'),
    role: localStorage.getItem('role'),
    username: localStorage.getItem('username')
  }
}

export function setStoredAuth({ token, role, username }) {
  if (token) localStorage.setItem('token', token)
  if (role) localStorage.setItem('role', role)
  if (username) localStorage.setItem('username', username)
}

export function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
}

export function authHeaders() {
  const { token } = getStoredAuth()
  const h = { 'Content-Type': 'application/json' }
  if (token) h.Authorization = `Bearer ${token}`
  return h
}

export async function login({ username, password }) {
  const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  const data = unwrapData(payload)
  if (!data || !data.accessToken) throw new Error('Phản hồi đăng nhập không hợp lệ')
  setStoredAuth({
    token: data.accessToken,
    role: data.role,
    username: data.username
  })
  return data
}
