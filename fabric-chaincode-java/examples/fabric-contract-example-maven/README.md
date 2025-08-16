# Financial Asset Management Contract - Maven

This is a Hyperledger Fabric chaincode example that implements a blockchain-based system to manage and track financial assets for a financial institution.

## Overview

The system supports creating assets, updating asset values, querying the world state to read assets, and retrieving asset transaction history. The assets represent accounts with specific attributes to ensure security, transparency, and immutability of asset records.

## Asset Attributes

Each financial asset contains the following attributes:

- **DEALERID**: Unique identifier for the dealer/account holder
- **MSISDN**: Mobile Station International Subscriber Directory Number
- **MPIN**: Mobile Personal Identification Number for security
- **BALANCE**: Current account balance
- **STATUS**: Account status (ACTIVE, SUSPENDED, CLOSED, etc.)
- **TRANSAMOUNT**: Transaction amount for the current operation
- **TRANSTYPE**: Type of transaction (DEPOSIT, WITHDRAWAL, TRANSFER, etc.)
- **REMARKS**: Additional notes or comments about the transaction

## Contract Functions

### Core Asset Operations
- `createAsset()` - Create a new financial asset
- `readAsset()` - Read asset details by ID
- `updateAsset()` - Update all asset attributes
- `deleteAsset()` - Delete an asset (with transaction history preservation)

### Specialized Operations
- `updateBalance()` - Update account balance with transaction tracking
- `updateStatus()` - Change account status
- `queryAssetsByDealer()` - Query all assets for a specific dealer
- `queryAssetsByStatus()` - Query assets by status
- `queryAssetsByMsisdn()` - Query assets by MSISDN
- `getTransactionHistory()` - Retrieve complete transaction history for an asset

### Utility Functions
- `assetExists()` - Check if an asset exists
- `getContractInfo()` - Get contract information

## Transaction History

Every operation on an asset creates a transaction history record that includes:
- Asset ID
- Transaction type
- Remarks
- Amount
- Timestamp

This ensures complete auditability and compliance with financial regulations.

## Building and Testing

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package
mvn package
```

## Usage Examples

### Creating an Asset
```java
contract.createAsset(ctx, "ASSET001", "DEALER001", "1234567890", "1234", 
                    "1000.00", "ACTIVE", "100.00", "DEPOSIT", "Initial deposit");
```

### Updating Balance
```java
contract.updateBalance(ctx, "ASSET001", "1500.00", "500.00", 
                      "DEPOSIT", "Additional deposit");
```

### Querying Assets
```java
List<FinancialAsset> assets = contract.queryAssetsByDealer(ctx, "DEALER001");
List<FinancialAsset> activeAssets = contract.queryAssetsByStatus(ctx, "ACTIVE");
```

### Getting Transaction History
```java
List<TransactionHistory> history = contract.getTransactionHistory(ctx, "ASSET001");
```

## Security Features

- **MPIN Validation**: Secure authentication using mobile PIN
- **Transaction Tracking**: Complete audit trail for all operations
- **Status Management**: Account status control for security
- **Immutable Records**: Blockchain ensures data integrity
- **Access Control**: Fabric's built-in access control mechanisms

## Compliance

This contract is designed to meet financial institution requirements for:
- Asset tracking and management
- Transaction history and audit trails
- Security and access control
- Regulatory compliance
- Data transparency and immutability

## Files Structure

- `src/main/java/org/example/FinancialAsset.java` - Financial asset data model
- `src/main/java/org/example/FinancialAssetContract.java` - Main contract implementation
- `src/main/java/org/example/TransactionHistory.java` - Transaction history tracking
- `src/test/java/org/example/FinancialAssetContractTest.java` - Comprehensive test suite
