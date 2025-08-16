package org.example.service;

import org.hyperledger.fabric.gateway.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Service class for interacting with Hyperledger Fabric network
 */
@Service
public class FabricService {

    private static final Logger logger = LoggerFactory.getLogger(FabricService.class);

    private Gateway gateway;
    private Network network;
    private Contract contract;

    @PostConstruct
    public void initialize() {
        try {
            // Load connection profile
            Path networkConfigPath = Paths.get("connection-org1.yaml");
            
            // Load wallet
            Path walletPath = Paths.get("wallet");
            Wallet wallet = Wallets.newFileSystemWallet(walletPath);
            
            // Load identity
            Identity identity = wallet.get("appUser");
            if (identity == null) {
                throw new RuntimeException("Identity 'appUser' not found in wallet");
            }

            // Create gateway connection
            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, "appUser")
                    .networkConfig(networkConfigPath)
                    .discovery(true);

            gateway = builder.connect();
            network = gateway.getNetwork("mychannel");
            contract = network.getContract("financial-asset");

            logger.info("Successfully connected to Hyperledger Fabric network");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Fabric connection", e);
            throw new RuntimeException("Fabric initialization failed", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (gateway != null) {
            try {
                gateway.close();
                logger.info("Fabric gateway connection closed");
            } catch (Exception e) {
                logger.error("Error closing Fabric gateway", e);
            }
        }
    }

    /**
     * Submit a transaction to the blockchain
     */
    public byte[] submitTransaction(String function, String... args) throws Exception {
        if (contract == null) {
            throw new RuntimeException("Contract not initialized");
        }

        try {
            byte[] result = contract.submitTransaction(function, args);
            logger.info("Transaction submitted successfully: {} with args: {}", function, String.join(", ", args));
            return result;
        } catch (Exception e) {
            logger.error("Transaction submission failed: {} with args: {}", function, String.join(", ", args), e);
            throw e;
        }
    }

    /**
     * Evaluate a transaction (read-only)
     */
    public byte[] evaluateTransaction(String function, String... args) throws Exception {
        if (contract == null) {
            throw new RuntimeException("Contract not initialized");
        }

        try {
            byte[] result = contract.evaluateTransaction(function, args);
            logger.info("Transaction evaluated successfully: {} with args: {}", function, String.join(", ", args));
            return result;
        } catch (Exception e) {
            logger.error("Transaction evaluation failed: {} with args: {}", function, String.join(", ", args), e);
            throw e;
        }
    }

    /**
     * Check if the service is ready
     */
    public boolean isReady() {
        return contract != null && gateway != null && network != null;
    }

    /**
     * Get network information
     */
    public String getNetworkInfo() {
        if (network != null) {
            return "Connected to network: " + network.getName();
        }
        return "Not connected to any network";
    }
}
