#!/bin/bash

# Sun Booking Tour - Database Setup Script
# This script helps set up the MySQL database for the application

echo "=========================================="
echo "Sun Booking Tour - Database Setup"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if MySQL is installed
echo "Checking MySQL installation..."
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}✗ MySQL is not installed${NC}"
    echo "Please install MySQL 8.0+:"
    echo "  sudo apt update"
    echo "  sudo apt install mysql-server"
    exit 1
fi

echo -e "${GREEN}✓ MySQL is installed${NC}"
MYSQL_VERSION=$(mysql --version)
echo "  Version: $MYSQL_VERSION"
echo ""

# Check if MySQL is running
echo "Checking MySQL status..."
if sudo systemctl is-active --quiet mysql; then
    echo -e "${GREEN}✓ MySQL is running${NC}"
else
    echo -e "${YELLOW}⚠ MySQL is not running${NC}"
    echo "Starting MySQL..."
    sudo systemctl start mysql
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ MySQL started successfully${NC}"
    else
        echo -e "${RED}✗ Failed to start MySQL${NC}"
        exit 1
    fi
fi
echo ""

# Create database
echo "Creating database 'sun_booking_tour'..."
echo ""
echo "Please enter your MySQL root password when prompted:"
echo ""

# Try to create database
sudo mysql -e "CREATE DATABASE IF NOT EXISTS sun_booking_tour CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Database 'sun_booking_tour' created successfully${NC}"
    echo ""
    
    # Verify database
    echo "Verifying database..."
    DB_CHECK=$(sudo mysql -e "SHOW DATABASES LIKE 'sun_booking_tour';" 2>/dev/null | grep sun_booking_tour)
    if [ -n "$DB_CHECK" ]; then
        echo -e "${GREEN}✓ Database verified${NC}"
        
        # Show database info
        echo ""
        echo "Database information:"
        sudo mysql -e "USE sun_booking_tour; SELECT @@character_set_database as charset, @@collation_database as collation;" 2>/dev/null
    fi
else
    echo ""
    echo -e "${RED}✗ Failed to create database${NC}"
    echo ""
    echo "Alternative methods:"
    echo ""
    echo "1. Using MySQL command line:"
    echo "   mysql -u root -p"
    echo "   CREATE DATABASE IF NOT EXISTS sun_booking_tour CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo "   exit;"
    echo ""
    echo "2. Using the SQL script:"
    echo "   mysql -u root -p < setup-database.sql"
    echo ""
    exit 1
fi

echo ""
echo "=========================================="
echo "Next Steps:"
echo "=========================================="
echo ""
echo "1. Update database password in application.properties:"
echo "   Edit: src/main/resources/application.properties"
echo "   Change: spring.datasource.password=YOUR_MYSQL_PASSWORD"
echo ""
echo "2. Run the application to test connection:"
echo "   mvn spring-boot:run"
echo ""
echo "3. Check logs for 'Database connection successful!'"
echo ""
echo "=========================================="
