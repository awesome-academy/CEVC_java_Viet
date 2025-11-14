#!/bin/bash

# JWT Authentication API Testing Script
# This script tests the JWT authentication endpoints

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "================================"
echo "JWT Authentication API Test"
echo "================================"
echo ""

# Test 1: Login with admin account
echo -e "${YELLOW}Test 1: Login with admin account${NC}"
echo "POST $BASE_URL/auth/login"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@sunbooking.com",
    "password": "Admin@123"
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
  echo -e "${GREEN}✓ Login successful (HTTP $HTTP_CODE)${NC}"
  echo "$BODY" | jq '.'
  
  # Extract JWT token
  JWT_TOKEN=$(echo "$BODY" | jq -r '.data.token')
  echo ""
  echo "JWT Token: $JWT_TOKEN"
  echo ""
else
  echo -e "${RED}✗ Login failed (HTTP $HTTP_CODE)${NC}"
  echo "$BODY" | jq '.'
  exit 1
fi

echo ""
echo "================================"
echo ""

# Test 2: Register new user
echo -e "${YELLOW}Test 2: Register new user${NC}"
echo "POST $BASE_URL/auth/register"
TIMESTAMP=$(date +%s)
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Test User $TIMESTAMP\",
    \"email\": \"test$TIMESTAMP@example.com\",
    \"password\": \"Test@123\",
    \"phone\": \"0987654321\"
  }")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 201 ] || [ "$HTTP_CODE" -eq 200 ]; then
  echo -e "${GREEN}✓ Registration successful (HTTP $HTTP_CODE)${NC}"
  echo "$BODY" | jq '.'
else
  echo -e "${RED}✗ Registration failed (HTTP $HTTP_CODE)${NC}"
  echo "$BODY" | jq '.'
fi

echo ""
echo "================================"
echo ""

# Test 3: Access protected endpoint WITHOUT token
echo -e "${YELLOW}Test 3: Access protected endpoint WITHOUT token${NC}"
echo "GET $BASE_URL/profile"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/profile")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 401 ]; then
  echo -e "${GREEN}✓ Correctly rejected (HTTP $HTTP_CODE)${NC}"
  echo "$BODY" | jq '.'
else
  echo -e "${RED}✗ Expected 401 but got HTTP $HTTP_CODE${NC}"
  echo "$BODY"
fi

echo ""
echo "================================"
echo ""

# Test 4: Access protected endpoint WITH valid token
if [ ! -z "$JWT_TOKEN" ]; then
  echo -e "${YELLOW}Test 4: Access protected endpoint WITH valid token${NC}"
  echo "GET $BASE_URL/profile"
  RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/profile" \
    -H "Authorization: Bearer $JWT_TOKEN")

  HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
  BODY=$(echo "$RESPONSE" | sed '$d')

  if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 404 ]; then
    echo -e "${GREEN}✓ Token accepted (HTTP $HTTP_CODE)${NC}"
    if [ "$HTTP_CODE" -eq 404 ]; then
      echo "Note: 404 expected if /api/profile endpoint not implemented yet"
    fi
    echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
  else
    echo -e "${RED}✗ Token rejected (HTTP $HTTP_CODE)${NC}"
    echo "$BODY"
  fi
fi

echo ""
echo "================================"
echo ""

# Test 5: Access public endpoint (no auth required)
echo -e "${YELLOW}Test 5: Access public endpoint (tours list)${NC}"
echo "GET $BASE_URL/tours"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/tours")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 404 ]; then
  echo -e "${GREEN}✓ Public endpoint accessible (HTTP $HTTP_CODE)${NC}"
  if [ "$HTTP_CODE" -eq 404 ]; then
    echo "Note: 404 expected if /api/tours endpoint not implemented yet"
  fi
  echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
else
  echo -e "${RED}✗ Public endpoint failed (HTTP $HTTP_CODE)${NC}"
  echo "$BODY"
fi

echo ""
echo "================================"
echo "Testing complete!"
echo "================================"
