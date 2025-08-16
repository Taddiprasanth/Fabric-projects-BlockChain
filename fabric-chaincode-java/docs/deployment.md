# Deployment Guide for Financial Asset Management System

This guide covers the complete deployment of the Hyperledger Fabric Financial Asset Management system, including the test network, smart contract, and REST API.

## System Overview

The complete system consists of three main components:

1. **Hyperledger Fabric Test Network** (Level 1)
2. **Financial Asset Smart Contract** (Level 2)
3. **REST API Service** (Level 3)

## Prerequisites

- Docker and Docker Compose
- Java 11+
- Maven 3.6+
- Git
- At least 8GB RAM and 20GB disk space

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Applications                      │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST
┌─────────────────────▼───────────────────────────────────────┐
│                REST API Service                             │
│                (Port 8080)                                 │
└─────────────────────┬───────────────────────────────────────┘
                      │ Fabric Gateway
┌─────────────────────▼───────────────────────────────────────┐
│              Hyperledger Fabric Network                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Peer     │  │   Peer     │  │  Orderer    │        │
│  │   Org1     │  │   Org2     │  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│  ┌─────────────┐  ┌─────────────┐                         │
│  │     CA     │  │     CA     │                         │
│  │    Org1    │  │    Org2    │                         │
│  └─────────────┘  └─────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

## Step-by-Step Deployment

### Phase 1: Deploy Hyperledger Fabric Network

#### 1.1 Clone and Setup Fabric Samples
```bash
# Clone Fabric samples
git clone https://github.com/hyperledger/fabric-samples.git
cd fabric-samples
git checkout v2.4.0

# Download binaries and images
./scripts/bootstrap.sh
```

#### 1.2 Start the Network
```bash
cd test-network

# Start the network
./network.sh up

# Create channel
./network.sh createChannel -c mychannel
```

#### 1.3 Verify Network Status
```bash
# Check running containers
docker ps

# Verify channel creation
peer channel getinfo -c mychannel
```

### Phase 2: Deploy Smart Contract

#### 2.1 Build the Chaincode
```bash
# Navigate to the chaincode directory
cd ../../fabric-chaincode-java/examples/fabric-contract-example-maven

# Build the project
mvn clean package

# Verify the JAR file is created
ls -la target/
```

#### 2.2 Deploy to Network
```bash
# Return to test network directory
cd ../../../fabric-samples/test-network

# Deploy the chaincode
./network.sh deployCC -ccn financial-asset -ccp ../fabric-chaincode-java/examples/fabric-contract-example-maven -ccl java
```

#### 2.3 Test Chaincode
```bash
# Test basic functionality
peer chaincode query -C mychannel -n financial-asset -c '{"Args":["getContractInfo"]}'
```

### Phase 3: Deploy REST API

#### 3.1 Prepare Network Configuration
```bash
# Create connection profile
cat > connection-org1.yaml << 'EOF'
name: "test-network-org1"
version: "1.0.0"

client:
  organization: Org1
  connection:
    timeout:
      peer:
        endorser: '300'

organizations:
  Org1:
    mspid: Org1MSP
    peers:
      - peer0.org1.example.com
    certificateAuthorities:
      - ca.org1.example.com

peers:
  peer0.org1.example.com:
    url: grpcs://localhost:7051
    tlsCACerts:
      path: organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
    grpcOptions:
      ssl-target-name-override: peer0.org1.example.com
      hostnameOverride: peer0.org1.example.com

certificateAuthorities:
  ca.org1.example.com:
    url: https://localhost:7054
    caName: ca-org1
    tlsCACerts:
      path: organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem
    registrar:
      - enrollId: admin
        enrollSecret: adminpw
    httpOptions:
      verify: false
EOF

# Create wallet directory
mkdir -p wallet/appUser
cp -r organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/* wallet/appUser/
```

#### 3.2 Build REST API
```bash
# Navigate to REST API directory
cd ../../fabric-chaincode-java/rest-api

# Build the project
mvn clean package

# Verify the JAR file
ls -la target/
```

#### 3.3 Deploy with Docker
```bash
# Build Docker image
docker build -t financial-asset-api .

# Run the container
docker run -d \
  --name financial-asset-api \
  --network host \
  -v $(pwd)/connection-org1.yaml:/app/connection-org1.yaml \
  -v $(pwd)/wallet:/app/wallet \
  financial-asset-api
```

## Alternative Deployment Methods

### Method 1: Docker Compose (Recommended)

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  financial-asset-api:
    build: .
    container_name: financial-asset-api
    ports:
      - "8080:8080"
    volumes:
      - ./connection-org1.yaml:/app/connection-org1.yaml
      - ./wallet:/app/wallet
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    networks:
      - fabric-network
    depends_on:
      - peer0.org1.example.com
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/assets/health"]
      interval: 30s
      timeout: 10s
      retries: 3

networks:
  fabric-network:
    external: true
```

Deploy:
```bash
docker-compose up -d
```

### Method 2: Kubernetes Deployment

Create `k8s-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: financial-asset-api
spec:
  replicas: 2
  selector:
    matchLabels:
      app: financial-asset-api
  template:
    metadata:
      labels:
        app: financial-asset-api
    spec:
      containers:
      - name: financial-asset-api
        image: financial-asset-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        volumeMounts:
        - name: config
          mountPath: /app/connection-org1.yaml
          subPath: connection-org1.yaml
        - name: wallet
          mountPath: /app/wallet
      volumes:
      - name: config
        configMap:
          name: fabric-config
      - name: wallet
        secret:
          secretName: fabric-wallet
