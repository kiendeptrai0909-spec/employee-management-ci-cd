const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function getErrorMessage(payload) {
  if (!payload) return 'Có lỗi xảy ra'
  if (typeof payload.message === 'string') return payload.message
  if (Array.isArray(payload.details) && payload.details.length > 0) return payload.details.join('\n')
  return 'Có lỗi xảy ra'
}

export async function fetchUsers() {
  const res = await fetch(`${API_BASE_URL}/api/users`)
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return payload
}

export async function createUser({ name, email, phone }) {
  const res = await fetch(`${API_BASE_URL}/api/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, phone })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return payload
}

export async function updateUser(id, { name, email, phone }) {
  const res = await fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, phone })
  })
  const payload = await res.json().catch(() => null)
  if (!res.ok) throw new Error(getErrorMessage(payload))
  return payload
}

export async function deleteUser(id) {
  const res = await fetch(`${API_BASE_URL}/api/users/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    const payload = await res.json().catch(() => null)
    throw new Error(getErrorMessage(payload))
  }
}

