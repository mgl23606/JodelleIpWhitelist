Jodelle IP Whitelist
Restrict server access to only whitelisted IPs with simple management commands.

Overview:
Jodelle IP Whitelist is a powerful and easy-to-use plugin for Velocity proxy servers that allows server administrators to manage which IP addresses can access their network. With a robust command system, you can reload the whitelist, add or remove IPs, and even display the current list of whitelisted IPs. This plugin ensures only trusted IPs can access your proxy, adding an extra layer of security to your setup.

Key Features:

Whitelist Management:
Easily add, remove, or reload IPs in your whitelist with simple commands.

IP Validation:
The plugin ensures that only valid IPv4 addresses (e.g., 192.168.1.23) are accepted for addition to the whitelist, preventing erroneous entries.

Flexible Command System:

/jodellewhitelist reload – Reload the whitelist from the file.
/jodellewhitelist addip <IP> – Add an IP address to the whitelist.
/jodellewhitelist removeip <IP> – Remove an IP address from the whitelist.
/jodellewhitelist reloadips – Reload the IP list.
/jodellewhitelist showips – View all currently whitelisted IPs.
IP Filtering:
During the whitelist loading process, invalid or malformed IP addresses are automatically skipped, and a warning is logged for each invalid entry.

User-Friendly:
Simple usage messages and helpful feedback ensure that server administrators are always informed about the status of the whitelist.

Why Use This Plugin?

Security:
By restricting server access to only whitelisted IPs, you add an essential layer of protection against unauthorized access.

Ease of Use:
The plugin’s intuitive command system makes managing your whitelist a breeze, even for beginners.

Flexibility:
Whether you need to quickly reload your whitelist, add a new IP, or see which addresses are currently allowed, this plugin offers everything you need.

Installation:

Download the plugin .jar file.
Place it in the plugins folder of your Velocity proxy server.
Restart or reload your proxy server to enable the plugin.
Modify the whitelist.txt file to configure your initial list of allowed IPs.
Commands:

/jodellewhitelist reload – Reload the whitelist from the file.
/jodellewhitelist addip <IP> – Add an IP address to the whitelist.
/jodellewhitelist removeip <IP> – Remove an IP address from the whitelist.
/jodellewhitelist reloadips – Reload the IP list.
/jodellewhitelist showips – Display all whitelisted IPs.
Permissions:

jodellewhitelist.reload – Allows users to reload the whitelist.
jodellewhitelist.addip – Allows users to add IPs to the whitelist.
jodellewhitelist.removeip – Allows users to remove IPs from the whitelist.
jodellewhitelist.showips – Allows users to view the list of whitelisted IPs.
Author:
Jodelle
