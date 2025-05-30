# PowerShell script to test Docker setup for Hotel Reservation System

Write-Host "Testing Docker setup for Hotel Reservation System" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Check if Docker is installed
try {
    docker --version | Out-Null
} catch {
    Write-Host "Error: Docker is not installed or not in PATH" -ForegroundColor Red
    exit 1
}

# Check if Docker Compose is installed
try {
    docker-compose --version | Out-Null
} catch {
    Write-Host "Error: Docker Compose is not installed or not in PATH" -ForegroundColor Red
    exit 1
}

Write-Host "Building Docker images..." -ForegroundColor Cyan
docker-compose build

Write-Host "Starting services..." -ForegroundColor Cyan
docker-compose up -d

Write-Host "Waiting for services to start (30 seconds)..." -ForegroundColor Cyan
Start-Sleep -Seconds 30

Write-Host "Checking if PostgreSQL is running..." -ForegroundColor Cyan
$pgIsReady = docker-compose exec db pg_isready -U postgres
if ($LASTEXITCODE -eq 0) {
    Write-Host "PostgreSQL is running" -ForegroundColor Green
} else {
    Write-Host "Error: PostgreSQL is not running" -ForegroundColor Red
    docker-compose logs db
    docker-compose down
    exit 1
}

Write-Host "Checking if application is running..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -UseBasicParsing -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Host "Application is running and Swagger UI is accessible" -ForegroundColor Green
    } else {
        throw "HTTP status code: $($response.StatusCode)"
    }
} catch {
    Write-Host "Error: Application is not running or Swagger UI is not accessible" -ForegroundColor Red
    docker-compose logs app
    docker-compose down
    exit 1
}

Write-Host "All tests passed!" -ForegroundColor Green
Write-Host "You can access the application at http://localhost:8080" -ForegroundColor Yellow
Write-Host "You can access the Swagger UI at http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow

Write-Host "To stop the services, run: docker-compose down" -ForegroundColor Cyan