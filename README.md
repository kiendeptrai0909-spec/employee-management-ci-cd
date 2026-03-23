# Employee Management (Spring Boot + React + MySQL)

Mục tiêu môn học: xây `Website Quản lý Nhân Viên` với CRUD dữ liệu `users (id, name, email, phone)`.

## Repo
- Frontend: ReactJS
- Backend: Spring Boot (REST API)
- DB: MySQL
- CI/CD: GitHub Actions (sẽ cấu hình theo từng bước)

## Cách chạy Backend (local)
1. Tạo database MySQL tên `employee_management`
2. Cập nhật biến môi trường (nếu cần) hoặc dùng mặc định trong `backend/src/main/resources/application.properties`
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Chạy Spring Boot

Endpoint:
- `GET /api/users`

## Cách chạy Frontend (local)
1. Mở terminal trong thư mục `frontend`
2. Cài dependencies và chạy Vite
   - `npm install`
   - `npm run dev`

Nếu bạn muốn trỏ frontend tới backend đang deploy (Render), dùng file:
- `frontend/.env.example` (biến `VITE_API_BASE_URL`)

