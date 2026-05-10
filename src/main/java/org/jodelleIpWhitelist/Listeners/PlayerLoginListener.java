package org.jodelleIpWhitelist.Listeners;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jodelleIpWhitelist.JodelleIpWhitelist;
import org.slf4j.Logger;

import java.util.List;

public class PlayerLoginListener {

    private final JodelleIpWhitelist plugin;
    private final Logger logger;

    public PlayerLoginListener(JodelleIpWhitelist plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerIP = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        String playerUsername = event.getPlayer().getUsername();

        // 1. Check if the user is even in the system
        if (!plugin.getWhiteListManager().containsUser(playerUsername)) {
            deny(event, playerUsername, playerIP, "USER_NOT_FOUND", "Your username is not Whitelisted!");
            return;
        }

        List<String> ipsForUsername = plugin.getWhiteListManager().getIpsForUsername(playerUsername);

        // 2. Check if they have any IPs assigned at all
        if (ipsForUsername.isEmpty()) {
            deny(event, playerUsername, playerIP, "NO_IP_ASSIGNED", "Your IP is not Whitelisted! Head over to discord and !updateip");
            return;
        }

        // 3. Check if their current IP matches their whitelisted ones
        if (!ipsForUsername.contains(playerIP)) {
            deny(event, playerUsername, playerIP, "IP_MISMATCH", "Your IP is not Whitelisted! Head over to discord and !updateip");
            return;
        }

        // 4. If we got here, they are good to go!
        // We log the success to the DB.
        plugin.getDatabaseManager().logAttempt(playerUsername, playerIP, "ALLOWED", "Successful login");
        logger.info("Player {} logged in successfully from {}", playerUsername, playerIP);
    }

    /**
     * Just a handy helper to handle the 'Access Denied' logic in one place.
     * It sends the message to the player, logs it to the console, and saves it to SQLite.
     */
    private void deny(LoginEvent event, String username, String ip, String status, String message) {
        // Kick them with a red message so it looks official
        event.setResult(ResultedEvent.ComponentResult.denied(Component.text(message, NamedTextColor.RED)));

        // Log to console for the admins
        logger.warn("Access Denied for {} ({}): {}", username, ip, status);

        // Save the attempt to our new database
        plugin.getDatabaseManager().logAttempt(username, ip, "DENIED", status);
    }
}