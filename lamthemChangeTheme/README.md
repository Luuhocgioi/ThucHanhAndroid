# 🔑 Login Example

Ứng dụng Android mô phỏng chức năng **đăng nhập cơ bản** với hai màn hình:  
1. Màn hình **Đăng nhập** (MainActivity)  
2. Màn hình **Chào mừng** (MainActivity2)  

---

## 🖼️ Giao diện minh họa

| Màn hình đăng nhập | Màn hình chào mừng |
|--------------------|--------------------|
| ![Login Screen](./Image/Login1.png) | ![Welcome Screen](./Image/Login2.png) |

---

## ⚙️ Chức năng chính
- Nhập **tên tài khoản** và **mật khẩu**.  
- Kiểm tra thông tin đăng nhập (có thể là dữ liệu cố định hoặc từ logic trong code).  
- Nếu đúng → chuyển sang màn hình chào mừng (`MainActivity2`).  
- Nếu sai → hiển thị thông báo lỗi (`sai tk hoac mat khau`).  

---

## 🧩 Mô tả giao diện

### **activity_main.xml (Màn hình đăng nhập)**
Tạo giao diện đăng nhập với bố cục `ConstraintLayout`, gồm:
- `TextView` tiêu đề **"Đăng nhập"**  
- `EditText` nhập **tên tài khoản** (`@id/edtUsername`)  
- `EditText` nhập **mật khẩu** (`@id/edtPassword`)  
- `Button` **Đăng nhập** (`@id/btnLogin`)  



### **activity_main2.xml (Màn hình chào mừng)**
Hiển thị dòng chữ: Chào mừng bạn đến với


## 🚀 Cách hoạt động
1. Người dùng nhập thông tin vào `EditText`.  
2. Nhấn **Đăng nhập**.  
3. Ứng dụng kiểm tra tài khoản (ví dụ: `username = "admin"`, `password = "123"`).  
4. Nếu hợp lệ → `Intent` mở `MainActivity2`.  
5. Nếu không → hiện `Toast` báo lỗi.
