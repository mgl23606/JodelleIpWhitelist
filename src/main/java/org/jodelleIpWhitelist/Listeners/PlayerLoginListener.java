package org.jodelleIpWhitelist.Listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.Component;
import org.jodelleIpWhitelist.JodelleIpWhitelist;
import org.slf4j.Logger;

import java.util.List;

/**
 * PlayerLoginListener handles login events and enforces IP whitelisting.
 */
public class PlayerLoginListener {

    private final JodelleIpWhitelist plugin; // Reference to the main plugin class
    private final Logger logger; // Logger for debugging and logging events

    /**
     * Constructor for PlayerLoginListener.
     *
     * @param plugin Reference to the main plugin instance
     * @param logger Logger instance for logging events
     */
    public PlayerLoginListener(JodelleIpWhitelist plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    /**
     * Handles player login events.
     * Checks if the player's IP is in the whitelist; if not, denies access.
     *
     * @param event The LoginEvent triggered when a player attempts to connect
     */
    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        // Get the player's IP address
        String playerIP = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        logger.info("Player {} attempted to join with IP: {}", event.getPlayer().getUsername(), playerIP);

        // Retrieve the list of allowed IPs from the plugin
        List<String> allowedIPs = plugin.getWhiteListManager().getAllowedIPs();

        // Check if the player's IP is in the whitelist
        if (!allowedIPs.contains(playerIP)) {
            // Deny the connection and send a message to the player
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Your IP is not Whitelisted!")));
            logger.warn("Blocked connection from {} ({})", event.getPlayer().getUsername(), playerIP);
        }
    }
}
