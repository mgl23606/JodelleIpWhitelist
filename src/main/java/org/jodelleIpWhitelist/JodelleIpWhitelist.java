package org.jodelleIpWhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Plugin(id = "jodelleipwhitelist", name = "JodelleIpWhitelist", version = "25")
public class JodelleIpWhitelist {

    @Inject
    private Logger logger;

    private List<String> allowedIPs;
    private final Path whitelistFile;

    @Inject
    public JodelleIpWhitelist(@DataDirectory Path dataDirectory) {
        this.whitelistFile = dataDirectory.resolve("whitelist.txt");

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadWhitelistedIPs();
        logger.info("Plugin Loaded");
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerIP = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        logger.info("Player {} attempted to join with IP: {}", event.getPlayer().getUsername(), playerIP);

        if (!allowedIPs.contains(playerIP)) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Your IP is not Whitelisted!")));
            logger.warn("Blocked connection from {} ({})", event.getPlayer().getUsername(), playerIP);
        }
    }

    private void loadWhitelistedIPs() {
        try {
            if (!Files.exists(whitelistFile)) {
                Files.createDirectories(whitelistFile.getParent());
                Files.write(whitelistFile, List.of("# Add one IP per line", "127.0.0.1"));
                logger.info("Whitelist file created: {}", whitelistFile);
            }

            allowedIPs = Files.lines(whitelistFile)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toList());

            logger.info("Loaded {} whitelisted IP(s).", allowedIPs.size());
        } catch (IOException e) {
            logger.error("Error reading whitelist file", e);
            allowedIPs = List.of();
        }
    }
}
