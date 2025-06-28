# Apple Shop Backend

**Dự án tốt nghiệp** - Backend cho hệ thống bán hàng Apple trực tuyến

## 📖 Tổng quan

Apple Shop Backend là một API RESTful được xây dựng bằng Java và Spring Boot, phục vụ cho việc quản lý cửa hàng bán sản phẩm Apple trực tuyến. Đây là dự án giáo dục nhằm thực hành các kỹ thuật phát triển backend hiện đại.

### ✨ Tính năng chính

- 🔐 Xác thực và phân quyền người dùng
- 📦 Quản lý sản phẩm và danh mục
- 🛒 Hệ thống giỏ hàng và đặt hàng
- 💳 Xử lý thanh toán
- ⭐ Đánh giá và nhận xét sản phẩm
- 📝 Hệ thống blog
- 👤 Quản lý hồ sơ người dùng

## 🛠️ Công nghệ sử dụng

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **Java** | 24 | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.5.0 | Framework backend |
| **Spring Data JPA** | Latest | Thao tác cơ sở dữ liệu |
| **Spring Security** | Latest | Bảo mật và xác thực |
| **Microsoft SQL Server** | Azure | Cơ sở dữ liệu |
| **Redis** | Latest | Cache và session |
| **JWT** | 0.12.6 | Token authentication |
| **Maven** | Latest | Quản lý dependency |
| **Lombok** | Latest | Giảm boilerplate code |

## 📁 Cấu trúc dự án

```
apple-shop/
├── src/main/java/com/web/appleshop/
│   ├── AppleShopApplication.java     # Entry point
│   ├── config/                       # Cấu hình Spring
│   ├── controller/                   # REST Controllers
│   ├── dto/                          # Data Transfer Objects
│   │   ├── request/                  # Request DTOs
│   │   └── response/                 # Response DTOs
│   ├── entity/                       # JPA Entities (22 entities)
│   ├── enums/                        # Enums
│   ├── exception/                    # Custom Exceptions
│   ├── repository/                   # Spring Data Repositories
│   ├── service/                      # Business Logic
│   └── util/                         # Utility Classes
├── src/main/resources/
│   ├── application.yaml              # Cấu hình ứng dụng
│   └── db/migration/                 # Database migration scripts
└── src/test/                         # Unit tests
```

## 🗄️ Cơ sở dữ liệu

### Các thực thể chính

| Entity | Mô tả | Quan hệ chính |
|--------|-------|---------------|
| **User** | Người dùng hệ thống | → Role, Orders, Reviews |
| **Product** | Sản phẩm | → Category, Stock, Reviews |
| **Order** | Đơn hàng | → User, OrderDetails |
| **Category** | Danh mục sản phẩm | → Products |
| **Stock** | Tồn kho sản phẩm | → Product, Color |
| **CartItem** | Giỏ hàng | → Product, User |
| **Review** | Đánh giá sản phẩm | → Product, User |
| **Blog** | Bài viết blog | → User |

### Thiết lập cơ sở dữ liệu

Dự án sử dụng Azure SQL Database. Để kết nối, bạn cần:

1. Cung cấp IPv4 để có thể duyệt quyền truy cập vào database.
2. Cấu hình connection string trong `application.yaml`.
3. Cập nhật thông tin kết nối (file .env) qua environment variables

## ⚙️ Cài đặt và chạy dự án

### Yêu cầu hệ thống

- ☕ **Java JDK 24+**
- 📦 **Maven 3.6+**
- 🗄️ **SQL Server** (Azure SQL hoặc Local)
- 🔧 **Git**

### Hướng dẫn cài đặt

1. **Clone repository**
```bash
git clone https://github.com/hieudq05/apple-shop-be.git
cd apple-shop-be/apple-shop
```

2. **Cấu hình environment variables**

Thêm các biến môi trường trong file .env ở thư mục gốc:
```env
DB_URL={your_database_url}
DB_USERNAME={your_username}
DB_PASSWORD={your_password}
REDIS_HOST={your_redis_host}
REDIS_PORT={redis_port}
REDIS_PASSWORD={your_redis_password}
REDIS_DATABASE={your_redis_database}
...
```

