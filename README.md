# Apple Shop Backend – Detailed Documentation
## 1. Overview
Apple Shop is a backend system for managing an online apple shop, built with Java 24 and Spring Boot 3.5.0. It provides RESTful APIs for product management, order processing, user authentication, and more.

## Table of Contents

- [Overview](#overview)
- [Technologies & Tools](#technologies--tools)
- [Project Structure](#project-structure)
- [Database](#database)
- [Configuration](#configuration)
- [Prerequisites](#prerequisites)
- [Build & Run](#build--run)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Contribution](#contribution)

---

## Overview

Apple Shop is a backend system for managing an online apple shop, built with **Java 24** and **Spring Boot 3.5.0**. It provides RESTful APIs for product management, order processing, user authentication, and more.

### ✨ Key Features
- Product catalog management
- User authentication and authorization
- Order processing and tracking
- Payment integration support
- Blog management system
- Product customization (colors, features)
- Review and rating system

---

## Technologies & Tools

| Technology | Version | Purpose |
|----------|---------|---------|
| **Java** | 24 | Core programming language |
| **Spring Boot** | 3.5.0 | Application framework |
| **Spring Data JPA** | Latest | Object-Relational Mapping |
| **Spring Security** | Latest | Authentication & Authorization |
| **Maven** | Latest | Build & dependency management |
| **Microsoft SQL Server** | Azure | Database management |
| **Lombok** | Latest | Boilerplate code reduction |
| **Flyway** | Latest | Database migration |
| **JUnit** | Latest | Unit testing framework |

---

## Project Structure

```
apple-shop/
│
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/web/appleshop/
│   │   │   ├── 🚀 AppleShopApplication.java      # Main application entry point
│   │   │   ├── 📂 config/                        # Configuration classes
│   │   │   ├── 📂 controller/                    # REST API controllers
│   │   │   ├── 📂 dto/                           # Data Transfer Objects
│   │   │   ├── 📂 entity/                        # JPA Entities (22 entities)
│   │   │   ├── 📂 enums/                         # Enum types
│   │   │   ├── 📂 exception/                     # Custom exceptions
│   │   │   ├── 📂 repository/                    # Spring Data JPA repositories
│   │   │   ├── 📂 service/                       # Business logic services
│   │   │   └── 📂 util/                          # Utility classes
│   │   └── 📂 resources/
│   │       ├── ⚙️ application.yaml               # Main configuration file
│   │       ├── 📂 db/migration/                  # Flyway migration scripts
│   │       ├── 📂 static/                        # Static web resources
│   │       └── 📂 templates/                     # Template files
│   └── 📂 test/
│       └── 📂 java/com/web/appleshop/
│           └── 🧪 AppleShopApplicationTests.java # Main test class
├── 📄 pom.xml                                    # Maven build configuration
├── 📄 README.md                                  # Project documentation
└── 📄 .gitignore                                 # Git ignore rules
```

---

## Database

### 📊 Database Information
- **Type**: Microsoft SQL Server (Azure SQL Database)
- **Connection**: Configured in `application.yaml`
- **Migration**: Managed by Flyway scripts in `db/migration/`

### 🔧 Database Setup
To access the database, you'll need to configure your IPv4 address:
- **Start IPv4 address**: `1.2.3.4`
- **End IPv4 address**: `1.2.3.4`

### 📋 Database Entities

| Entity | Description | Key Relationships |
|--------|-------------|-------------------|
| **User** | User accounts and profiles | → Role, ShippingInfo, Orders |
| **Product** | Product catalog items | → Category, Stock, Reviews |
| **Order** | Customer orders | → User, OrderDetails, OrderStatus |
| **Category** | Product categories | → Products, Promotions |
| **Blog** | Blog posts and articles | → User (author) |
| **CartItem** | Shopping cart items | → Product, Stock, User |
| **Color** | Product color options | → Stock |
| **Feature** | Product features | → InstanceProperty |
| **OrderDetail** | Order line items | → Order, Stock |
| **PaymentType** | Payment methods | → Orders |
| **ProductPhoto** | Product images | → Stock |
| **Promotion** | Promotional campaigns | → Categories, PromotionType |
| **Review** | Product reviews | → Product, User |
| **Role** | User roles and permissions | → Users |
| **SavedProduct** | User's saved products | → User, Product |
| **ShippingInfo** | Shipping addresses | → User |
| **Stock** | Product inventory | → Product, Color |
| **UserActivityLog** | User activity tracking | → User |

---

## Configuration

The **`application.yaml`** file contains all main application settings:

```yaml
spring:
  application:
    name: apple-shop
  datasource:
    url: jdbc:sqlserver://[server]:1433;database=[database_name]
    username: [username]
    password: [password]
    driverClassName: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: validate
```

### 🔧 Configuration Options
- **Database Connection**: SQL Server connection parameters
- **JPA Settings**: Hibernate DDL mode set to `validate`
- **Security**: Spring Security configuration (to be implemented)
- **Logging**: Application logging levels

---

## Prerequisites

Before running the application, ensure you have the following installed:

| Requirement | Version | Download Link |
|-------------|---------|---------------|
| ☕ **Java JDK** | 24+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| 📦 **Maven** | 3.6+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| 🗄️ **SQL Server** | 2019+ | [Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) |
| 🔧 **Git** | Latest | [Git SCM](https://git-scm.com/downloads) |

### 🌐 Network Requirements
- Internet connection for Maven dependencies
- Access to Azure SQL Database (if using cloud database)
- Firewall configuration for SQL Server connection

---

## Build & Run

### 1. Clone the Repository
```bash
git clone https://github.com/hieudq05/apple-shop-be.git
cd apple-shop-be/apple-shop
```

### 2. Configure the Database
Edit `src/main/resources/application.yaml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://your-server:1433;database=your-database
    username: your-username
    password: your-password
```

### 3. Build and Run
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

### 4. Verify Installation
The application will start on **[localhost:8080](http://localhost:8080)** by default.

---

## API Endpoints

> **Note**: API endpoints are currently in development. The following endpoints are planned:

### Authentication
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/login` | User login | 🚧 Planned |
| `POST` | `/api/auth/register` | User registration | 🚧 Planned |
| `POST` | `/api/auth/logout` | User logout | 🚧 Planned |

### User Management
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/users` | Get all users | 🚧 Planned |
| `GET` | `/api/users/{id}` | Get user by ID | 🚧 Planned |
| `PUT` | `/api/users/{id}` | Update user | 🚧 Planned |

### Product Management
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/products` | Get all products | 🚧 Planned |
| `GET` | `/api/products/{id}` | Get product by ID | 🚧 Planned |
| `POST` | `/api/products` | Create new product | 🚧 Planned |
| `PUT` | `/api/products/{id}` | Update product | 🚧 Planned |

### Order Management
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/orders` | Get all orders | 🚧 Planned |
| `POST` | `/api/orders` | Create new order | 🚧 Planned |
| `GET` | `/api/orders/{id}` | Get order by ID | 🚧 Planned |

---

## Testing

### 🔬 Test Structure
- Unit and integration tests are located in `src/test/java/com/web/appleshop/`
- Main test class: `AppleShopApplicationTests.java`

### 🏃‍♂️ Running Tests
```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=AppleShopApplicationTests
```

### 📊 Test Coverage


---

## Contribution

We welcome contributions to the Apple Shop Backend project! Here's how you can help:

### 🔄 Development Workflow
1. **Fork** the repository
2. **Create** a new branch (`git checkout -b feature/your-feature`)
3. **Make** your changes with proper tests
4. **Commit** your changes (`git commit -m 'Add some feature'`)
5. **Push** to the branch (`git push origin feature/your-feature`)
6. **Open** a Pull Request

### 📝 Contribution Guidelines
- Follow Java coding standards and Spring Boot best practices
- Write comprehensive tests for new features
- Update documentation for any API changes
- Ensure all tests pass before submitting PR

### 🐛 Bug Reports
- Use GitHub Issues to report bugs
- Include detailed steps to reproduce
- Provide system information and error logs

### 💡 Feature Requests
- Open an issue with the `enhancement` label
- Describe the feature and its benefits
- Discuss implementation approach

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact & Support

- **Project Repository**: [GitHub](https://github.com/hieudq05/apple-shop-be)
- **Issues**: [GitHub Issues](https://github.com/hieudq05/apple-shop-be/issues)
- **Documentation**: This README and inline code comments

---

<div align="center">

**🍎 Apple Shop Backend** - Built with ❤️ using Spring Boot

[![Java](https://img.shields.io/badge/Java-24-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-Azure-blue?style=for-the-badge&logo=microsoft-sql-server)](https://azure.microsoft.com/en-us/services/sql-database/)

</div>




