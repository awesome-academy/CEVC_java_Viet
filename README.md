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

### 5. Database Schema

The application uses Flyway for database migrations. On first startup, 9 migrations will be automatically executed:

1. **V1**: Create users table (with soft-delete)
2. **V2**: Create categories table (TOUR, NEWS, FOOD, PLACE)
3. **V3**: Create tours table (with full-text search)
4. **V4**: Create bookings table (with payment tracking)
5. **V5**: Create reviews table
6. **V6**: Create comments table (with nested replies)
7. **V7**: Create likes table
8. **V8**: Create additional performance indexes
9. **V9**: Seed initial data (admin account + 19 categories)

**Initial Admin Account**:

- Email: `admin@sunbooking.com`
- Password: `Admin@123`

⚠️ **Change this password immediately after first login!**

### 6. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

**On first run**:

- Flyway will create all database tables
- Initial admin account and categories will be seeded
- Check logs for migration status

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

Migrations run automatically on application startup. Schema version can be checked:

```bash
# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Run migrations manually (if needed)
mvn flyway:migrate
```

**Migration Files**:

- V1 to V7: Table creation scripts
- V8: Additional indexes for performance
- V9: Initial seed data (admin + categories)

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

### Environment Profiles

The application supports three environment profiles:

- **dev** (Development - default)
- **prod** (Production)
- **test** (Testing)

### Setting Up Environment Variables

1. **Copy the example environment file**:

```bash
cp .env.example .env
```

2. **Edit `.env` with your configuration**:

```bash
# Example .env file
SPRING_PROFILE=dev
DB_URL=jdbc:mysql://localhost:3306/sun_booking_tour?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=your-secure-jwt-secret-key-here
```

3. **Run the application**:

The `.env` file is automatically loaded by the `spring-dotenv` library when the application starts.

```bash
mvn spring-boot:run
```

### Running with Different Profiles

**Development Profile (Default)**:

```bash
# Using Maven
mvn spring-boot:run

# Or specify explicitly
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Using JAR
java -jar target/sun-booking-tour-1.0.0.jar --spring.profiles.active=dev
```

**Production Profile**:

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Using JAR (recommended for production)
java -jar target/sun-booking-tour-1.0.0.jar --spring.profiles.active=prod
```

**Test Profile**:

```bash
# Tests automatically use test profile
mvn test
```

### Profile-Specific Configurations

#### Development Profile (`application-dev.properties`)

- **Database**: MySQL with verbose SQL logging
- **Logging**: DEBUG level for application code
- **Thymeleaf**: Cache disabled for hot reload
- **Security**: Less strict (for easier development)
- **Error Details**: Full stack traces visible
- **CORS**: Permissive (allows localhost origins)

#### Production Profile (`application-prod.properties`)

- **Database**: MySQL with optimized connection pool
- **Logging**: INFO/WARN level only, errors logged to separate file
- **Thymeleaf**: Cache enabled for performance
- **Security**: Strict settings, all secrets from environment variables
- **Error Details**: Minimal (security)
- **CORS**: Restricted to configured origins
- **Session**: Secure cookies, HTTPS-only

#### Test Profile (`application-test.properties`)

- **Database**: H2 in-memory database
- **Logging**: Minimal (WARN level)
- **Flyway**: Disabled (uses JPA schema generation)

### Environment Variables Reference

| Variable               | Description                    | Default                 | Required   |
| ---------------------- | ------------------------------ | ----------------------- | ---------- |
| `SPRING_PROFILE`       | Active Spring profile          | `dev`                   | No         |
| `SERVER_PORT`          | Application port               | `8080`                  | No         |
| `DB_URL`               | Database connection URL        | localhost MySQL         | Yes (prod) |
| `DB_USERNAME`          | Database username              | `root`                  | Yes (prod) |
| `DB_PASSWORD`          | Database password              | `root`                  | Yes (prod) |
| `DB_POOL_SIZE`         | Max connection pool size       | `10` (dev), `20` (prod) | No         |
| `JWT_SECRET`           | JWT signing secret (256+ bits) | Default dev key         | Yes (prod) |
| `JWT_EXPIRATION`       | Token expiration (ms)          | `86400000` (24h)        | No         |
| `ADMIN_USERNAME`       | Initial admin username         | `admin`                 | Yes (prod) |
| `ADMIN_PASSWORD`       | Initial admin password         | `admin123`              | Yes (prod) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins           | localhost               | Yes (prod) |
| `LOG_FILE_PATH`        | Log file location              | `logs/`                 | No         |

### Logging Configuration

Logs are configured per environment in `logback-spring.xml`:

**Development**:

- Console: Colorized, detailed output
- Files:
  - `logs/sun-booking-tour-dev.log` (all logs)
  - `logs/sun-booking-tour-dev-error.log` (errors only)
- Rotation: 10MB per file, 7 days retention, 100MB total

**Production**:

- Console: Standard format
- Files:
  - `/var/log/sun-booking-tour/application.log` (all logs)
  - `/var/log/sun-booking-tour/application-error.log` (errors only)
- Rotation: 50MB per file, 30 days retention, 1GB total

**Viewing Logs**:

```bash
# Tail development logs
tail -f logs/sun-booking-tour-dev.log

