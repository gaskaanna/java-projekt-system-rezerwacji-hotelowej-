#!/bin/bash
set -e

echo "Testing Docker setup for Hotel Reservation System"
echo "================================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed or not in PATH"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed or not in PATH"
    exit 1
fi

echo "Building Docker images..."
docker-compose build

echo "Starting services..."
docker-compose up -d

echo "Waiting for services to start (30 seconds)..."
sleep 30

echo "Checking if PostgreSQL is running..."
if docker-compose exec db pg_isready -U postgres; then
    echo "PostgreSQL is running"
else
    echo "Error: PostgreSQL is not running"
    docker-compose logs db
    docker-compose down
    exit 1
fi

echo "Checking if application is running..."
if curl -s http://localhost:8080/swagger-ui.html > /dev/null; then
    echo "Application is running and Swagger UI is accessible"
else
    echo "Error: Application is not running or Swagger UI is not accessible"
    docker-compose logs app
    docker-compose down
    exit 1
fi

echo "All tests passed!"
echo "You can access the application at http://localhost:8080"
echo "You can access the Swagger UI at http://localhost:8080/swagger-ui.html"

echo "To stop the services, run: docker-compose down"