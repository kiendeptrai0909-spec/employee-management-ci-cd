import { authHeaders, authHeadersMultipart } from './authApi'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function getErrorMessage(payload) {
  if (!payload) return 'Có lỗi xảy ra'
  if (payload.error && typeof payload.error.message === 'string') return payload.error.message
  if (typeof payload.message === 'string') return payload.message
  if (Array.isArray(payload.details) && payload.details.length > 0) return payload.details.join('\n')
  return 'Có lỗi xảy ra'
}

function unwrapData(payload) {
  if (payload && typeof payload === 'object' && 'data' in payload) return payload.data
  return payload
}

export async function fetchUsers({ page = 0, size = 10, sortBy = 'id', sortDir = 'asc', keyword = '' } = {}) {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    sortBy,
    sortDir
  })
  if (keyword.trim()) params.set('keyword', keyword.trim())

  const res = await fetch(`${API_BASE_URL}/api/users?${params.toString()}`, {
    headers: authHeaders()
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  const data = unwrapData(payload)
  if (Array.isArray(data)) {
    return {
      content: data,
      page,
      size,
      totalElements: data.length,
      totalPages: data.length > 0 ? 1 : 0,
      hasNext: false,
      hasPrevious: false
    }
  }
  return data
}

export async function createUser({ name, email, phone }) {
  const res = await fetch(`${API_BASE_URL}/api/users`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name, email, phone })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return unwrapData(payload)
}

export async function updateUser(id, { name, email, phone }) {
  const res = await fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify({ name, email, phone })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return unwrapData(payload)
}

export async function deleteUser(id) {
  const res = await fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    const payload = await res.json().catch(() => null)
    throw new Error(getErrorMessage(payload))
  }
}

export async function exportUsersCsv() {
  const res = await fetch(`${API_BASE_URL}/api/users/export/csv`, { headers: authHeadersMultipart() })
  if (!res.ok) {
    const payload = await res.json().catch(() => null)
    throw new Error(getErrorMessage(payload))
  }
  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'users.csv'
  a.click()
  URL.revokeObjectURL(url)
}

export async function importUsersCsv(file) {
  const fd = new FormData()
  fd.append('file', file)
  const res = await fetch(`${API_BASE_URL}/api/users/import/csv`, {
    method: 'POST',
    headers: authHeadersMultipart(),
    body: fd
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return unwrapData(payload)
}

export async function bulkDeleteUsers(ids) {
  const res = await fetch(`${API_BASE_URL}/api/users/bulk`, {
    method: 'DELETE',
    headers: authHeaders(),
    body: JSON.stringify({ ids })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return unwrapData(payload)
}
