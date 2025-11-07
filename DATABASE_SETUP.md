# Database Setup Guide

## Prerequisites

- MySQL 8.0+ installed
- MySQL running on localhost:3306

## Step 1: Create Database

You have two options to create the database:

### Option A: Using MySQL Command Line

```bash
# Connect to MySQL (you'll be prompted for password)
mysql -u root -p

# Then run:
CREATE DATABASE IF NOT EXISTS sun_booking_tour
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

# Verify
SHOW DATABASES LIKE 'sun_booking_tour';

# Exit
exit;
```

### Option B: Using SQL Script

```bash
# Run the provided SQL script
mysql -u root -p < setup-database.sql
```

### Option C: Using sudo (Linux only)

```bash
# If MySQL is configured for socket authentication
sudo mysql -e "CREATE DATABASE IF NOT EXISTS sun_booking_tour CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

## Step 2: Configure Application

The application is pre-configured to connect to:

- **Host**: localhost:3306
- **Database**: sun_booking_tour
- **Username**: root
- **Password**: password (default - change in application.properties)

### Update Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sun_booking_tour?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**Security Note**: Never commit real passwords to Git! Use environment variables in production.

## Step 3: Verify Database Connection

```bash
# Test connection
mysql -u root -p sun_booking_tour -e "SELECT 'Connection Successful!' as status;"
```

## Step 4: Run Application to Test

```bash
# Build and run
mvn spring-boot:run

# Check logs for:
# "HikariPool-1 - Start completed"
# This confirms database connection is working
```

## Database Configuration Details

### Connection Pooling (HikariCP)

The application uses HikariCP (default in Spring Boot) with these settings:

```properties
# Maximum pool size
spring.datasource.hikari.maximum-pool-size=10

# Minimum idle connections
spring.datasource.hikari.minimum-idle=5

# Connection timeout (30 seconds)
spring.datasource.hikari.connection-timeout=30000

# Idle timeout (10 minutes)
spring.datasource.hikari.idle-timeout=600000

# Max lifetime (30 minutes)
spring.datasource.hikari.max-lifetime=1800000
```

### JPA Configuration

```properties
# Validate schema against entities
spring.jpa.hibernate.ddl-auto=validate

# Show SQL queries in logs (disable in production)
spring.jpa.show-sql=true

# Format SQL for readability
spring.jpa.properties.hibernate.format_sql=true

# Use MySQL 8 dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### Flyway Configuration

```properties
# Enable Flyway migrations
spring.flyway.enabled=true

# Create flyway_schema_history table if it doesn't exist
spring.flyway.baseline-on-migrate=true

# Migration scripts location
spring.flyway.locations=classpath:db/migration
```

## Flyway Migration Directory

Migration scripts are located in: `src/main/resources/db/migration/`

Naming convention: `V{version}__{description}.sql`

Example:

- `V1__create_users_table.sql`
- `V2__create_categories_table.sql`
- `V3__create_tours_table.sql`

Flyway will automatically run these migrations on application startup.

## Troubleshooting

### Issue: "Access denied for user 'root'@'localhost'"

**Solution**: Check your MySQL password in `application.properties`

```bash
# Reset MySQL root password (if needed)
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
exit;
```

### Issue: "Communications link failure"

**Solution**: Ensure MySQL is running

```bash
# Check MySQL status
sudo systemctl status mysql

# Start MySQL if stopped
sudo systemctl start mysql
```

### Issue: "Unknown database 'sun_booking_tour'"

**Solution**: Create the database using one of the methods in Step 1

### Issue: "Public Key Retrieval is not allowed"

**Solution**: Add to connection URL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sun_booking_tour?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### Issue: Flyway validation fails

**Solution**: Either:

1. Drop the database and recreate it
2. Run Flyway baseline:

```bash
mvn flyway:baseline
```

## Next Steps

After database setup is complete:

1. ✅ Database created
2. ✅ Application properties configured
3. ✅ Connection tested
4. ⏳ Run Task 1.3: Create Database Schema (Flyway Migrations)

## Production Setup (Optional)

For production, create a dedicated database user:

```sql
-- Create user
CREATE USER 'sunbooking'@'localhost' IDENTIFIED BY 'secure_password_here';

-- Grant privileges
GRANT ALL PRIVILEGES ON sun_booking_tour.* TO 'sunbooking'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

Then update `application-prod.properties`:

```properties
spring.datasource.username=sunbooking
spring.datasource.password=${DB_PASSWORD}
```

And set environment variable:

```bash
export DB_PASSWORD=secure_password_here
```

## Database Backup

```bash
# Backup database
mysqldump -u root -p sun_booking_tour > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore database
mysql -u root -p sun_booking_tour < backup_20251106_123456.sql
```

## Monitoring

### Check connection pool status

Enable Actuator and access:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/metrics/hikaricp.connections`

### Check Flyway migration history

```sql
USE sun_booking_tour;
SELECT * FROM flyway_schema_history;
```

---

**Last Updated**: November 6, 2025  
**Task**: 1.2 - Database Setup & Configuration
