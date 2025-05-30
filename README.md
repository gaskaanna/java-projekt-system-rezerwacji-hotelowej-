# Hotel Reservation System - Docker Setup

This document provides instructions for running the Hotel Reservation System application using Docker.

## Prerequisites

- [Docker](https://www.docker.com/get-started) installed on your machine
- [Docker Compose](https://docs.docker.com/compose/install/) installed on your machine

## Quick Start

1. Clone the repository
2. Navigate to the project root directory
3. Run the application with Docker Compose:

```bash
docker-compose up
```

The application will be available at http://localhost:8080

## Docker Configuration

The project includes the following Docker configuration files:

- `Dockerfile`: Defines the container for the Spring Boot application
- `docker-compose.yml`: Orchestrates the application and PostgreSQL database
- `.dockerignore`: Specifies files to exclude from the Docker build context
- `application-docker.properties`: Spring Boot configuration for Docker environment

## Services

The Docker Compose setup includes the following services:

### Application Service (`app`)

- Built from the project's Dockerfile
- Exposes port 8080
- Connects to the PostgreSQL database
- Uses environment variables for configuration

### Database Service (`db`)

- Uses PostgreSQL 16 Alpine image
- Exposes port 5432
- Stores data in a persistent volume
- Initialized with the hotel-reservations-system database

## Environment Variables

The following environment variables are used in the Docker Compose configuration:

### Application Service

- `SPRING_DATASOURCE_URL`: JDBC URL for the PostgreSQL database
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Hibernate schema management strategy
- `SPRING_FLYWAY_ENABLED`: Enable Flyway migrations
- `SPRING_FLYWAY_BASELINE_ON_MIGRATE`: Baseline on migrate for existing databases

### Database Service

- `POSTGRES_DB`: Database name
- `POSTGRES_USER`: Database username
- `POSTGRES_PASSWORD`: Database password

## Data Persistence

The PostgreSQL data is stored in a Docker volume named `postgres-data`, ensuring that data persists across container restarts.

## Development Workflow

### Building the Docker Image

To build the Docker image without starting the services:

```bash
docker-compose build
```

### Running in Development Mode

For development, you can run the services in the background:

```bash
docker-compose up -d
```

### Viewing Logs

To view the logs of the running services:

```bash
docker-compose logs -f
```

### Stopping the Services

To stop the running services:

```bash
docker-compose down
```

To stop the services and remove the volumes:

```bash
docker-compose down -v
```

## Testing the Docker Setup

The project includes test scripts to verify that the Docker setup is working correctly:

### For Linux/macOS

```bash
# Make the script executable
chmod +x test-docker.sh

# Run the test script
./test-docker.sh
```

### For Windows

```powershell
# Run the test script in PowerShell
.\test-docker.ps1
```

The test scripts will:
1. Check if Docker and Docker Compose are installed
2. Build the Docker images
3. Start the services
4. Verify that PostgreSQL is running
5. Verify that the application is running and Swagger UI is accessible

## Troubleshooting

### Database Connection Issues

If the application cannot connect to the database, ensure that:

1. The database service is running: `docker-compose ps`
2. The database is initialized: `docker-compose logs db`
3. The application is using the correct connection URL: `docker-compose logs app`

### Application Startup Issues

If the application fails to start:

1. Check the application logs: `docker-compose logs app`
2. Ensure that the database migrations are running correctly
3. Verify that the environment variables are set correctly in `docker-compose.yml`

## Summary of Docker Implementation

This project has been containerized with Docker to provide a consistent and reproducible environment for development and deployment. The implementation includes:

1. **Multi-stage build** in the Dockerfile for efficient image creation
2. **Docker Compose** for orchestrating the application and database services
3. **Environment variables** for flexible configuration
4. **Volume mapping** for data persistence
5. **Network configuration** for service communication
6. **Docker-specific application properties** for containerized environments
7. **Test scripts** for verifying the Docker setup

These Docker configurations make it easy to run the Hotel Reservation System in any environment that supports Docker, without worrying about dependencies or configuration issues.
