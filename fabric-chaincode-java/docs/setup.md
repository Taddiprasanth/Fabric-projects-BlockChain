# Hyperledger Fabric Test Network Setup Guide

This guide will help you set up the Hyperledger Fabric test network for the Financial Asset Management system.

## Prerequisites

Before starting, ensure you have the following installed:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Go**: Version 1.19 or higher
- **Node.js**: Version 16 or higher
- **Java**: Version 11 or higher
- **Git**: Latest version

### Installing Prerequisites

#### Docker and Docker Compose
```bash
# For Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io docker-compose

# For macOS
brew install docker docker-compose

# For Windows
# Download Docker Desktop from https://www.docker.com/products/docker-desktop
```

#### Go
```bash
# Download from https://golang.org/dl/
wget https://go.dev/dl/go1.19.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.19.linux-amd64.tar.gz
export PATH=$PATH:/usr/local/go/bin
```

#### Node.js
```bash
# Using Node Version Manager (nvm)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 16
nvm use 16
```

#### Java
```bash
# For Ubuntu/Debian
sudo apt-get install openjdk-11-jdk

# For macOS
brew install openjdk@11

# For Windows
# Download from https://adoptium.net/
```

## Step 1: Clone Fabric Samples

```bash
# Clone the Hyperledger Fabric samples repository
git clone https://github.com/hyperledger/fabric-samples.git
cd fabric-samples

# Checkout a stable version
git checkout v2.4.0
```

## Step 2: Download Fabric Binaries and Docker Images

```bash
# Download Fabric binaries and Docker images
./scripts/bootstrap.sh

# This will download:
# - Fabric binaries (peer, orderer, configtxgen, etc.)
# - Fabric Docker images
# - Fabric CA Docker images
```

## Step 3: Start the Test Network

```bash
# Navigate to the test network directory
cd test-network

# Start the network
./network.sh up

# This will:
# - Start 2 peer organizations (Org1 and Org2)
# - Start 1 orderer organization
# - Create a consortium
# - Start Fabric CA servers
# - Create crypto materials
```

## Step 4: Create a Channel

```bash
# Create a channel named 'mychannel'
./network.sh createChannel -c mychannel

# This will:
# - Generate channel configuration
# - Create the channel
# - Join both organizations to the channel
```

## Step 5: Deploy the Financial Asset Chaincode

```bash
# Deploy the chaincode
./network.sh deployCC -ccn financial-asset -ccp ../fabric-chaincode-java/examples/fabric-contract-example-maven -ccl java

# This will:
# - Package the chaincode
# - Install it on both peers
# - Approve it for both organizations
# - Commit it to the channel
```

## Step 6: Verify the Setup

### Check Running Containers
```bash
docker ps

# You should see containers for:
# - peer0.org1.example.com
# - peer0.org2.example.com
# - orderer.example.com
# - ca_org1
# - ca_org2
```

### Check Channel Information
```bash
# Set environment variables for Org1
export PATH=${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051

# Query channel info
peer channel getinfo -c mychannel
```

### Test Chaincode Invocation
```bash
# Test the chaincode by calling getContractInfo
peer chaincode query -C mychannel -n financial-asset -c '{"Args":["getContractInfo"]}'
```

## Step 7: Set Up Network Configuration for REST API

### Create Connection Profile
Create a file `connection-org1.yaml` in your REST API project:

```yaml
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
```

### Create Wallet Directory
```bash
# Create wallet directory in your REST API project
mkdir -p wallet

# Copy the admin identity
cp -r organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp wallet/appUser
```

## Step 8: Test the Complete System

### Start the REST API
```bash
# Navigate to your REST API project
cd rest-api

# Build and run
mvn clean package
java -jar target/financial-asset-rest-api-1.0.0.jar
```

### Test API Endpoints
```bash
# Health check
curl http://localhost:8080/api/assets/health

# Create an asset
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

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   sudo netstat -tulpn | grep :7051
   
   # Kill the process
   sudo kill -9 <PID>
   ```

2. **Docker Permission Issues**
   ```bash
   # Add user to docker group
   sudo usermod -aG docker $USER
   
   # Logout and login again
   ```

3. **Chaincode Deployment Failed**
   ```bash
   # Check peer logs
   docker logs peer0.org1.example.com
   
   # Check if chaincode is installed
   peer lifecycle chaincode queryinstalled
   ```

4. **TLS Certificate Issues**
   ```bash
   # Regenerate crypto materials
   ./network.sh down
   ./network.sh up
   ```

### Useful Commands

```bash
# Stop the network
./network.sh down

# Restart the network
./network.sh restart

# View logs
docker logs <container-name>

# Execute commands in a container
docker exec -it <container-name> bash

# Check network status
./network.sh status
```

## Network Architecture

The test network consists of:

- **2 Organizations**: Org1 and Org2
- **1 Channel**: mychannel
- **1 Orderer**: Single RAFT orderer
- **2 Peers**: One peer per organization
- **2 CA Servers**: One CA per organization

## Security Considerations

- The test network uses TLS for secure communication
- All identities are managed through Fabric CA
- The network is configured for development/testing only
- Production deployments require additional security measures

## Next Steps

After setting up the test network:

1. **Deploy the REST API** using Docker
2. **Test all API endpoints** with sample data
3. **Monitor the network** using Fabric tools
4. **Scale the network** by adding more organizations if needed

## References

- [Hyperledger Fabric Documentation](https://hyperledger-fabric.readthedocs.io/)
- [Test Network Guide](https://hyperledger-fabric.readthedocs.io/en/latest/test_network.html)
- [Fabric Gateway](https://hyperledger-fabric.readthedocs.io/en/latest/gateway.html)
- [Chaincode Development](https://hyperledger-fabric.readthedocs.io/en/latest/developapps/developing_applications.html)