---
apiVersion: v1
kind: Service
metadata:
  name: financial-asset-api-service
spec:
  selector:
    app: financial-asset-api
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

Deploy:
```bash
kubectl apply -f k8s-deployment.yaml
```

## Configuration Management

### Environment Variables

```bash
# Application configuration
export SPRING_PROFILES_ACTIVE=production
export SERVER_PORT=8080

# Fabric configuration
export FABRIC_NETWORK_NAME=mychannel
export FABRIC_CONTRACT_NAME=financial-asset
export FABRIC_CONNECTION_PROFILE=connection-org1.yaml
export FABRIC_WALLET_PATH=wallet
export FABRIC_IDENTITY=appUser

# JVM options
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

### Configuration Files

#### application-production.yml
```yaml
server:
  port: 8080

spring:
  profiles:
    active: production

logging:
  level:
    org.example: INFO
    org.hyperledger.fabric: WARN

fabric:
  network:
    name: ${FABRIC_NETWORK_NAME:mychannel}
    contract: ${FABRIC_CONTRACT_NAME:financial-asset}
  connection:
    profile: ${FABRIC_CONNECTION_PROFILE:connection-org1.yaml}
    wallet: ${FABRIC_WALLET_PATH:wallet}
    identity: ${FABRIC_IDENTITY:appUser}
```

## Monitoring and Health Checks

### Health Endpoints
```bash
# Application health
curl http://localhost:8080/api/assets/health

# Actuator endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
```

### Logging
```bash
# View application logs
docker logs financial-asset-api

# Follow logs
docker logs -f financial-asset-api

# View specific log levels
docker logs financial-asset-api | grep "ERROR"
```

### Metrics
```bash
# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP metrics
curl http://localhost:8080/actuator/metrics/http.server.requests
```

## Testing the Deployment

### 1. Health Check
```bash
curl http://localhost:8080/api/assets/health
```

### 2. Create Asset
```bash
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

### 3. Query Asset
```bash
# Get the asset ID from the create response
ASSET_ID="ASSET_DEALER001_1234567890_1234567890"

curl http://localhost:8080/api/assets/$ASSET_ID
```

### 4. Update Balance
```bash
curl -X PUT "http://localhost:8080/api/assets/$ASSET_ID/balance?newBalance=1500.00&transAmount=500.00&transType=DEPOSIT&remarks=Additional deposit"
```

### 5. Get Transaction History
```bash
curl http://localhost:8080/api/assets/$ASSET_ID/history
```

## Troubleshooting

### Common Issues

1. **API Service Won't Start**
   ```bash
   # Check logs
   docker logs financial-asset-api
   
   # Check if Fabric network is running
   docker ps | grep peer
   
   # Verify connection profile
   ls -la connection-org1.yaml
   ```

2. **Fabric Connection Failed**
   ```bash
   # Check network status
   cd fabric-samples/test-network
   ./network.sh status
   
   # Restart network if needed
   ./network.sh restart
   ```

3. **Chaincode Not Found**
   ```bash
   # Check if chaincode is deployed
   peer lifecycle chaincode querycommitted -C mychannel
   
   # Redeploy if needed
   ./network.sh deployCC -ccn financial-asset -ccp ../fabric-chaincode-java/examples/fabric-contract-example-maven -ccl java
   ```

4. **Permission Issues**
   ```bash
   # Check wallet permissions
   ls -la wallet/
   
   # Fix permissions
   chmod -R 755 wallet/
   ```

### Performance Tuning

```bash
# Increase JVM memory
export JAVA_OPTS="-Xmx4g -Xms2g"

# Enable GC logging
export JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# Restart service
docker restart financial-asset-api
```

## Backup and Recovery

### Backup Configuration
```bash
# Backup configuration files
tar -czf config-backup-$(date +%Y%m%d).tar.gz \
  connection-org1.yaml \
  wallet/ \
  application.yml
```

### Recovery Procedure
```bash
# Stop the service
docker stop financial-asset-api

# Restore configuration
tar -xzf config-backup-YYYYMMDD.tar.gz

# Restart the service
docker start financial-asset-api
```

## Security Considerations

1. **Network Security**
   - Use TLS for all communications
   - Restrict access to Fabric network ports
   - Implement firewall rules

2. **API Security**
   - Add authentication and authorization
   - Implement rate limiting
   - Use HTTPS in production

3. **Data Security**
   - Encrypt sensitive data
   - Implement audit logging
   - Regular security updates

## Scaling Considerations

1. **Horizontal Scaling**
   - Deploy multiple API instances
   - Use load balancer
   - Implement session management

2. **Vertical Scaling**
   - Increase JVM memory
   - Optimize database queries
   - Use connection pooling

## Maintenance

### Regular Tasks
```bash
# Daily
docker logs financial-asset-api | grep ERROR

# Weekly
docker system prune -f
docker image prune -f

# Monthly
# Update dependencies
# Review security patches
# Performance analysis
```

### Update Procedures
```bash
# Build new image
docker build -t financial-asset-api:v2 .

# Update deployment
docker-compose down
docker-compose up -d

# Verify update
curl http://localhost:8080/api/assets/health
```

## Support and Documentation

- **Logs**: Check Docker logs for detailed information
- **Health Checks**: Use built-in health endpoints
- **Monitoring**: Implement external monitoring tools
- **Documentation**: Keep deployment procedures updated

## Next Steps

After successful deployment:

1. **Load Testing**: Test system under various loads
2. **Security Audit**: Review security configurations
3. **Backup Strategy**: Implement automated backups
4. **Monitoring**: Set up comprehensive monitoring
5. **Documentation**: Document operational procedures
