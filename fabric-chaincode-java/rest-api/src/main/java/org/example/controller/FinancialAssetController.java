package org.example.controller;

import org.example.model.ApiResponse;
import org.example.model.FinancialAssetRequest;
import org.example.service.FabricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Controller for Financial Asset Management
 */
@RestController
@RequestMapping("/api/assets")
@Validated
@CrossOrigin(origins = "*")
public class FinancialAssetController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialAssetController.class);

    @Autowired
    private FabricService fabricService;

    /**
     * Create a new financial asset
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createAsset(@Valid @RequestBody FinancialAssetRequest request) {
        try {
            logger.info("Creating asset for dealer: {}", request.getDealerId());
            
            String assetId = generateAssetId(request.getDealerId(), request.getMsisdn());
            
            fabricService.submitTransaction("createAsset", 
                assetId,
                request.getDealerId(),
                request.getMsisdn(),
                request.getMpin(),
                request.getBalance().toString(),
                request.getStatus(),
                request.getTransAmount().toString(),
                request.getTransType(),
                request.getRemarks() != null ? request.getRemarks() : ""
            );

            return ResponseEntity.ok(ApiResponse.success(assetId, "Asset created successfully"));
            
        } catch (Exception e) {
            logger.error("Error creating asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create asset: " + e.getMessage()));
        }
    }

    /**
     * Get asset by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> getAsset(@PathVariable String id) {
        try {
            logger.info("Getting asset with ID: {}", id);
            
            byte[] result = fabricService.evaluateTransaction("readAsset", id);
            String assetData = new String(result);
            
            return ResponseEntity.ok(ApiResponse.success(assetData, "Asset retrieved successfully"));
            
        } catch (Exception e) {
            logger.error("Error retrieving asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve asset: " + e.getMessage()));
        }
    }

    /**
     * Update asset
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateAsset(@PathVariable String id, 
                                                         @Valid @RequestBody FinancialAssetRequest request) {
        try {
            logger.info("Updating asset with ID: {}", id);
            
            fabricService.submitTransaction("updateAsset", 
                id,
                request.getDealerId(),
                request.getMsisdn(),
                request.getMpin(),
                request.getBalance().toString(),
                request.getStatus(),
                request.getTransAmount().toString(),
                request.getTransType(),
                request.getRemarks() != null ? request.getRemarks() : ""
            );

            return ResponseEntity.ok(ApiResponse.success(id, "Asset updated successfully"));
            
        } catch (Exception e) {
            logger.error("Error updating asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update asset: " + e.getMessage()));
        }
    }

    /**
     * Delete asset
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAsset(@PathVariable String id) {
        try {
            logger.info("Deleting asset with ID: {}", id);
            
            fabricService.submitTransaction("deleteAsset", id);

            return ResponseEntity.ok(ApiResponse.success(id, "Asset deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting asset", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete asset: " + e.getMessage()));
        }
    }

    /**
     * Update asset balance
     */
    @PutMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<String>> updateBalance(@PathVariable String id,
                                                           @RequestParam String newBalance,
                                                           @RequestParam String transAmount,
                                                           @RequestParam String transType,
                                                           @RequestParam(required = false) String remarks) {
        try {
            logger.info("Updating balance for asset: {} to {}", id, newBalance);
            
            fabricService.submitTransaction("updateBalance", 
                id, newBalance, transAmount, transType, 
                remarks != null ? remarks : ""
            );

            return ResponseEntity.ok(ApiResponse.success(id, "Balance updated successfully"));
            
        } catch (Exception e) {
            logger.error("Error updating balance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update balance: " + e.getMessage()));
        }
    }

    /**
     * Update asset status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable String id,
                                                          @RequestParam String newStatus,
                                                          @RequestParam(required = false) String remarks) {
        try {
            logger.info("Updating status for asset: {} to {}", id, newStatus);
            
            fabricService.submitTransaction("updateStatus", 
                id, newStatus, remarks != null ? remarks : ""
            );

            return ResponseEntity.ok(ApiResponse.success(id, "Status updated successfully"));
            
        } catch (Exception e) {
            logger.error("Error updating status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update status: " + e.getMessage()));
        }
    }

    /**
     * Get assets by dealer ID
     */
    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<ApiResponse<String>> getAssetsByDealer(@PathVariable String dealerId) {
        try {
            logger.info("Getting assets for dealer: {}", dealerId);
            
            byte[] result = fabricService.evaluateTransaction("queryAssetsByDealer", dealerId);
            String assetsData = new String(result);
            
            return ResponseEntity.ok(ApiResponse.success(assetsData, "Assets retrieved successfully"));
            
        } catch (Exception e) {
            logger.error("Error retrieving assets by dealer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve assets: " + e.getMessage()));
        }
    }

    /**
     * Get assets by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<String>> getAssetsByStatus(@PathVariable String status) {
        try {
            logger.info("Getting assets with status: {}", status);
            
            byte[] result = fabricService.evaluateTransaction("queryAssetsByStatus", status);
            String assetsData = new String(result);
            
            return ResponseEntity.ok(ApiResponse.success(assetsData, "Assets retrieved successfully"));
            
        } catch (Exception e) {
            logger.error("Error retrieving assets by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve assets: " + e.getMessage()));
        }
    }

    /**
     * Get assets by MSISDN
     */
    @GetMapping("/msisdn/{msisdn}")
    public ResponseEntity<ApiResponse<String>> getAssetsByMsisdn(@PathVariable String msisdn) {
        try {
            logger.info("Getting assets for MSISDN: {}", msisdn);
            
            byte[] result = fabricService.evaluateTransaction("queryAssetsByMsisdn", msisdn);
            String assetsData = new String(result);
            
            return ResponseEntity.ok(ApiResponse.success(assetsData, "Assets retrieved successfully"));
            
        } catch (Exception e) {
            logger.error("Error retrieving assets by MSISDN", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve assets: " + e.getMessage()));
        }
    }

    /**
     * Get transaction history for an asset
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<String>> getTransactionHistory(@PathVariable String id) {
        try {
            logger.info("Getting transaction history for asset: {}", id);
            
            byte[] result = fabricService.evaluateTransaction("getTransactionHistory", id);
            String historyData = new String(result);
            
            return ResponseEntity.ok(ApiResponse.success(historyData, "Transaction history retrieved successfully"));
            
        } catch (Exception e) {
            logger.error("Error retrieving transaction history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve transaction history: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        if (fabricService.isReady()) {
            return ResponseEntity.ok(ApiResponse.success("Service is healthy", "Service is running"));
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("Service is not ready", "SERVICE_UNAVAILABLE"));
        }
    }

    /**
     * Generate a unique asset ID based on dealer ID and MSISDN
     */
    private String generateAssetId(String dealerId, String msisdn) {
        return "ASSET_" + dealerId + "_" + msisdn + "_" + System.currentTimeMillis();
    }
}
