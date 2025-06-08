# Apple Shop Backend – Detailed Documentation
## 1. Overview
Apple Shop is a backend system for managing an online apple shop, built with Java 24 and Spring Boot 3.5.0. It provides RESTful APIs for product management, order processing, user authentication, and more.

***

## 2. Technologies & Tools
1. Java 24 
2. Spring Boot 3.5.0 
3. Spring Data JPA (ORM)
4. Spring Security (authentication/authorization)
5. Maven (build & dependency management)
6. Microsoft SQL Server (database, via JDBC)
7. Lombok (boilerplate code reduction)
8. Flyway (database migration, via db/migration scripts)
9. JUnit (testing)

***

## 3. Project Structure
```
apple-shop/
│
├── src/
│   ├── main/
│   │   ├── java/com/web/appleshop/
│   │   │   ├── AppleShopApplication.java
│   │   │   ├── config/         # Configuration classes
│   │   │   ├── controller/     # REST API controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA Entities (Product, Order, User, etc.)
│   │   │   ├── enums/          # Enum types
│   │   │   ├── exception/      # Custom exceptions
│   │   │   ├── repository/     # Spring Data JPA repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── util/           # Utility classes
│   │   └── resources/
│   │       ├── application.yaml    # Main configuration file
│   │       ├── db/migration/       # Flyway migration scripts (e.g., update-schema.sql)
│   │       ├── static/             # Static resources (if any)
│   │       └── templates/          # Templates (if any)
│   └── test/
│       └── java/com/web/appleshop/
│           └── AppleShopApplicationTests.java
├── pom.xml         # Maven build file
└── README.md
```

***

## 4. Database
* **Type**: Microsoft SQL Server (Azure SQL Database)
* **Connection**: Configured in application.yaml
* **Setup**: To have access to the database, you'll need to provide your own ipV4 address of your computer. The format is:
  * Start ipV4 address: `1.2.3.4`
  * End ipV4 address: `1.2.3.4`
* **Entities**: Includes Blog, CartItem, Category, Color, Feature, InstanceProperty, Order, OrderDetail, OrderStatus, PaymentType, Product, ProductPhoto, Promotion, PromotionType, Review, Role, SavedProduct, ShippingInfo, Stock, User, UserActivityLog, etc.

***

## 5. Configuration
* **application.yaml** contains all main settings:
  * Database URL, username, password, and driver
  * JPA settings (e.g., ddl-auto: validate for schema validation)

***    

## 6. Build & Run
#### 1. Clone the repository:
``` shell
git clone https://github.com/hieudq05/apple-shop-be.git
cd apple-shop-be/apple-shop
```

#### 2. Configure the database:
Edit `src/main/resources/application.yaml` with your database credentials if needed.
#### 3. Build and run:
``` shell
./mvnw spring-boot:run
```
The app runs at [localhost:8080](http://localhost:8080) by default.

***

## 7. Testing
* Unit and integration tests are located in src/test/java/com/web/appleshop/
* Run tests with:
``` shell
./mvnw test
```

***

## 8. Contribution
1. Fork the repository 
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit and push your changes
4. Open a pull request


