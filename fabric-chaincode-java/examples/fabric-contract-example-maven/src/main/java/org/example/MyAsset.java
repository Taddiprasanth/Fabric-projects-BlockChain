package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DataType()
public class MyAsset {

    @Property()
    private String dealerId;

    @Property()
    private String msisdn;

    @Property()
    private String mpin;

    @Property()
    private BigDecimal balance;

    @Property()
    private String status;

    @Property()
    private BigDecimal transAmount;

    @Property()
    private String transType;

    @Property()
    private String remarks;

    @Property()
    private String createdAt;

    @Property()
    private String updatedAt;

    public MyAsset() {
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.updatedAt = this.createdAt;
    }

    // Getters and Setters
    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String toJSONString() {
        return new JSONObject(this).toString();
    }

    public static MyAsset fromJSONString(String json) {
        JSONObject jsonObject = new JSONObject(json);
        MyAsset asset = new MyAsset();
        
        asset.setDealerId(jsonObject.optString("dealerId", ""));
        asset.setMsisdn(jsonObject.optString("msisdn", ""));
        asset.setMpin(jsonObject.optString("mpin", ""));
        asset.setBalance(new BigDecimal(jsonObject.optString("balance", "0")));
        asset.setStatus(jsonObject.optString("status", "ACTIVE"));
        asset.setTransAmount(new BigDecimal(jsonObject.optString("transAmount", "0")));
        asset.setTransType(jsonObject.optString("transType", ""));
        asset.setRemarks(jsonObject.optString("remarks", ""));
        asset.setCreatedAt(jsonObject.optString("createdAt", ""));
        asset.setUpdatedAt(jsonObject.optString("updatedAt", ""));
        
        return asset;
    }

    @Override
    public String toString() {
        return "MyAsset{" +
                "dealerId='" + dealerId + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", transAmount=" + transAmount +
                ", transType='" + transType + '\'' +
                ", remarks='" + remarks + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

