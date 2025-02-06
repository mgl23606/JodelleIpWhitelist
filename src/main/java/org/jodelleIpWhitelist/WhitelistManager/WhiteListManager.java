package org.jodelleIpWhitelist.WhitelistManager;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WhiteListManager {
    private final Path whiteListFile;
    private final Logger logger;
    private List<String> allowedIPs;

    public WhiteListManager(Path whitelistFile, Logger logger) {
        this.whiteListFile = whitelistFile;
        this.logger = logger;
    }

    /**
     * Loads the whitelisted IPs from the whitelist.txt file.
     * If the file does not exist, it creates one with a default IP.
     */
    public void loadWhitelistedIPs() {
        try {
            // Check if the whitelist file exists; if not, create it with default content
            if (!Files.exists(whiteListFile)) {
                Files.createDirectories(whiteListFile.getParent()); // Ensure directory exists
                Files.write(whiteListFile, List.of("# Add one IP per line", "127.0.0.1")); // Default entry
                logger.info("Whitelist file created: {}", whiteListFile);
            }

            // Read the file and filter out empty lines and comments
            allowedIPs = Files.lines(whiteListFile)
                    .map(String::trim) // Remove extra spaces
                    .filter(line -> !line.isEmpty() && !line.startsWith("#")) // Ignore comments and empty lines
                    .filter(this::isValidIPv4) // Only accept valid IPv4 addresses
                    .peek(ip -> {
                        if (!isValidIPv4(ip)) {
                            logger.warn("Skipping invalid IP in whitelist: {}", ip);
                        }
                    }) // Log warning for invalid IPs (if any, even if skipped)
                    .collect(Collectors.toList());

            logger.info("Loaded {} whitelisted IP(s).", allowedIPs.size());
        } catch (IOException e) {
            logger.error("Error reading whitelist file", e);
            allowedIPs = List.of(); // Set an empty list in case of failure
        }
    }

    // Helper method to validate IPv4 format using regex
    private boolean isValidIPv4(String ip) {
        // Regular expression for matching valid IPv4 addresses
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        return pattern.matcher(ip).matches();
    }


    public List<String> getAllowedIPs() {
        return allowedIPs;
    }

    /**
     * Adds an IP to the whitelist if it's not already added.
     *
     * @param ip The IP address to add
     * @return true if IP was added, false if it's already in the whitelist
     */
    public boolean addIP(String ip) {
        if (allowedIPs.contains(ip)) {
            return false; // IP already exists in the whitelist
        }
        allowedIPs.add(ip);
        saveWhitelist(); // Save the updated whitelist to the file
        return true; // IP successfully added
    }

    /**
     * Removes an IP from the whitelist if it exists.
     *
     * @param ip The IP address to remove
     * @return true if IP was removed, false if it's not in the whitelist
     */
    public boolean removeIP(String ip) {
        if (!allowedIPs.contains(ip)) {
            return false; // IP not found in the whitelist
        }
        allowedIPs.remove(ip);
        saveWhitelist(); // Save the updated whitelist to the file
        return true; // IP successfully removed
    }

    /**
     * Saves the current list of allowed IPs back to the whitelist.txt file.
     */
    private void saveWhitelist() {
        try {
            Files.write(whiteListFile, allowedIPs); // Overwrite the file with the updated list
            logger.info("Whitelist updated.");
        } catch (IOException e) {
            logger.error("Error saving whitelist file", e);
        }
    }

    public void reloadIPs() {
        loadWhitelistedIPs();
    }
}
