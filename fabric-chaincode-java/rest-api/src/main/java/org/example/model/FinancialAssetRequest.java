package org.example.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * Request model for financial asset operations
 */
public class FinancialAssetRequest {

    @NotBlank(message = "Dealer ID is required")
    private String dealerId;

    @NotBlank(message = "MSISDN is required")
    private String msisdn;

    @NotBlank(message = "MPIN is required")
    private String mpin;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    private BigDecimal balance;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.0", message = "Transaction amount must be non-negative")
    private BigDecimal transAmount;

    @NotBlank(message = "Transaction type is required")
    private String transType;

    private String remarks;

    // Default constructor
    public FinancialAssetRequest() {}

    // Constructor with all fields
    public FinancialAssetRequest(String dealerId, String msisdn, String mpin, 
                                BigDecimal balance, String status, BigDecimal transAmount, 
                                String transType, String remarks) {
        this.dealerId = dealerId;
        this.msisdn = msisdn;
        this.mpin = mpin;
        this.balance = balance;
        this.status = status;
        this.transAmount = transAmount;
        this.transType = transType;
        this.remarks = remarks;
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

    @Override
    public String toString() {
        return "FinancialAssetRequest{" +
                "dealerId='" + dealerId + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", transAmount=" + transAmount +
                ", transType='" + transType + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
