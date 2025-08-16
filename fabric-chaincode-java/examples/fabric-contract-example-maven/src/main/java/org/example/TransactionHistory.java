package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import java.math.BigDecimal;

@DataType()
public class TransactionHistory {

    @Property()
    private String assetId;

    @Property()
    private String transType;

    @Property()
    private String remarks;

    @Property()
    private BigDecimal amount;

    @Property()
    private long timestamp;

    public TransactionHistory() {
    }

    // Getters and Setters
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toJSONString() {
        return new JSONObject(this).toString();
    }

    public static TransactionHistory fromJSONString(String json) {
        JSONObject jsonObject = new JSONObject(json);
        TransactionHistory history = new TransactionHistory();
        
        history.setAssetId(jsonObject.optString("assetId", ""));
        history.setTransType(jsonObject.optString("transType", ""));
        history.setRemarks(jsonObject.optString("remarks", ""));
        history.setAmount(new BigDecimal(jsonObject.optString("amount", "0")));
        history.setTimestamp(jsonObject.optLong("timestamp", 0));
        
        return history;
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "assetId='" + assetId + '\'' +
                ", transType='" + transType + '\'' +
                ", remarks='" + remarks + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