# Tail error logs only
tail -f logs/sun-booking-tour-dev-error.log

# Production logs
tail -f /var/log/sun-booking-tour/application.log
```

### Security Best Practices

1. **Never commit sensitive data**:

   - The `.env` file is in `.gitignore`
   - Use environment variables for all secrets
   - Use different secrets for each environment

2. **Generate secure JWT secret**:

```bash
# Generate a secure random key
openssl rand -base64 64
```

3. **Production checklist**:
   - [ ] Change all default passwords
   - [ ] Use strong JWT secret (256+ bits)
   - [ ] Configure HTTPS/SSL
   - [ ] Set secure database credentials
   - [ ] Restrict CORS to specific domains
   - [ ] Enable production logging
   - [ ] Configure firewall rules

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

## Error Handling & Logging

This project includes a comprehensive error handling and logging infrastructure:

### Exception Handling

- **Custom Exceptions**: ResourceNotFoundException, ValidationException, UnauthorizedException, BusinessLogicException, DuplicateResourceException
- **Global Exception Handlers**: Separate handlers for API (REST) and Admin (MVC) controllers
- **Standard Error Responses**: Consistent JSON error format for API endpoints
- **Thymeleaf Error Pages**: User-friendly error views for admin pages

### Logging Features

- **Request Correlation**: Unique request ID for tracking requests across logs
- **Performance Monitoring**: Automatic slow request detection (>1s)
- **Structured Logging**: Consistent format with timestamps, thread, request ID, level, and logger
- **Profile-Aware Configuration**: Different log levels for dev/prod/test environments
- **Log Rotation**: Automatic file rotation with configurable retention policies

For detailed documentation, see `ERROR_HANDLING_LOGGING.md`.

## Support

For detailed specifications, see the `spec/` folder:

- `spec/requirements.md` - Project requirements
- `spec/technical-architecture.md` - Technical architecture
- `spec/data-models.md` - Database models
- `spec/database-diagram.md` - Entity Relationship Diagram
- `spec/IMPLEMENTATION_TASKS.md` - Implementation task list
- `DATABASE_SETUP.md` - Database setup and troubleshooting guide
- `ERROR_HANDLING_LOGGING.md` - Error handling and logging guide

## Version History

- **v1.0.0** (2025-11-10): Initial project setup with complete infrastructure
  - Spring Boot project structure with all dependencies
  - Maven configuration with spring-dotenv support
  - MySQL database setup with Flyway migrations
  - Complete database schema: 7 tables (users, categories, tours, bookings, reviews, comments, likes)
  - Environment-based configuration (dev/prod/test profiles)
  - Comprehensive error handling infrastructure with custom exceptions
  - Global exception handlers for API and MVC controllers
  - Request/response logging with correlation IDs
  - Profile-aware logging configuration with rotation policies
  - Security setup with externalized secrets
  - Initial seed data: 1 admin account + 19 categories
  - Complete project documentation

---

**Created**: November 6, 2025  
**Last Updated**: November 10, 2025
