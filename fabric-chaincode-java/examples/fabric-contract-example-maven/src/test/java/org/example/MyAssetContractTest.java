/*
 * SPDX-License-Identifier: Apache License 2.0
 */

package org.example;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class MyAssetContractTest {

    @Nested
    class AssetExists {
        @Test
        public void noProperAsset() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(new byte[] {});
            boolean result = contract.assetExists(ctx,"10001");

            assertFalse(result);
        }

        @Test
        public void assetExists() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(new byte[] {42});
            boolean result = contract.assetExists(ctx,"10001");

            assertTrue(result);
        }

        @Test
        public void noKey() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10002")).thenReturn(null);
            boolean result = contract.assetExists(ctx,"10002");

            assertFalse(result);
        }
    }

    @Nested
    class AssetCreates {
        @Test
        public void newAssetCreate() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            contract.createAsset(ctx, "10001", "DEALER001", "1234567890", "1234", "1000.00", "ACTIVE", "100.00", "DEPOSIT", "Initial deposit");

            verify(stub).putState("10001", Mockito.any(byte[].class));
        }

        @Test
        public void alreadyExists() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10002")).thenReturn(new byte[] { 42 });

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.createAsset(ctx, "10002", "DEALER001", "1234567890", "1234", "1000.00", "ACTIVE", "100.00", "DEPOSIT", "Initial deposit");
            });

            assertEquals(thrown.getMessage(), "The asset 10002 already exists");
        }
    }

    @Test
    public void assetRead() {
        MyAssetContract contract = new MyAssetContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        MyAsset asset = new MyAsset();
        asset.setDealerId("DEALER001");
        asset.setMsisdn("1234567890");
        asset.setMpin("1234");
        asset.setBalance(new BigDecimal("1000.00"));
        asset.setStatus("ACTIVE");
        asset.setTransAmount(new BigDecimal("100.00"));
        asset.setTransType("DEPOSIT");
        asset.setRemarks("Initial deposit");

        String json = asset.toJSONString();
        when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));

        MyAsset returnedAsset = contract.readAsset(ctx, "10001");
        assertEquals(returnedAsset.getDealerId(), asset.getDealerId());
        assertEquals(returnedAsset.getMsisdn(), asset.getMsisdn());
        assertEquals(returnedAsset.getBalance(), asset.getBalance());
    }

    @Nested
    class AssetUpdates {
        @Test
        public void updateExisting() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getState("10001")).thenReturn(new byte[] { 42 });

            contract.updateAsset(ctx, "10001", "DEALER001", "1234567890", "1234", "1500.00", "ACTIVE", "500.00", "DEPOSIT", "Additional deposit");

            verify(stub).putState("10001", Mockito.any(byte[].class));
        }

        @Test
        public void updateMissing() {
            MyAssetContract contract = new MyAssetContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(null);

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.updateAsset(ctx, "10001", "DEALER001", "1234567890", "1234", "1500.00", "ACTIVE", "500.00", "DEPOSIT", "Additional deposit");
            });

            assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
        }
    }

    @Test
    public void assetDelete() {
        MyAssetContract contract = new MyAssetContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getState("10001")).thenReturn(null);

        Exception thrown = assertThrows(RuntimeException.class, () -> {
            contract.deleteAsset(ctx, "10001");
        });

        assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
    }

    @Test
    public void updateBalance() {
        MyAssetContract contract = new MyAssetContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        MyAsset asset = new MyAsset();
        asset.setDealerId("DEALER001");
        asset.setMsisdn("1234567890");
        asset.setMpin("1234");
        asset.setBalance(new BigDecimal("1000.00"));
        asset.setStatus("ACTIVE");
        asset.setTransAmount(new BigDecimal("100.00"));
        asset.setTransType("DEPOSIT");
        asset.setRemarks("Initial deposit");

        String json = asset.toJSONString();
        when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));

        contract.updateBalance(ctx, "10001", "1500.00", "500.00", "DEPOSIT", "Additional deposit");

        verify(stub).putState("10001", Mockito.any(byte[].class));
    }

    @Test
    public void updateStatus() {
        MyAssetContract contract = new MyAssetContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        MyAsset asset = new MyAsset();
        asset.setDealerId("DEALER001");
        asset.setMsisdn("1234567890");
        asset.setMpin("1234");
        asset.setBalance(new BigDecimal("1000.00"));
        asset.setStatus("ACTIVE");
        asset.setTransAmount(new BigDecimal("100.00"));
        asset.setTransType("DEPOSIT");
        asset.setRemarks("Initial deposit");

        String json = asset.toJSONString();
        when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));

        contract.updateStatus(ctx, "10001", "SUSPENDED", "Account suspended due to suspicious activity");

        verify(stub).putState("10001", Mockito.any(byte[].class));
    }

    @Test
    public void getContractInfo() {
        MyAssetContract contract = new MyAssetContract();
        String info = contract.getContractInfo();
        assertTrue(info.contains("Financial Asset Management Contract"));
        assertTrue(info.contains("DEALERID"));
        assertTrue(info.contains("MSISDN"));
        assertTrue(info.contains("MPIN"));
        assertTrue(info.contains("BALANCE"));
        assertTrue(info.contains("STATUS"));
        assertTrue(info.contains("TRANSAMOUNT"));
        assertTrue(info.contains("TRANSTYPE"));
        assertTrue(info.contains("REMARKS"));
    }
}