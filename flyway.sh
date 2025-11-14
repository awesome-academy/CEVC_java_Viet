#!/bin/bash
# Flyway Helper Script
# Usage: ./flyway.sh [command]
# Commands: info, migrate, validate, clean, repair

# Load environment variables from .env file
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
fi

# Set default command if not provided
COMMAND=${1:-info}

# Run Flyway with environment variables
DB_URL="${DB_URL}" \
DB_USERNAME="${DB_USERNAME}" \
DB_PASSWORD="${DB_PASSWORD}" \
mvn flyway:${COMMAND}
