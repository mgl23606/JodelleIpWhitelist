package org.jodelleIpWhitelist.WhitelistManager;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
        // Clear the list of Ip's everytime we reload the ip list
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
                    .collect(Collectors.toList());

            logger.info("Loaded {} whitelisted IP(s).", allowedIPs.size());
        } catch (IOException e) {
            logger.error("Error reading whitelist file", e);
            allowedIPs = List.of(); // Set an empty list in case of failure
        }
    }

    public List<String> getAllowedIPs() {
        return allowedIPs;
    }
}
