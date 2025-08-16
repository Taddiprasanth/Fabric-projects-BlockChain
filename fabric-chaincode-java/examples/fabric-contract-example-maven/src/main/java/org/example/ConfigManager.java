package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for loading personal credentials and settings
 */
public class ConfigManager {
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                isLoaded = true;
            } else {
                System.err.println("Warning: config.properties not found in classpath");
            }
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key, "");
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    public static boolean isConfigLoaded() {
        return isLoaded;
    }
    
    // Convenience methods for common properties
    public static String getUserName() {
        return getProperty("user.name", "Unknown User");
    }
    
    public static String getUserEmail() {
        return getProperty("user.email", "unknown@example.com");
    }
    
    public static String getUserOrganization() {
        return getProperty("user.organization", "Unknown Organization");
    }
    
    public static String getAppName() {
        return getProperty("app.name", "MyAssetContract");
    }
    
    public static String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }
    
    public static String getApiKey() {
        return getProperty("api.key", "");
    }
    
    public static String getApiSecret() {
        return getProperty("api.secret", "");
    }
    
    public static String getEncryptionKey() {
        return getProperty("encryption.key", "");
    }
    
    public static String getSignatureKey() {
        return getProperty("signature.key", "");
    }
    
    public static int getMaxAssetValue() {
        return getIntProperty("max.asset.value", 1000000);
    }
    
    public static int getMinAssetValue() {
        return getIntProperty("min.asset.value", 1);
    }
}
