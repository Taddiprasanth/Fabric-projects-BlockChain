# Hyperledger Fabric Financial Asset Management System

This project implements a blockchain-based system for managing and tracking financial assets using Hyperledger Fabric. The system supports creating assets, updating asset values, querying the world state to read assets, and retrieving asset transaction history.

## Project Overview

The financial institution needs to implement a blockchain-based system to manage and track assets with the following specific attributes:
- **DEALERID**: Unique identifier for the dealer/account holder
- **MSISDN**: Mobile Station International Subscriber Directory Number
- **MPIN**: Mobile Personal Identification Number for security
- **BALANCE**: Current account balance
- **STATUS**: Account status (ACTIVE, SUSPENDED, CLOSED, etc.)
- **TRANSAMOUNT**: Transaction amount for the current operation
- **TRANSTYPE**: Type of transaction (DEPOSIT, WITHDRAWAL, TRANSFER, etc.)
- **REMARKS**: Additional notes or comments about the transaction

## Project Structure

```
fabric-chaincode-java/
├── examples/
│   └── fabric-contract-example-maven/          # Smart Contract (Level-2)
│       ├── src/main/java/org/example/
│       │   ├── FinancialAsset.java            # Asset data model
│       │   ├── FinancialAssetContract.java    # Main contract
│       │   └── TransactionHistory.java        # Transaction tracking
│       ├── src/test/java/org/example/
│       │   └── FinancialAssetContractTest.java # Test suite
│       ├── pom.xml                            # Maven configuration
│       └── README.md                          # Contract documentation
├── rest-api/                                  # REST API (Level-3)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/example/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   └── model/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile                             # Docker image
│   ├── pom.xml                               # Maven configuration
│   └── README.md                             # API documentation
├── network/                                   # Test Network (Level-1)
│   ├── scripts/
│   ├── organizations/
│   └── docker-compose.yaml
└── docs/                                      # Documentation
    ├── setup.md                               # Network setup guide
    ├── deployment.md                          # Deployment guide
    └── api-reference.md                       # API reference
```

## Level 1: Hyperledger Fabric Test Network Setup

### Prerequisites
- Docker and Docker Compose
- Go 1.19+ (for Fabric binaries)
- Node.js 16+ (for Fabric samples)
- Java 11+ (for chaincode)

### Setup Steps
1. **Clone Fabric Samples**
   ```bash
   git clone https://github.com/hyperledger/fabric-samples.git
   cd fabric-samples
   ```

2. **Start Test Network**
   ```bash
   cd test-network
   ./network.sh up
   ```

3. **Create Channel**
   ```bash
   ./network.sh createChannel -c mychannel
   ```

4. **Deploy Chaincode**
   ```bash
   ./network.sh deployCC -ccn financial-asset -ccp ../fabric-chaincode-java/examples/fabric-contract-example-maven -ccl java
   ```

## Level 2: Smart Contract Development

### Features Implemented
- **Asset Management**: Create, read, update, delete financial assets
- **Transaction Tracking**: Complete audit trail for all operations
- **Query Functions**: Search assets by dealer, status, MSISDN
- **Security**: MPIN validation and status management
- **Compliance**: Regulatory-compliant transaction history

### Contract Functions
- `createAsset()` - Create new financial asset
- `readAsset()` - Read asset details
- `updateAsset()` - Update asset attributes
- `deleteAsset()` - Delete asset with history preservation
- `updateBalance()` - Update balance with transaction tracking
- `updateStatus()` - Change account status
- `queryAssetsByDealer()` - Query assets by dealer
- `queryAssetsByStatus()` - Query assets by status
- `queryAssetsByMsisdn()` - Query assets by MSISDN
- `getTransactionHistory()` - Get complete transaction history

### Testing
```bash
cd examples/fabric-contract-example-maven
mvn test
```

## Level 3: REST API Development

### API Endpoints
- `POST /api/assets` - Create new asset
- `GET /api/assets/{id}` - Get asset by ID
- `PUT /api/assets/{id}` - Update asset
- `DELETE /api/assets/{id}` - Delete asset
- `PUT /api/assets/{id}/balance` - Update balance
- `PUT /api/assets/{id}/status` - Update status
- `GET /api/assets/dealer/{dealerId}` - Get assets by dealer
- `GET /api/assets/status/{status}` - Get assets by status
- `GET /api/assets/msisdn/{msisdn}` - Get assets by MSISDN
- `GET /api/assets/{id}/history` - Get transaction history

### Docker Image
```bash
cd rest-api
docker build -t financial-asset-api .
docker run -p 8080:8080 financial-asset-api
```

## Security Features

- **MPIN Validation**: Secure authentication using mobile PIN
- **Transaction Tracking**: Complete audit trail for all operations
- **Status Management**: Account status control for security
- **Immutable Records**: Blockchain ensures data integrity
- **Access Control**: Fabric's built-in access control mechanisms

## Compliance

This system is designed to meet financial institution requirements for:
- Asset tracking and management
- Transaction history and audit trails
- Security and access control
- Regulatory compliance
- Data transparency and immutability

## Getting Started

1. **Setup Test Network** (Level 1)
   - Follow the setup guide in `docs/setup.md`

2. **Deploy Smart Contract** (Level 2)
   - Build and deploy the chaincode
   - Run tests to verify functionality

3. **Deploy REST API** (Level 3)
   - Build Docker image
   - Deploy to your infrastructure

## Contributing

This project is developed for educational purposes. Please refer to the contributing guidelines for any modifications.

## License

This project is licensed under the Apache License 2.0.

## References

- [Hyperledger Fabric Documentation](https://hyperledger-fabric.readthedocs.io/)
- [Getting Started Guide](https://hyperledger-fabric.readthedocs.io/en/latest/getting_started.html)
- [Test Network Guide](https://hyperledger-fabric.readthedocs.io/en/latest/test_network.html)
- [Smart Contract Development](https://hyperledger-fabric.readthedocs.io/en/latest/smartcontract/smartcontract.html)
- [Fabric Gateway](https://hyperledger-fabric.readthedocs.io/en/latest/gateway.html)
