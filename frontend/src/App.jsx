import React, { useEffect, useMemo, useState } from 'react'
import { clearAuth, getStoredAuth, login as apiLogin } from './api/authApi'
import {
  bulkDeleteUsers,
  createUser,
  deleteUser,
  exportUsersCsv,
  fetchUsers,
  importUsersCsv,
  updateUser
} from './api/usersApi'

function emptyForm() {
  return { name: '', email: '', phone: '' }
}

export default function App() {
  const [auth, setAuth] = useState(() => getStoredAuth())
  const [loginForm, setLoginForm] = useState({ username: '', password: '' })
  const [loginLoading, setLoginLoading] = useState(false)
  const [loginError, setLoginError] = useState('')

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
  const [successMsg, setSuccessMsg] = useState('')
  const [submitLoading, setSubmitLoading] = useState(false)
  const [selectedIds, setSelectedIds] = useState([])
  const [csvBusy, setCsvBusy] = useState(false)

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
      setSelectedIds([])
    } catch (e) {
      setError(e.message || 'Không lấy được dữ liệu')
    } finally {
      setLoading(false)
    }
  }

  const isAdmin = auth.role === 'ADMIN'

  useEffect(() => {
    if (!auth.token) return
    loadUsers()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth.token])

  async function handleLogin(e) {
    e.preventDefault()
    setLoginLoading(true)
    setLoginError('')
    try {
      await apiLogin(loginForm)
      setAuth(getStoredAuth())
      setLoginForm({ username: '', password: '' })
    } catch (err) {
      setLoginError(err.message || 'Đăng nhập thất bại')
    } finally {
      setLoginLoading(false)
    }
  }

  function handleLogout() {
    clearAuth()
    setAuth({ token: null, role: null, username: null })
    setUsers([])
    setError('')
    setSuccessMsg('')
  }

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
    setSuccessMsg('')
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
    setSuccessMsg('')
    try {
      await deleteUser(id)
      await loadUsers()
    } catch (e) {
      setError(e.message || 'Xóa thất bại')
    }
  }

  function toggleSelect(id) {
    setSelectedIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  function toggleSelectAll() {
    if (users.length === 0) return
    if (selectedIds.length === users.length) {
      setSelectedIds([])
    } else {
      setSelectedIds(users.map((u) => u.id))
    }
  }

  async function handleBulkDelete() {
    if (selectedIds.length === 0) return
    const ok = window.confirm(`Xóa mềm ${selectedIds.length} nhân viên đã chọn?`)
    if (!ok) return
    setError('')
    setSuccessMsg('')
    try {
      await bulkDeleteUsers(selectedIds)
      await loadUsers()
    } catch (e) {
      setError(e.message || 'Xóa hàng loạt thất bại')
    }
  }

  async function handleExportCsv() {
    setCsvBusy(true)
    setError('')
    setSuccessMsg('')
    try {
      await exportUsersCsv()
    } catch (e) {
      setError(e.message || 'Xuất CSV thất bại')
    } finally {
      setCsvBusy(false)
    }
  }

  async function handleImportCsv(e) {
    const file = e.target.files && e.target.files[0]
    e.target.value = ''
    if (!file) return
    setCsvBusy(true)
    setError('')
    setSuccessMsg('')
    try {
      const result = await importUsersCsv(file)
      const msg =
        result && typeof result.imported === 'number'
          ? `Đã import ${result.imported} dòng, bỏ qua ${result.skipped ?? 0}.`
          : 'Import xong.'
      setSuccessMsg(msg)
      await loadUsers({ page: 0 })
    } catch (err) {
      setError(err.message || 'Import CSV thất bại')
    } finally {
      setCsvBusy(false)
    }
  }

  if (!auth.token) {
    return (
      <div className="container">
        <div className="header">
          <h1>Quản lý Nhân viên</h1>
        </div>
        <div className="card" style={{ maxWidth: 420, margin: '24px auto' }}>
          <h2 style={{ marginTop: 0, fontSize: 18 }}>Đăng nhập</h2>
          {loginError ? <div className="error">{loginError}</div> : null}
          <form onSubmit={handleLogin}>
            <div className="field">
              <label>Tên đăng nhập</label>
              <input
                value={loginForm.username}
                onChange={(e) => setLoginForm((s) => ({ ...s, username: e.target.value }))}
                autoComplete="username"
                required
              />
            </div>
            <div className="field">
              <label>Mật khẩu</label>
              <input
                type="password"
                value={loginForm.password}
                onChange={(e) => setLoginForm((s) => ({ ...s, password: e.target.value }))}
                autoComplete="current-password"
                required
              />
            </div>
            <button className="btn primary" type="submit" disabled={loginLoading}>
              {loginLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </button>
          </form>
          <p className="hint" style={{ marginTop: 16 }}>
            Demo: admin / Admin@123 (ADMIN, được xóa) hoặc user / User@123 (USER, không xóa được).
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="container">
      <div className="header">
        <h1>Quản lý Nhân viên</h1>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
          <span style={{ color: '#6b7280', fontSize: 14 }}>
            {auth.username} ({auth.role})
          </span>
          <button className="btn small" onClick={() => loadUsers()} disabled={loading}>
            {loading ? 'Đang tải...' : 'Làm mới'}
          </button>
          <button className="btn small" type="button" onClick={handleLogout}>
            Đăng xuất
          </button>
        </div>
      </div>

      <div className="grid">
        <div className="card">
          {error ? <div className="error">{error}</div> : null}
          {successMsg ? <div className="success">{successMsg}</div> : null}
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
              <option value="createdAt">Sắp xếp theo tạo lúc</option>
              <option value="updatedAt">Sắp xếp theo cập nhật</option>
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
            {isAdmin ? (
              <>
                <button className="btn small" type="button" disabled={loading || csvBusy} onClick={handleExportCsv}>
                  Xuất CSV
                </button>
                <label className="btn small" style={{ cursor: csvBusy ? 'wait' : 'pointer' }}>
                  Import CSV
                  <input
                    type="file"
                    accept=".csv,text/csv"
                    style={{ display: 'none' }}
                    disabled={csvBusy}
                    onChange={handleImportCsv}
                  />
                </label>
                <button
                  className="btn small danger"
                  type="button"
                  disabled={loading || selectedIds.length === 0}
                  onClick={handleBulkDelete}
                >
                  Xóa đã chọn ({selectedIds.length})
                </button>
              </>
            ) : null}
          </div>

          <table className="table">
            <thead>
              <tr>
                {isAdmin ? (
                  <th style={{ width: 44 }}>
                    <input
                      type="checkbox"
                      checked={users.length > 0 && selectedIds.length === users.length}
                      onChange={toggleSelectAll}
                    />
                  </th>
                ) : null}
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
                  <td colSpan={isAdmin ? 6 : 5} style={{ color: '#6b7280', padding: '14px 8px' }}>
                    Chưa có dữ liệu. Hãy thêm nhân viên phía bên phải.
                  </td>
                </tr>
              ) : (
                users.map((u) => (
                  <tr key={u.id}>
                    {isAdmin ? (
                      <td>
                        <input
                          type="checkbox"
                          checked={selectedIds.includes(u.id)}
                          onChange={() => toggleSelect(u.id)}
                        />
                      </td>
                    ) : null}
                    <td>{u.id}</td>
                    <td>{u.name}</td>
                    <td>{u.email}</td>
                    <td>{u.phone}</td>
                    <td>
                      <div className="row-actions">
                        <button className="btn small" onClick={() => startEdit(u)}>
                          Sửa
                        </button>
                        {isAdmin ? (
                          <button className="btn small danger" onClick={() => handleDelete(u.id)}>
                            Xóa
                          </button>
                        ) : (
                          <span className="hint" style={{ fontSize: 12 }}>
                            Chỉ ADMIN xóa
                          </span>
                        )}
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

