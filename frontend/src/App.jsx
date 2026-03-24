import React, { useEffect, useMemo, useState } from 'react'
import { createUser, deleteUser, fetchUsers, updateUser } from './api/usersApi'

function emptyForm() {
  return { name: '', email: '', phone: '' }
}

export default function App() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [query, setQuery] = useState({
    page: 0,
    size: 10,
    sortBy: 'id',
    sortDir: 'asc',
    keyword: ''
  })
  const [pageMeta, setPageMeta] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    hasNext: false,
    hasPrevious: false
  })

  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm())

  const [error, setError] = useState('')
  const [submitLoading, setSubmitLoading] = useState(false)

  const formTitle = useMemo(() => {
    return editingId == null ? 'Thêm nhân viên' : `Sửa nhân viên (id: ${editingId})`
  }, [editingId])

  async function loadUsers(nextQuery) {
    const mergedQuery = { ...query, ...nextQuery }
    setLoading(true)
    setError('')
    try {
      const data = await fetchUsers(mergedQuery)
      setUsers(data.content || [])
      setPageMeta({
        page: data.page ?? mergedQuery.page,
        size: data.size ?? mergedQuery.size,
        totalElements: data.totalElements ?? 0,
        totalPages: data.totalPages ?? 0,
        hasNext: Boolean(data.hasNext),
        hasPrevious: Boolean(data.hasPrevious)
      })
      setQuery(mergedQuery)
    } catch (e) {
      setError(e.message || 'Không lấy được dữ liệu')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUsers()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function startEdit(user) {
    setEditingId(user.id)
    // Dùng form state để hiển thị dữ liệu lên input
    setForm({ name: user.name || '', email: user.email || '', phone: user.phone || '' })
    setError('')
  }

  function cancelEdit() {
    setEditingId(null)
    setForm(emptyForm())
    setError('')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitLoading(true)
    setError('')
    try {
      if (editingId == null) {
        // POST /api/users
        await createUser(form)
      } else {
        // PUT /api/users/{id}
        await updateUser(editingId, form)
      }
      await loadUsers({ page: 0 })
      cancelEdit()
    } catch (e) {
      setError(e.message || 'Gửi dữ liệu thất bại')
    } finally {
      setSubmitLoading(false)
    }
  }

  async function handleDelete(id) {
    const ok = window.confirm('Bạn có chắc chắn muốn xóa user này không?')
    if (!ok) return

    setError('')
    try {
      await deleteUser(id)
      await loadUsers()
    } catch (e) {
      setError(e.message || 'Xóa thất bại')
    }
  }

  return (
    <div className="container">
      <div className="header">
        <h1>Quản lý Nhân viên</h1>
        <button className="btn small" onClick={() => loadUsers()} disabled={loading}>
          {loading ? 'Đang tải...' : 'Làm mới'}
        </button>
      </div>

      <div className="grid">
        <div className="card">
          {error ? <div className="error">{error}</div> : null}
          <div className="toolbar">
            <input
              placeholder="Tìm theo tên, email, số điện thoại"
              value={query.keyword}
              onChange={(e) => setQuery((s) => ({ ...s, keyword: e.target.value }))}
            />
            <select
              value={query.sortBy}
              onChange={(e) => loadUsers({ page: 0, sortBy: e.target.value })}
            >
              <option value="id">Sắp xếp theo ID</option>
              <option value="name">Sắp xếp theo Tên</option>
              <option value="email">Sắp xếp theo Email</option>
              <option value="phone">Sắp xếp theo SĐT</option>
            </select>
            <select
              value={query.sortDir}
              onChange={(e) => loadUsers({ page: 0, sortDir: e.target.value })}
            >
              <option value="asc">Tăng dần</option>
              <option value="desc">Giảm dần</option>
            </select>
            <button className="btn small" onClick={() => loadUsers({ page: 0 })} disabled={loading}>
              Tìm
            </button>
          </div>

          <table className="table">
            <thead>
              <tr>
                <th style={{ width: 80 }}>ID</th>
                <th>Tên</th>
                <th style={{ width: 260 }}>Email</th>
                <th style={{ width: 180 }}>SĐT</th>
                <th style={{ width: 210 }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ color: '#6b7280', padding: '14px 8px' }}>
                    Chưa có dữ liệu. Hãy thêm nhân viên phía bên phải.
                  </td>
                </tr>
              ) : (
                users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.name}</td>
                    <td>{u.email}</td>
                    <td>{u.phone}</td>
                    <td>
                      <div className="row-actions">
                        <button className="btn small" onClick={() => startEdit(u)}>
                          Sửa
                        </button>
                        <button className="btn small danger" onClick={() => handleDelete(u.id)}>
                          Xóa
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
          <div className="pagination">
            <button
              className="btn small"
              disabled={loading || !pageMeta.hasPrevious}
              onClick={() => loadUsers({ page: Math.max(0, pageMeta.page - 1) })}
            >
              Trang trước
            </button>
            <span>
              Trang {pageMeta.totalPages === 0 ? 0 : pageMeta.page + 1}/{pageMeta.totalPages} - Tổng:{' '}
              {pageMeta.totalElements}
            </span>
            <button
              className="btn small"
              disabled={loading || !pageMeta.hasNext}
              onClick={() => loadUsers({ page: pageMeta.page + 1 })}
            >
              Trang sau
            </button>
          </div>
        </div>

        <div className="card">
          <h2 style={{ margin: '0 0 12px 0', fontSize: 16 }}>{formTitle}</h2>

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label>Tên</label>
              <input
                value={form.name}
                onChange={(e) => setForm((s) => ({ ...s, name: e.target.value }))}
                required
              />
            </div>

            <div className="field">
              <label>Email</label>
              <input
                value={form.email}
                onChange={(e) => setForm((s) => ({ ...s, email: e.target.value }))}
                type="email"
                required
              />
            </div>

            <div className="field">
              <label>Số điện thoại</label>
              <input
                value={form.phone}
                onChange={(e) => setForm((s) => ({ ...s, phone: e.target.value }))}
                required
              />
            </div>

            <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
              <button className="btn primary" type="submit" disabled={submitLoading}>
                {submitLoading ? 'Đang gửi...' : editingId == null ? 'Thêm' : 'Cập nhật'}
              </button>

              {editingId != null ? (
                <button type="button" className="btn" onClick={cancelEdit} disabled={submitLoading}>
                  Hủy
                </button>
              ) : null}
            </div>

            <div className="hint">
              Gợi ý: email bị trùng hoặc nhập sai định dạng sẽ trả lỗi từ backend (có message tiếng Việt).
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

