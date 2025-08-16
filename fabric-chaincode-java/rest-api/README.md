# Financial Asset Management REST API

This is a Spring Boot REST API that provides a RESTful interface to the Hyperledger Fabric Financial Asset Management smart contract.

## Overview

The REST API enables external applications to interact with the blockchain-based financial asset management system through standard HTTP endpoints. It handles all the complexity of Fabric Gateway communication and provides a clean, RESTful interface.

## Features

- **Asset Management**: Create, read, update, and delete financial assets
- **Balance Operations**: Update account balances with transaction tracking
- **Status Management**: Change account statuses
- **Query Operations**: Search assets by various criteria
- **Transaction History**: Retrieve complete audit trails
- **Health Monitoring**: Built-in health checks and monitoring
- **Docker Support**: Containerized deployment

## API Endpoints

### Core Asset Operations

#### Create Asset
```http
POST /api/assets
Content-Type: application/json

{
  "dealerId": "DEALER001",
  "msisdn": "1234567890",
  "mpin": "1234",
  "balance": "1000.00",
  "status": "ACTIVE",
  "transAmount": "100.00",
  "transType": "DEPOSIT",
  "remarks": "Initial deposit"
}
```

#### Get Asset
```http
GET /api/assets/{id}
```

#### Update Asset
```http
PUT /api/assets/{id}
Content-Type: application/json

{
  "dealerId": "DEALER001",
  "msisdn": "1234567890",
  "mpin": "1234",
  "balance": "1500.00",
  "status": "ACTIVE",
  "transAmount": "500.00",
  "transType": "DEPOSIT",
  "remarks": "Additional deposit"
}
```

#### Delete Asset
```http
DELETE /api/assets/{id}
```

### Specialized Operations

#### Update Balance
```http
PUT /api/assets/{id}/balance?newBalance=1500.00&transAmount=500.00&transType=DEPOSIT&remarks=Additional deposit
```

#### Update Status
```http
PUT /api/assets/{id}/status?newStatus=SUSPENDED&remarks=Account suspended
```

### Query Operations

#### Get Assets by Dealer
```http
GET /api/assets/dealer/{dealerId}
```

#### Get Assets by Status
```http
GET /api/assets/status/{status}
```

#### Get Assets by MSISDN
```http
GET /api/assets/msisdn/{msisdn}
```

#### Get Transaction History
```http
GET /api/assets/{id}/history
```

### Health Check
```http
GET /api/assets/health
```

## Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": "Response data",
  "timestamp": "2024-01-01T12:00:00",
  "errorCode": null
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T12:00:00",
  "errorCode": "ERROR_CODE"
}
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Hyperledger Fabric test network running
- Fabric Gateway Java SDK

## Building the Application

```bash
# Clone the repository
git clone <repository-url>
cd rest-api

# Build the project
mvn clean package

# The JAR file will be created in target/financial-asset-rest-api-1.0.0.jar
```

## Configuration

### Application Properties

The application can be configured through `application.yml`:

```yaml
server:
  port: 8080

fabric:
  network:
    name: mychannel
    contract: financial-asset
  connection:
    profile: connection-org1.yaml
    wallet: wallet
    identity: appUser
```

### Fabric Network Configuration

Ensure you have the following files in your project root:
- `connection-org1.yaml` - Fabric network connection profile
- `wallet/` directory - Contains user identities

## Running the Application

### Local Development
```bash
mvn spring-boot:run
```

### Production
```bash
java -jar target/financial-asset-rest-api-1.0.0.jar
```

### Docker
```bash
# Build the image
docker build -t financial-asset-api .

# Run the container
docker run -p 8080:8080 financial-asset-api
```

## Docker Deployment

### Building Docker Image
```bash
docker build -t financial-asset-api .
```

### Running Container
```bash
docker run -d \
  --name financial-asset-api \
  -p 8080:8080 \
  -v $(pwd)/connection-org1.yaml:/app/connection-org1.yaml \
  -v $(pwd)/wallet:/app/wallet \
  financial-asset-api
```

### Docker Compose
```yaml
version: '3.8'
services:
  financial-asset-api:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./connection-org1.yaml:/app/connection-org1.yaml
      - ./wallet:/app/wallet
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/assets/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing
```bash
# Test health endpoint
curl http://localhost:8080/api/assets/health

# Test creating an asset
curl -X POST http://localhost:8080/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "dealerId": "DEALER001",
    "msisdn": "1234567890",
    "mpin": "1234",
    "balance": "1000.00",
    "status": "ACTIVE",
    "transAmount": "100.00",
    "transType": "DEPOSIT",
    "remarks": "Initial deposit"
  }'
```

## Monitoring and Health Checks

The application includes built-in monitoring:

- **Health Endpoint**: `/api/assets/health`
- **Actuator Endpoints**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- **Logging**: Structured logging with configurable levels
- **Metrics**: Application metrics through Spring Boot Actuator

## Security Considerations

- Input validation using Bean Validation
- Error handling without exposing internal details
- CORS configuration for web applications
- Logging of all operations for audit purposes

## Troubleshooting

### Common Issues

1. **Fabric Connection Failed**
   - Verify network is running
   - Check connection profile path
   - Ensure wallet contains valid identity

2. **Port Already in Use**
   - Change port in `application.yml`
   - Kill existing process using the port

3. **Memory Issues**
   - Adjust JVM options in Dockerfile
   - Increase container memory limits

### Logs
Check application logs for detailed error information:
```bash
docker logs financial-asset-api
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.
