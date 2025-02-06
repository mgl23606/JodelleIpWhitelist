package org.jodelleIpWhitelist.Listeners;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        CommandSource source = invocation.source();

        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /jodellewhitelist <reload|addip|removeip|reloadips|showips> [IP]"));
            return;
        }

        boolean isPlayer = source instanceof Player;
        Player player = isPlayer ? (Player) source : null;

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!hasPermission(source, "jodellewhitelist.reload")) {
                    source.sendMessage(Component.text("You do not have permission to use this command."));
                    return;
                }
                reloadWhitelist(source);
                break;
            case "addip":
                if (!hasPermission(source, "jodellewhitelist.addip")) {
                    source.sendMessage(Component.text("You do not have permission to use this command."));
                    return;
                }
                if (args.length < 2) {
                    source.sendMessage(Component.text("Usage: /jodellewhitelist addip <IP>"));
                    return;
                }
                addIP(source, args[1]);
                break;
            case "removeip":
                if (!hasPermission(source, "jodellewhitelist.removeip")) {
                    source.sendMessage(Component.text("You do not have permission to use this command.").color(NamedTextColor.RED));
                    return;
                }
                if (args.length < 2) {
                    source.sendMessage(Component.text("Usage: /jodellewhitelist removeip <IP>"));
                    return;
                }
                removeIP(source, args[1]);
                break;
            case "reloadips":
                if (!hasPermission(source, "jodellewhitelist.reloadips")) {
                    source.sendMessage(Component.text("You do not have permission to use this command.").color(NamedTextColor.RED));
                    return;
                }
                reloadIPs(source);
                break;
            case "showips":
                if (!hasPermission(source, "jodellewhitelist.showips")) {
                    source.sendMessage(Component.text("You do not have permission to use this command.").color(NamedTextColor.RED));
                    return;
                }
                showWhitelistedIPs(source);
                break;
            default:
                source.sendMessage(Component.text("Unknown command. Usage: /jodellewhitelist <reload|addip|removeip|reloadips|showips> [IP]"));
                break;
        }
    }

    private boolean hasPermission(CommandSource source, String permission) {
        return !(source instanceof Player) || ((Player) source).hasPermission(permission);
    }

    private void reloadWhitelist(CommandSource source) {
        whitelistManager.loadWhitelistedIPs();
        source.sendMessage(Component.text("Whitelist reloaded!"));
    }

    private void addIP(CommandSource source, String ip) {
        if (!isValidIPv4(ip)) {
            source.sendMessage(Component.text("Invalid IP format. Please provide a valid IPv4 address (e.g., 192.168.1.23)."));
            return;
        }

        if (whitelistManager.addIP(ip)) {
            source.sendMessage(Component.text("Added IP to whitelist: " + ip));
        } else {
            source.sendMessage(Component.text("IP is already whitelisted: " + ip));
        }
    }

    private boolean isValidIPv4(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return Pattern.compile(ipv4Pattern).matcher(ip).matches();
    }

    private void removeIP(CommandSource source, String ip) {
        if (whitelistManager.removeIP(ip)) {
            source.sendMessage(Component.text("Removed IP from whitelist: " + ip));
        } else {
            source.sendMessage(Component.text("IP was not found in whitelist: " + ip));
        }
    }

    private void reloadIPs(CommandSource source) {
        whitelistManager.reloadIPs();
        source.sendMessage(Component.text("IPs reloaded!"));
    }

    private void showWhitelistedIPs(CommandSource source) {
        List<String> allowedIPs = whitelistManager.getAllowedIPs();
        if (allowedIPs.isEmpty()) {
            source.sendMessage(Component.text("No IPs are currently whitelisted."));
        } else {
            source.sendMessage(Component.text("Whitelisted IPs: " + String.join(", ", allowedIPs)));
        }
    }
}