3. **Cài đặt dependencies**
```bash
./mvnw clean install
```

4. **Chạy ứng dụng**
```bash
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy tại `http://localhost:8080`
Context path của ứng dụng `/api/v1/`

## 🔧 API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Đăng nhập
- `POST /api/v1/auth/register` - Đăng ký tài khoản

### Products
- `GET /api/v1/products/{categoryId}` - Lấy danh sách sản phẩm
- `GET /api/v1/products/{categoryId}/{id}` - Chi tiết sản phẩm
- `POST /api/v1/admin/products` - Tạo sản phẩm mới (Admin)
- `PUT /api/v1/admin/products/{categoryId}/{id}` - Cập nhật sản phẩm (Admin)
- `GET /api/v1/admin/products/{categoryId}` - Lấy danh sách sản phẩm (Admin)
- `GET /api/v1/admin/products/{categoryId}/{id}` - Chi tiết sản phẩm (Admin)

### Orders
- `GET /api/v1/orders/me` - Lấy danh sách lịch sử đơn hàng của người dùng đang đăng nhập
- `POST /api/v1/payments/vnpay/create-payment` - Tạo đơn hàng mới thanh toán bằng VNPAY
- `GET /api/v1/orders/{id}/cancel` - Huỷ một đơn hàng của người dùng đang đăng nhập bằng orderId

*Để xem chi tiết API documentation, chạy ứng dụng và truy cập Swagger UI (nếu được cấu hình)*

## 🧪 Testing
`Chưa triển khai`

### Chạy tests

```bash
# Chạy tất cả tests
./mvnw test

# Chạy tests với coverage report
./mvnw test jacoco:report

# Chạy specific test class
./mvnw test -Dtest=CreateProductRequestValidationTest
```

### Test Structure

- **Unit Tests**: Test các method riêng lẻ
- **Integration Tests**: Test tích hợp giữa các component
- **Validation Tests**: Test validation cho DTOs

## 📚 Tài liệu học tập

### Kiến thức áp dụng

- **Spring Boot**: REST API, Auto Configuration, Starter Dependencies
- **Spring Data JPA**: Entity Mapping, Repository Pattern, Query Methods
- **Spring Security**: Authentication, Authorization, JWT
- **Database Design**: Relationship Mapping, Migration với Flyway
- **Validation**: Bean Validation, Custom Validators
- **Testing**: JUnit, Mockito, Test Slices
- **Architecture**: Layered Architecture, DTO Pattern

### Patterns và Best Practices

- **Repository Pattern**: Tách biệt logic truy cập dữ liệu
- **DTO Pattern**: Transfer object cho API
- **Builder Pattern**: Tạo object phức tạp
- **Exception Handling**: Global exception handler
- **Validation**: Comprehensive input validation

## 🚀 Tính năng nâng cao

- **Redis Caching**: Cache dữ liệu otp được sinh khi tạo tài khoản
- **JWT Authentication**: Stateless authentication
- **File Upload**: Upload hình ảnh sản phẩm
- **Email Service**: Gửi email xác nhận
- **Pagination**: Phân trang cho danh sách lớn
- **Sorting & Filtering**: Sắp xếp và lọc dữ liệu

## 🤝 Đóng góp

Đây là dự án giáo dục, hoan nghênh mọi đóng góp để cải thiện:

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push branch (`git push origin feature/amazing-feature`)
5. Tạo Pull Request

### Guidelines

- Viết code clean và có comment
- Tuân thủ coding convention
- Thêm tests cho feature mới
- Cập nhật documentation

## 📧 Liên hệ

- **Repository**: [apple-shop-be](https://github.com/hieudq05/apple-shop-be)
- **Issues**: [GitHub Issues](https://github.com/hieudq05/apple-shop-be/issues)

---

<div align="center">

**🍎 Apple Shop Backend** - Dự án tốt nghiệp Spring Boot

Made with ❤️ for learning

</div>
