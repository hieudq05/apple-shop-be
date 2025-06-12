# Backend Apple Shop – Tài liệu chi tiết

## 1\. Tổng quan

Apple Shop là một hệ thống backend để quản lý một cửa hàng Apple trực tuyến, được xây dựng bằng **Java 24** và **Spring Boot 3.5.0**. Nó cung cấp các API RESTful để quản lý sản phẩm, xử lý đơn hàng, xác thực người dùng, và nhiều hơn nữa.

## Mục lục

  - [Tổng quan](https://www.google.com/search?q=%23t%E1%BB%95ng-quan)
  - [Công nghệ & Công cụ](https://www.google.com/search?q=%23c%C3%B4ng-ngh%E1%BB%87--c%C3%B4ng-c%E1%BB%A5)
  - [Cấu trúc dự án](https://www.google.com/search?q=%23c%E1%BA%A5u-tr%C3%BAc-d%E1%BB%B1-%C3%A1n)
  - [Cơ sở dữ liệu](https://www.google.com/search?q=%23c%C6%A1-s%E1%BB%9F-d%E1%BB%AF-li%E1%BB%87u)
  - [Cấu hình](https://www.google.com/search?q=%23c%E1%BA%A5u-h%C3%ACnh)
  - [Điều kiện tiên quyết](https://www.google.com/search?q=%23%C4%91i%E1%BB%81u-ki%E1%BB%87n-ti%C3%AAn-quy%E1%BA%BFt)
  - [Xây dựng & Chạy](https://www.google.com/search?q=%23x%C3%A2y-d%E1%BB%B1ng--ch%E1%BA%A1y)
  - [Điểm cuối API](https://www.google.com/search?q=%23%C4%91i%E1%BB%83m-cu%E1%BB%91i-api)
  - [Kiểm thử](https://www.google.com/search?q=%23ki%E1%BB%83m-th%E1%BB%AD)
  - [Đóng góp](https://www.google.com/search?q=%23%C4%91%C3%B3ng-g%C3%B3p)

-----

## Tổng quan

Apple Shop là một hệ thống backend để quản lý một cửa hàng Apple trực tuyến, được xây dựng bằng **Java 24** và **Spring Boot 3.5.0**. Nó cung cấp các API RESTful để quản lý sản phẩm, xử lý đơn hàng, xác thực người dùng, và nhiều hơn nữa.

### ✨ Tính năng chính

  - Quản lý danh mục sản phẩm
  - Xác thực và phân quyền người dùng
  - Xử lý và theo dõi đơn hàng
  - Hỗ trợ tích hợp thanh toán
  - Hệ thống quản lý blog
  - Tùy chỉnh sản phẩm (màu sắc, tính năng)
  - Hệ thống đánh giá và xếp hạng

-----

## Công nghệ & Công cụ

| Technology | Version | Purpose |
|----------|---------|---------|
| **Java** | 24 | Ngôn ngữ lập trình cốt lõi |
| **Spring Boot** | 3.5.0 | Framework ứng dụng |
| **Spring Data JPA** | Latest | Ánh xạ đối tượng-quan hệ |
| **Spring Security** | Latest | Xác thực & Phân quyền |
| **Maven** | Latest | Quản lý bản dựng & phụ thuộc |
| **Microsoft SQL Server** | Azure | Quản lý cơ sở dữ liệu |
| **Lombok** | Latest | Giảm mã boilerplate |
| **Flyway** | Latest | Di chuyển cơ sở dữ liệu |
| **JUnit** | Latest | Framework kiểm thử đơn vị |

-----

## Cấu trúc dự án

```
apple-shop/
│
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/web/appleshop/
│   │   │   ├── 🚀 AppleShopApplication.java      # Điểm vào chính của ứng dụng
│   │   │   ├── 📂 config/                        # Các lớp cấu hình
│   │   │   ├── 📂 controller/                    # Các bộ điều khiển REST API
│   │   │   ├── 📂 dto/                           # Các đối tượng truyền dữ liệu
│   │   │   ├── 📂 entity/                        # Các thực thể JPA (22 thực thể)
│   │   │   ├── 📂 enums/                         # Các kiểu Enum
│   │   │   ├── 📂 exception/                     # Các ngoại lệ tùy chỉnh
│   │   │   ├── 📂 repository/                    # Các kho lưu trữ Spring Data JPA
│   │   │   ├── 📂 service/                       # Các dịch vụ logic nghiệp vụ
│   │   │   └── 📂 util/                          # Các lớp tiện ích
│   │   └── 📂 resources/
│   │       ├── ⚙️ application.yaml               # Tệp cấu hình chính
│   │       ├── 📂 db/migration/                  # Các script di chuyển Flyway
│   │       ├── 📂 static/                        # Các tài nguyên web tĩnh
│   │       └── 📂 templates/                     # Các tệp template
│   └── 📂 test/
│       └── 📂 java/com/web/appleshop/
│           └── 🧪 AppleShopApplicationTests.java # Lớp kiểm thử chính
├── 📄 pom.xml                                    # Cấu hình bản dựng Maven
├── 📄 README.md                                  # Tài liệu dự án
└── 📄 .gitignore                                 # Các quy tắc bỏ qua Git
```

-----

## Cơ sở dữ liệu

### 📊 Thông tin cơ sở dữ liệu

  - **Type**: Microsoft SQL Server (Azure SQL Database)
  - **Connection**: Được cấu hình trong `application.yaml`
  - **Migration**: Được quản lý bởi các script Flyway trong `db/migration/`

### 🔧 Thiết lập cơ sở dữ liệu

Để truy cập cơ sở dữ liệu, bạn cần cung cấp địa chỉ IPv4 của mình:

  - **Start IPv4 address**: `1.2.3.4`
  - **End IPv4 address**: `1.2.3.4`

### 📋 Các thực thể cơ sở dữ liệu

| Entity | Description | Key Relationships |
|--------|-------------|-------------------|
| **User** | Tài khoản và hồ sơ người dùng | → Role, ShippingInfo, Orders |
| **Product** | Các mục trong danh mục sản phẩm | → Category, Stock, Reviews |
| **Order** | Đơn hàng của khách hàng | → User, OrderDetails, OrderStatus |
| **Category** | Danh mục sản phẩm | → Products, Promotions |
| **Blog** | Bài đăng và bài viết blog | → User (tác giả) |
| **CartItem** | Các mặt hàng trong giỏ hàng | → Product, Stock, User |
| **Color** | Các tùy chọn màu sắc sản phẩm | → Stock |
| **Feature** | Các tính năng sản phẩm | → InstanceProperty |
| **OrderDetail** | Các mục chi tiết đơn hàng | → Order, Stock |
| **PaymentType** | Các phương thức thanh toán | → Orders |
| **ProductPhoto** | Hình ảnh sản phẩm | → Stock |
| **Promotion** | Các chiến dịch khuyến mãi | → Categories, PromotionType |
| **Refresh Token** | Các token làm mới | → User |
| **Review** | Đánh giá sản phẩm | → Product, User |
| **Role** | Vai trò và quyền của người dùng | → Users |
| **SavedProduct** | Các sản phẩm đã lưu của người dùng | → User, Product |
| **ShippingInfo** | Địa chỉ giao hàng | → User |
| **Stock** | Tồn kho sản phẩm | → Product, Color |
| **UserActivityLog** | Theo dõi hoạt động người dùng | → User |

-----

## Cấu hình

Tệp **`application.yaml`** chứa tất cả các cài đặt ứng dụng chính:

```yaml
spring:
  application:
    name: apple-shop
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driverClassName: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: validate
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
      database: ${REDIS_DATABASE}
      timeout: 6000
...
```

### 🔧 Các tùy chọn cấu hình

  - **Database Connection**: Các tham số kết nối SQL Server
  - **JPA Settings**: Chế độ Hibernate DDL được đặt thành `validate`
  - **Security**: Cấu hình Spring Security (sẽ được triển khai)
  - **Logging**: Mức độ ghi log ứng dụng

-----

## Điều kiện tiên quyết

Trước khi chạy ứng dụng, đảm bảo bạn đã cài đặt những thứ sau:

| Requirement | Version | Download Link |
|-------------|---------|---------------|
| ☕ **Java JDK** | 24+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| 📦 **Maven** | 3.6+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| 🗄️ **SQL Server** | 2019+ | [Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) |
| 🔧 **Git** | Latest | [Git SCM](https://git-scm.com/downloads) |

### 🌐 Yêu cầu mạng

  - Kết nối Internet cho các dependency của Maven
  - Truy cập vào Azure SQL Database (nếu sử dụng cơ sở dữ liệu đám mây)
  - Cấu hình tường lửa cho kết nối SQL Server

-----

## Xây dựng & Chạy

### 1\. Clone Repository

```bash
git clone https://github.com/hieudq05/apple-shop-be.git
cd apple-shop-be/apple-shop
```

### 2\. Cấu hình cơ sở dữ liệu

Chỉnh sửa `src/main/resources/application.yaml` với thông tin đăng nhập cơ sở dữ liệu của bạn:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://dqhieuse.database.windows.net:1433;database=apple_dev_shop_v1;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
    username: [secret]
    password: [secret]
```

### 3\. Xây dựng và chạy

```bash
# Sử dụng Maven wrapper (khuyên dùng)
./mvnw spring-boot:run

# Hoặc sử dụng Maven đã cài đặt
mvn spring-boot:run
```

### 4\. Xác minh cài đặt

Ứng dụng sẽ khởi chạy trên **[localhost:8080](https://www.google.com/search?q=http://localhost:8080)** theo mặc định.

-----

## Điểm cuối API

> **Lưu ý**: Các điểm cuối API hiện đang được phát triển. Các điểm cuối sau đây đã được lên kế hoạch:

### Xác thực

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/api/v1/auth/login` | Đăng nhập người dùng | Complete |
| `POST` | `/api/v1/auth/register` | Đăng ký người dùng | Complete |
| `POST` | `/api/v1/auth/logout` | Đăng xuất người dùng | 🚧 Planned |

### Quản lý người dùng

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/users` | Lấy tất cả người dùng | 🚧 Planned |
| `GET` | `/api/v1/users/{id}` | Lấy người dùng theo ID | 🚧 Planned |
| `PUT` | `/api/v1/users/{id}` | Cập nhật người dùng | 🚧 Planned |

### Quản lý sản phẩm

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/products` | Lấy tất cả sản phẩm | 🚧 Planned |
| `GET` | `/api/v1/products/{id}` | Lấy sản phẩm theo ID | 🚧 Planned |
| `POST` | `/api/v1/products` | Tạo sản phẩm mới | 🚧 Planned |
| `PUT` | `/api/v1/products/{id}` | Cập nhật sản phẩm | 🚧 Planned |

### Quản lý đơn hàng

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/orders` | Lấy tất cả đơn hàng | 🚧 Planned |
| `POST` | `/api/v1/orders` | Tạo đơn hàng mới | 🚧 Planned |
| `GET` | `/api/v1/orders/{id}` | Lấy đơn hàng theo ID | 🚧 Planned |

-----

## Kiểm thử

### 🔬 Cấu trúc kiểm thử

  - Unit và integration tests được đặt tại `src/test/java/com/web/appleshop/`
  - Lớp kiểm thử chính: `AppleShopApplicationTests.java`

### 🏃‍♂️ Chạy kiểm thử

```bash
# Chạy tất cả các bài kiểm thử
./mvnw test

# Chạy kiểm thử với độ bao phủ
./mvnw test jacoco:report

# Chạy một lớp kiểm thử cụ thể
./mvnw test -Dtest=AppleShopApplicationTests
```

### 📊 Độ bao phủ kiểm thử

-----

## Đóng góp

Chúng tôi hoan nghênh những đóng góp cho dự án Apple Shop Backend\! Dưới đây là cách bạn có thể giúp đỡ:

### 🔄 Quy trình phát triển

1.  **Fork** repository
2.  **Tạo** một nhánh mới (`git checkout -b feature/your-feature`)
3.  **Thực hiện** các thay đổi của bạn với các bài kiểm thử thích hợp
4.  **Commit** các thay đổi của bạn (`git commit -m 'Add some feature'`)
5.  **Push** lên nhánh (`git push origin feature/your-feature`)
6.  **Mở** một Pull Request

### 📝 Hướng dẫn đóng góp

  - Tuân thủ các tiêu chuẩn mã hóa Java và các phương pháp hay nhất của Spring Boot
  - Viết các bài kiểm thử toàn diện cho các tính năng mới
  - Cập nhật tài liệu cho bất kỳ thay đổi API nào
  - Đảm bảo tất cả các bài kiểm thử đều vượt qua trước khi gửi PR

### 🐛 Báo cáo lỗi

  - Sử dụng GitHub Issues để báo cáo lỗi
  - Bao gồm các bước chi tiết để tái tạo
  - Cung cấp thông tin hệ thống và nhật ký lỗi

### 💡 Yêu cầu tính năng

  - Mở một issue với nhãn `enhancement`
  - Mô tả tính năng và lợi ích của nó
  - Thảo luận về phương pháp triển khai

-----

## 📄 Giấy phép

Dự án này được cấp phép theo Giấy phép MIT - xem tệp [LICENSE](https://www.google.com/search?q=LICENSE) để biết chi tiết.

-----

## 📞 Liên hệ & Hỗ trợ

  - **Kho lưu trữ dự án**: [GitHub](https://github.com/hieudq05/apple-shop-be)
  - **Vấn đề**: [GitHub Issues](https://github.com/hieudq05/apple-shop-be/issues)
  - **Tài liệu**: README này và các chú thích mã nội tuyến

---

<div align="center">

**🍎 Apple Shop Backend** - Built with ❤️ using Spring Boot

[![Java](https://img.shields.io/badge/Java-24-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-Azure-blue?style=for-the-badge&logo=microsoft-sql-server)](https://azure.microsoft.com/en-us/services/sql-database/)

</div>




