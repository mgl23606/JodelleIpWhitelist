package org.jodelleIpWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import jdk.jpackage.internal.Log;
import org.jodelleIpWhitelist.Listeners.PlayerLoginListener;
import org.jodelleIpWhitelist.WhitelistManager.WhiteListManager;
import org.slf4j.Logger;

import java.nio.file.*;
import java.util.List;

/**
 * JodelleIpWhitelist is a Velocity plugin that restricts proxy access
 * to only whitelisted IPs.
 */
@Plugin(id = "jodelleipwhitelist", name = "JodelleIpWhitelist", version = "25")
public class JodelleIpWhitelist {

    @Inject
    private Logger logger; // Logger for debugging and informational messages

    private List<String> allowedIPs; // List of whitelisted IPs
    private final Path whiteListFile; // Path to the whitelist.txt file
    private final ProxyServer proxy; // Reference to the ProxyServer instance
    private final WhiteListManager whiteListManager; //reference to the WhiteListManager instance

    /**
     * Constructor initializes the plugin and sets up the whitelist file path.
     *
     * @param proxy The Velocity ProxyServer instance
     * @param dataDirectory The plugin's data directory where whitelist.txt will be stored
     */
    @Inject
    public JodelleIpWhitelist(ProxyServer proxy, @DataDirectory Path dataDirectory, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        // Specify the file that will contain the whitelisted IPs
        this.whiteListFile = dataDirectory.resolve("whitelist.txt");

        this.whiteListManager = new WhiteListManager(whiteListFile, logger);
    }

    /**
     * Called when the proxy initializes.
     * Loads the whitelisted IPs and registers the login event listener.
     *
     * @param event The ProxyInitializeEvent
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        whiteListManager.loadWhitelistedIPs(); // Load the whitelisted IPs from file

        // Register the login event listener
        proxy.getEventManager().register(this, new PlayerLoginListener(this, logger));

        logger.info("Plugin Loaded");
    }

    /**
     * Gets the list of allowed IPs.
     *
     * @return A list of whitelisted IP addresses
     */
    public List<String> getAllowedIPs() {
        return allowedIPs;
    }

    public WhiteListManager getWhiteListManager() {
        return whiteListManager;
    }
}
