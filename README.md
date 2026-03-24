# Employee Management (Spring Boot + React + MySQL)

Website Quan ly Nhan Vien voi CRUD du lieu bang `users (id, name, email, phone)`.

## 1) Cong nghe su dung
- Backend: Spring Boot + Spring Data JPA + Validation
- Frontend: ReactJS (Vite)
- API: REST API
- Database: MySQL
- CI/CD: GitHub Actions + Deploy Hooks (Render/Vercel)

## 2) Link deploy
- Frontend: https://employee-management-ci-cd.vercel.app/
- Backend: https://employee-management-ci-cd.onrender.com/
- GitHub: https://github.com/kiendeptrai0909-spec/employee-management-ci-cd.git

## 3) Mo ta chuc nang
He thong quan ly nhan vien cho phep:
- Hien thi danh sach nhan vien (Read all)
- Xem chi tiet nhan vien theo id (Read one)
- Them nhan vien moi (Create)
- Cap nhat thong tin nhan vien (Update)
- Xoa nhan vien (Delete)

## 4) CSDL
Bang: `users`
- `id` (PK, auto increment)
- `name`
- `email` (unique)
- `phone`

## 5) REST API
Base URL local: `http://localhost:8080`

- `GET /api/users`  
  Lay danh sach users

- `GET /api/users/{id}`  
  Lay user theo id

- `POST /api/users`  
  Tao user moi  
  Body:
  ```json
  {
    "name": "Nguyen Van A",
    "email": "a@gmail.com",
    "phone": "0901234567"
  }
  ```

- `PUT /api/users/{id}`  
  Cap nhat user theo id  
  Body:
  ```json
  {
    "name": "Nguyen Van B",
    "email": "b@gmail.com",
    "phone": "0912345678"
  }
  ```

- `DELETE /api/users/{id}`  
  Xoa user theo id

## 6) Chay backend local
1. Tao database MySQL: `employee_management`
2. Cau hinh bien moi truong (neu can):
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Chay Spring Boot:
   - Neu da cai Maven: `mvn spring-boot:run`
   - Hoac chay bang IDE tai class `EmployeeManagementApplication`

Luu y: thong so mac dinh datasource nam trong `backend/src/main/resources/application.properties`.

## 7) Chay frontend local
1. Mo terminal trong thu muc `frontend`
2. Cai dependencies: `npm install`
3. Chay dev server: `npm run dev`

Neu muon frontend goi backend deploy tren Render:
- Tao file `.env` trong `frontend` va them:
  - `VITE_API_BASE_URL=https://employee-management-ci-cd.onrender.com`
- Co the tham khao `frontend/.env.example`

## 8) CI (GitHub Actions)
Workflow: `/.github/workflows/ci.yml`
- Trigger khi `push` va `pull request` vao `main`
- Build backend bang Maven
- Build frontend bang Vite

## 9) CD (GitHub Actions + Deploy Hook)
Workflow: `/.github/workflows/deploy.yml`
- Trigger khi `push` vao `main` hoac `workflow_dispatch`
- Goi webhook deploy:
  - Backend (Render): secret `RENDER_DEPLOY_HOOK_URL`
  - Frontend (Vercel): secret `VERCEL_DEPLOY_HOOK_URL`

## 10) Checklist demo de nop bai
- [ ] Mo website frontend
- [ ] Them 1 user moi
- [ ] Sua thong tin user vua tao
- [ ] Xoa user
- [ ] Kiem tra du lieu thay doi tren table
- [ ] Kiem tra tab Actions tren GitHub xanh (CI pass)

