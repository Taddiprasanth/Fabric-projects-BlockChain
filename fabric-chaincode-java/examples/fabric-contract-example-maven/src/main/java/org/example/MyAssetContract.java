/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static java.nio.charset.StandardCharsets.UTF_8;

@Contract(name = "MyAssetContract",
    info = @Info(title = "Financial Asset Management Contract",
                description = "Blockchain-based system to manage and track financial assets with security and transparency",
                version = "1.0.0",
                license = @License(name = "SPDX-License-Identifier: Apache-2.0", url = ""),
                contact = @Contact(email = "admin@financialinstitution.com",
                                name = "Financial Institution",
                                url = "https://financialinstitution.com")))
@Default
public class MyAssetContract implements ContractInterface {
    
    public MyAssetContract() {
        System.out.println("Financial Asset Management Contract initialized");
    }
    
    @Transaction()
    public boolean assetExists(Context ctx, String assetId) {
        byte[] buffer = ctx.getStub().getState(assetId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public void createAsset(Context ctx, String assetId, String dealerId, String msisdn, 
                           String mpin, String balance, String status, String transAmount, 
                           String transType, String remarks) {
        boolean exists = assetExists(ctx, assetId);
        if (exists) {
            throw new RuntimeException("The asset " + assetId + " already exists");
        }
        
        MyAsset asset = new MyAsset();
        asset.setDealerId(dealerId);
        asset.setMsisdn(msisdn);
        asset.setMpin(mpin);
        asset.setBalance(new BigDecimal(balance));
        asset.setStatus(status);
        asset.setTransAmount(new BigDecimal(transAmount));
        asset.setTransType(transType);
        asset.setRemarks(remarks);
        
        ctx.getStub().putState(assetId, asset.toJSONString().getBytes(UTF_8));
        
        // Create transaction history record
        createTransactionHistory(ctx, assetId, "CREATE", "Asset created", new BigDecimal(transAmount));
        
        System.out.println("Financial asset created: " + assetId + " for dealer: " + dealerId);
    }

    @Transaction()
    public MyAsset readAsset(Context ctx, String assetId) {
        boolean exists = assetExists(ctx, assetId);
        if (!exists) {
            throw new RuntimeException("The asset " + assetId + " does not exist");
        }

        MyAsset asset = MyAsset.fromJSONString(new String(ctx.getStub().getState(assetId), UTF_8));
        return asset;
    }

    @Transaction()
    public void updateAsset(Context ctx, String assetId, String dealerId, String msisdn, 
                           String mpin, String balance, String status, String transAmount, 
                           String transType, String remarks) {
        boolean exists = assetExists(ctx, assetId);
        if (!exists) {
            throw new RuntimeException("The asset " + assetId + " does not exist");
        }
        
        MyAsset asset = new MyAsset();
        asset.setDealerId(dealerId);
        asset.setMsisdn(msisdn);
        asset.setMpin(mpin);
        asset.setBalance(new BigDecimal(balance));
        asset.setStatus(status);
        asset.setTransAmount(new BigDecimal(transAmount));
        asset.setTransType(transType);
        asset.setRemarks(remarks);
        asset.updateTimestamp();

        ctx.getStub().putState(assetId, asset.toJSONString().getBytes(UTF_8));
        
        // Create transaction history record
        createTransactionHistory(ctx, assetId, "UPDATE", "Asset updated", new BigDecimal(transAmount));
        
        System.out.println("Financial asset updated: " + assetId);
    }

    @Transaction()
    public void deleteAsset(Context ctx, String assetId) {
        boolean exists = assetExists(ctx, assetId);
        if (!exists) {
            throw new RuntimeException("The asset " + assetId + " does not exist");
        }
        
        // Create transaction history record before deletion
        createTransactionHistory(ctx, assetId, "DELETE", "Asset deleted", BigDecimal.ZERO);
        
        ctx.getStub().delState(assetId);
        System.out.println("Financial asset deleted: " + assetId);
    }

    @Transaction()
    public void updateBalance(Context ctx, String assetId, String newBalance, String transAmount, 
                             String transType, String remarks) {
        boolean exists = assetExists(ctx, assetId);
        if (!exists) {
            throw new RuntimeException("The asset " + assetId + " does not exist");
        }
        
        MyAsset asset = MyAsset.fromJSONString(new String(ctx.getStub().getState(assetId), UTF_8));
        asset.setBalance(new BigDecimal(newBalance));
        asset.setTransAmount(new BigDecimal(transAmount));
        asset.setTransType(transType);
        asset.setRemarks(remarks);
        asset.updateTimestamp();

        ctx.getStub().putState(assetId, asset.toJSONString().getBytes(UTF_8));
        
        // Create transaction history record
        createTransactionHistory(ctx, assetId, transType, remarks, new BigDecimal(transAmount));
        
        System.out.println("Balance updated for asset: " + assetId + " to: " + newBalance);
    }

    @Transaction()
    public void updateStatus(Context ctx, String assetId, String newStatus, String remarks) {
        boolean exists = assetExists(ctx, assetId);
        if (!exists) {
            throw new RuntimeException("The asset " + assetId + " does not exist");
        }
        
        MyAsset asset = MyAsset.fromJSONString(new String(ctx.getStub().getState(assetId), UTF_8));
        asset.setStatus(newStatus);
        asset.setRemarks(remarks);
        asset.updateTimestamp();

        ctx.getStub().putState(assetId, asset.toJSONString().getBytes(UTF_8));
        
        // Create transaction history record
        createTransactionHistory(ctx, assetId, "STATUS_CHANGE", "Status changed to: " + newStatus, BigDecimal.ZERO);
        
        System.out.println("Status updated for asset: " + assetId + " to: " + newStatus);
    }

    @Transaction()
    public List<MyAsset> queryAssetsByDealer(Context ctx, String dealerId) {
        List<MyAsset> assets = new ArrayList<>();
        ChaincodeStub stub = ctx.getStub();
        
        QueryResultsIterator<KeyValue> results = stub.getQueryResult("{\"selector\":{\"dealerId\":\"" + dealerId + "\"}}");
        
        for (KeyValue result : results) {
            MyAsset asset = MyAsset.fromJSONString(new String(result.getValue(), UTF_8));
            assets.add(asset);
        }
        
        return assets;
    }

    @Transaction()
    public List<MyAsset> queryAssetsByStatus(Context ctx, String status) {
        List<MyAsset> assets = new ArrayList<>();
        ChaincodeStub stub = ctx.getStub();
        
        QueryResultsIterator<KeyValue> results = stub.getQueryResult("{\"selector\":{\"status\":\"" + status + "\"}}");
        
        for (KeyValue result : results) {
            MyAsset asset = MyAsset.fromJSONString(new String(result.getValue(), UTF_8));
            assets.add(asset);
        }
        
        return assets;
    }

    @Transaction()
    public List<MyAsset> queryAssetsByMsisdn(Context ctx, String msisdn) {
        List<MyAsset> assets = new ArrayList<>();
        ChaincodeStub stub = ctx.getStub();
        
        QueryResultsIterator<KeyValue> results = stub.getQueryResult("{\"selector\":{\"msisdn\":\"" + msisdn + "\"}}");
        
        for (KeyValue result : results) {
            MyAsset asset = MyAsset.fromJSONString(new String(result.getValue(), UTF_8));
            assets.add(asset);
        }
        
        return assets;
    }

    @Transaction()
    public List<TransactionHistory> getTransactionHistory(Context ctx, String assetId) {
        List<TransactionHistory> history = new ArrayList<>();
        ChaincodeStub stub = ctx.getStub();
        
        QueryResultsIterator<KeyValue> results = stub.getQueryResult("{\"selector\":{\"assetId\":\"" + assetId + "\"}}");
        
        for (KeyValue result : results) {
            if (result.getKey().startsWith("HISTORY_")) {
                TransactionHistory txHistory = TransactionHistory.fromJSONString(new String(result.getValue(), UTF_8));
                history.add(txHistory);
            }
        }
        
        return history;
    }

    private void createTransactionHistory(Context ctx, String assetId, String transType, 
                                       String remarks, BigDecimal amount) {
        String historyKey = "HISTORY_" + assetId + "_" + System.currentTimeMillis();
        TransactionHistory history = new TransactionHistory();
        history.setAssetId(assetId);
        history.setTransType(transType);
        history.setRemarks(remarks);
        history.setAmount(amount);
        history.setTimestamp(System.currentTimeMillis());
        
        ctx.getStub().putState(historyKey, history.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public String getContractInfo() {
        return "Financial Asset Management Contract v1.0.0 - " +
               "Manages financial assets with DEALERID, MSISDN, MPIN, BALANCE, STATUS, TRANSAMOUNT, TRANSTYPE, and REMARKS";
    }
}