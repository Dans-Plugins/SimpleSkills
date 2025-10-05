package dansplugins.simpleskills.utils;

import org.bukkit.Bukkit;

/**
 * Version checking utility to replace Ponder's NMSAssistant
 * @author Daniel Stephenson
 */
public class VersionChecker {
    
    private final String nmsVersion;
    private final int majorVersion;
    
    public VersionChecker() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        this.nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
        this.majorVersion = extractMajorVersion(nmsVersion);
    }
    
    /**
     * Extract the major version number from NMS version string
     * @param versionString The NMS version string (e.g., "v1_18_R1")
     * @return The major version number (e.g., 18)
     */
    private int extractMajorVersion(String versionString) {
        try {
            // Extract version from format like "v1_18_R1"
            String[] parts = versionString.split("_");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse NMS version: " + versionString);
        }
        return 0;
    }
    
    /**
     * Check if the server version is greater than the specified version
     * @param version The version to compare against
     * @return true if current version is greater, false otherwise
     */
    public boolean isVersionGreaterThan(int version) {
        return majorVersion > version;
    }
    
    /**
     * Get the NMS version string
     * @return The NMS version string
     */
    public String getNMSVersion() {
        return nmsVersion;
    }
    
    /**
     * Get the major version number
     * @return The major version number
     */
    public int getMajorVersion() {
        return majorVersion;
    }
}
