package org.jodelleIpWhitelist.Listeners;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.jodelleIpWhitelist.WhitelistManager.WhiteListManager;
import org.slf4j.Logger;


import java.util.List;
import java.util.regex.Pattern;

public class CommandListener implements SimpleCommand {
    private final WhiteListManager whitelistManager;
    private final Logger logger;

    public CommandListener(WhiteListManager whitelistManager, Logger logger) {
        this.whitelistManager = whitelistManager;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();

        // Check if there are no arguments or the first argument is invalid
        if (args.length == 0) {
            invocation.source().sendMessage(Component.text("Usage: /jodellewhitelist <reload|addip|removeip|reloadips|showips> [IP]"));
            return;
        }

        // Handle different command cases
        switch (args[0].toLowerCase()) {
            case "reload":
                reloadWhitelist(invocation);
                break;
            case "addip":
                if (args.length < 2) {
                    invocation.source().sendMessage(Component.text("Usage: /jodellewhitelist addip <IP>"));
                    return;
                }
                addIP(invocation, args[1]);
                break;
            case "removeip":
                if (args.length < 2) {
                    invocation.source().sendMessage(Component.text("Usage: /jodellewhitelist removeip <IP>"));
                    return;
                }
                removeIP(invocation, args[1]);
                break;
            case "reloadips":
                reloadIPs(invocation);
                break;
            case "showips":
                showWhitelistedIPs(invocation);
                break;
            default:
                invocation.source().sendMessage(Component.text("Unknown command. Usage: /jodellewhitelist <reload|addip|removeip|reloadips|showips> [IP]"));
                break;
        }
    }

    // Reload the whitelist file and update allowed IPs
    private void reloadWhitelist(Invocation invocation) {
        whitelistManager.loadWhitelistedIPs();
        invocation.source().sendMessage(Component.text("Whitelist reloaded!"));
    }

    private void addIP(Invocation invocation, String ip) {
        // Validate if the provided IP is in valid IPv4 format using regex
        if (!isValidIPv4(ip)) {
            invocation.source().sendMessage(Component.text("Invalid IP format. Please provide a valid IPv4 address (e.g., 192.168.1.23)."));
            return;
        }

        // Check if the IP is already in the whitelist
        if (whitelistManager.addIP(ip)) {
            invocation.source().sendMessage(Component.text("Added IP to whitelist: " + ip));
        } else {
            invocation.source().sendMessage(Component.text("IP is already whitelisted: " + ip));
        }
    }

    // Helper method to validate IPv4 format using regex
    private boolean isValidIPv4(String ip) {
        // Regular expression for matching valid IPv4 addresses
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        return pattern.matcher(ip).matches();
    }

    // Remove an IP address from the whitelist
    private void removeIP(Invocation invocation, String ip) {
        if (whitelistManager.removeIP(ip)) {
            invocation.source().sendMessage(Component.text("Removed IP from whitelist: " + ip));
        } else {
            invocation.source().sendMessage(Component.text("IP was not found in whitelist: " + ip));
        }
    }

    // Reload the IPs (specific functionality for "reloadips")
    private void reloadIPs(Invocation invocation) {
        whitelistManager.reloadIPs(); // Add a method in your WhiteListManager to reload the IPs
        invocation.source().sendMessage(Component.text("IPs reloaded!"));
    }

    // Show all the whitelisted IPs
    private void showWhitelistedIPs(Invocation invocation) {
        // Get the list of whitelisted IPs from the whitelist manager
        List<String> allowedIPs = whitelistManager.getAllowedIPs();

        if (allowedIPs.isEmpty()) {
            invocation.source().sendMessage(Component.text("No IPs are currently whitelisted."));
        } else {
            String ipList = String.join(", ", allowedIPs);
            invocation.source().sendMessage(Component.text("Whitelisted IPs: " + ipList));
        }
    }
}
