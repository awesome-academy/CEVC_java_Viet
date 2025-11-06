# Sun Booking Tour

A comprehensive travel booking tours application built with Spring Boot, featuring both an admin management system and a user-facing REST API.

## Project Overview

This application consists of two main components:

1. **Admin Site**: Server-side rendered MVC application using Thymeleaf for managing the platform
2. **User API**: REST API providing data and functionality for client-side applications

## Technology Stack

### Backend

- **Java**: 11+
- **Framework**: Spring Boot 2.7.18
- **Core Dependencies**:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Thymeleaf
  - Flyway (Database Migrations)

### Database

- **MySQL**: 8.0+
- **Connection Pool**: HikariCP

### Security

- **Admin Site**: Session/Cookie-based authentication
- **User API**: JWT-based authentication
- **Password Hashing**: BCrypt (strength 10+)

### Build Tool

- **Maven**: 3.6+

## Project Structure

```
sun-booking-tour/
├── src/
│   ├── main/
│   │   ├── java/com/sunbooking/
│   │   │   ├── SunBookingTourApplication.java
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/
│   │   │   │   ├── admin/        # Admin MVC controllers
│   │   │   │   └── api/          # REST API controllers
│   │   │   ├── service/
│   │   │   │   ├── admin/        # Admin services
│   │   │   │   └── api/          # API services
│   │   │   ├── repository/       # Spring Data JPA repositories
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/
│   │   │   │   ├── request/      # API request DTOs
│   │   │   │   ├── response/     # API response DTOs
│   │   │   │   └── form/         # Admin form DTOs
│   │   │   ├── security/         # Security configurations
│   │   │   ├── exception/        # Custom exceptions
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migration/     # Flyway migration scripts
│   │       ├── static/           # Static resources (CSS, JS, images)
│   │       └── templates/        # Thymeleaf templates
│   └── test/                     # Test files
├── spec/                         # Project specifications
├── pom.xml
└── README.md
```

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK**: 11 or higher
- **Maven**: 3.6 or higher
- **MySQL**: 8.0 or higher
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (recommended)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd sun-booking-tour
```

## Database Setup

### Prerequisites

- MySQL 8.0+ installed and running
- MySQL running on `localhost:3306`

### Setup Steps

**Option 1: Automated Setup (Recommended)**

Run the provided setup script:

```bash
./setup-database.sh
```

**Option 2: Manual Setup**

1. Create the database:

```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE IF NOT EXISTS sun_booking_tour
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

# Verify
SHOW DATABASES LIKE 'sun_booking_tour';

# Exit
exit;
```

2. Update database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**⚠️ Important**: Never commit your real database password to Git!

For detailed database setup instructions, troubleshooting, and configuration options, see [DATABASE_SETUP.md](DATABASE_SETUP.md)

### 3. Configure Database Connection

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sun_booking_tour?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Accessing the Application

### Admin Site

- **URL**: `http://localhost:8080/admin/login`
- **Default Credentials**: (Will be configured after database migrations)

### User API

- **Base URL**: `http://localhost:8080/api`
- **Documentation**: (Swagger UI - to be configured)

## Development Workflow

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=YourTestClass

# Run with coverage
mvn clean test jacoco:report
```

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`

Migrations run automatically on application startup. To manually trigger:

```bash
mvn flyway:migrate
```

### Building for Production

```bash
# Create production JAR
mvn clean package -DskipTests

# Run production JAR
java -jar target/sun-booking-tour-1.0.0.jar
```

## Project Features

### Admin Site Features

1. Authentication & Authorization
2. Dashboard with Statistics
3. User Management
4. Admin Management
5. Category Management
6. Tour Management
7. Booking Management
8. Review Management

### User API Features

1. User Registration & Authentication
2. Profile Management
3. Tour Search & Booking
4. Reviews & Ratings
5. Comments & Likes
6. Booking History

## API Documentation

API documentation will be available at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (to be configured)
- **API Docs**: See `spec/` folder for detailed specifications

## Configuration

### Environment-Specific Configuration

The application supports multiple profiles:

- **Development**: `application-dev.properties`
- **Production**: `application-prod.properties`
- **Test**: `application-test.properties`

Run with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### JWT Configuration

Update JWT secret in `application.properties`:

```properties
jwt.secret=your-secret-key-minimum-256-bits
jwt.expiration=86400000  # 24 hours in milliseconds
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**

   - Verify MySQL is running
   - Check database credentials
   - Ensure database exists

2. **Port Already in Use**

   - Change server port in `application.properties`:
     ```properties
     server.port=8081
     ```

3. **Build Failures**
   - Clean Maven cache: `mvn clean`
   - Update dependencies: `mvn dependency:resolve`

## Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Ensure all tests pass
5. Submit a pull request

## Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc for public APIs
- Keep methods focused and concise
- Write unit tests for business logic

## License

This is an educational project for learning purposes.

## Support

For detailed specifications, see the `spec/` folder:

- `spec/requirements.md` - Project requirements
- `spec/technical-architecture.md` - Technical architecture
- `spec/data-models.md` - Database models
- `spec/IMPLEMENTATION_TASKS.md` - Implementation task list

## Version History

- **v1.0.0** (2025-11-06): Initial project setup
  - Spring Boot project structure
  - Maven configuration
  - Basic application configuration
  - Project documentation

---

**Created**: November 6, 2025  
**Last Updated**: November 6, 2025
