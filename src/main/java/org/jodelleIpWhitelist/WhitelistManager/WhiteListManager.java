package org.jodelleIpWhitelist.WhitelistManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;

public class WhiteListManager {
    private final Logger logger;

    // Gson instance for JSON serialization and deserialization, with pretty printing
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Path to the whitelist JSON file
    private final Path whiteListFile = Paths.get("plugins/jodelleipwhitelist/whitelist.json");

    // Hashtable to store usernames and associated list of IPs
    private Hashtable<String, List<String>> usernameToIps;

    /**
     * Constructor to initialize the whitelist manager with a specific file and logger.
     *
     * @param logger Logger instance for logging messages.
     */
    public WhiteListManager(Logger logger) {
        this.logger = logger;
        usernameToIps = new Hashtable<>();
    }

    /**
     * Loads the whitelist data from the JSON file.
     *
     * If the whitelist file does not exist, it creates the file with default data.
     * It then deserializes the JSON data into a Hashtable where each key is a username,
     * and the value is a list of IP addresses associated with that username.
     */
    public void loadWhitelistedIPs() {
        try {
            // Check if the file exists; if not, create it with default data
            if (!Files.exists(whiteListFile)) {
                Files.createDirectories(whiteListFile.getParent());

                // Default data with a single user "user1" and a default IP
                Hashtable<String, List<String>> defaultData = new Hashtable<>();
                defaultData.put("user1", new ArrayList<>(List.of("127.0.0.1")));

                // Write the default data to the JSON file
                Files.writeString(whiteListFile, gson.toJson(defaultData), StandardOpenOption.CREATE);
                logger.info("Whitelist JSON created: {}", whiteListFile);
            }

            // Read and parse the JSON file into the usernameToIps map
            String json = Files.readString(whiteListFile);
            Type type = new TypeToken<Hashtable<String, List<String>>>() {}.getType();
            usernameToIps = gson.fromJson(json, type);

            // Ensure that the map is initialized even if the file is empty or malformed
            if (usernameToIps == null) {
                usernameToIps = new Hashtable<>();
            }

            logger.info("Loaded {} usernames from the whitelist JSON.", usernameToIps.size());
        } catch (IOException e) {
            logger.error("Error reading whitelist.json", e);
            usernameToIps = new Hashtable<>();
        }
    }

    /**
     * Helper method to validate if a string is a valid IPv4 address.
     *
     * @param ip IP address to validate.
     * @return true if the IP is a valid IPv4 address, false otherwise.
     */
    private boolean isValidIPv4(String ip) {
        // Regular expression for matching valid IPv4 addresses
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        return pattern.matcher(ip).matches();
    }

    /**
     * Retrieves the list of IP addresses associated with a specific username.
     *
     * @param username the username to look up.
     * @return a list of IP addresses associated with the given username.
     */
    public List<String> getIpsForUsername(String username) {
        return usernameToIps.getOrDefault(username, new ArrayList<>());
    }

    /**
     * Prints all entries in the whitelist (for debugging or testing purposes).
     */
    public void printEntries() {
        usernameToIps.forEach((username, ips) ->
                System.out.println("Username: " + username + " -> IPs: " + ips)
        );
    }

    /**
     * Checks if the specified username exists in the whitelist.
     *
     * @param username the username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean containsUser(String username) {
        return usernameToIps.containsKey(username);
    }

    /**
     * Reloads the whitelist from the JSON file.
     * This method is useful to refresh the data in case of external modifications to the file.
     */
    public void reloadIPs() {
        loadWhitelistedIPs();
    }

    /**
     * Adds an IP address to the list of IPs for a given username.
     *
     * @param username the username to add the IP for.
     * @param ip the IP address to add.
     * @return true if the IP was successfully added, false if the IP already exists for this username.
     */
    public boolean addIP(String username, String ip) {
        try {
            // Check if the IP is already associated with the username
            List<String> existingIps = usernameToIps.getOrDefault(username, new ArrayList<>());
            if (existingIps.contains(ip)) {
                return false; // IP already exists for this user
            }

            // Add the new IP to the username's list
            existingIps.add(ip);
            usernameToIps.put(username, existingIps);

            // Write the updated data back to the JSON file
            Files.writeString(
                    whiteListFile,
                    gson.toJson(usernameToIps),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return true;
        } catch (IOException e) {
            logger.error("Failed to add IP to JSON whitelist", e);
            return false;
        }
    }

    /**
     * Removes an IP address from the list of IPs associated with a username.
     *
     * If the username does not exist or the IP is not associated with the username,
     * this method returns false. If the IP is removed successfully, the method will
     * update the JSON file and return true.
     *
     * @param username the username to remove the IP for.
     * @param ip the IP address to remove.
     * @return true if the IP was successfully removed, false if the IP or username doesn't exist.
     */
    public boolean removeIP(String username, String ip) {
        // Retrieve the list of IPs for the specified username
        List<String> ips = usernameToIps.get(username);
        if (ips == null || !ips.contains(ip)) {
            return false; // IP not found for this user
        }

        // Remove the IP from the list
        ips.remove(ip);

        // If the list is empty after removal, remove the username from the map
        if (ips.isEmpty()) {
            usernameToIps.remove(username);
        } else {
            usernameToIps.put(username, ips);
        }

        // Save the updated list to the JSON file
        saveWhitelistedIPs();

        return true; // IP removed successfully
    }

    /**
     * Saves the current whitelist (usernameToIps) back to the JSON file.
     *
     * This method serializes the in-memory whitelist data to JSON and overwrites the existing file.
     */
    public void saveWhitelistedIPs() {
        try {
            Files.writeString(whiteListFile, gson.toJson(usernameToIps), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to save whitelist.json", e);
        }
    }

    /**
     * Retrieves a list of all usernames and their associated IP addresses in a formatted manner.
     *
     * This method constructs a list of strings where each string contains a username followed
     * by their associated IPs in the format "username: [ip1, ip2, ...]". The result can be used
     * for logging, displaying, or further processing.
     *
     * @return a list of formatted strings representing the whitelist.
     */
    public List<String> getAllowedIPs() {
        List<String> result = new ArrayList<>();

        // Iterate over each entry in the usernameToIps map and format it
        for (Map.Entry<String, List<String>> entry : usernameToIps.entrySet()) {
            String username = entry.getKey();
            List<String> ips = entry.getValue();
            result.add(username + ": " + ips);
        }

        return result; // Return the formatted list
    }
}
