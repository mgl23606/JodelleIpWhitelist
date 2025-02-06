# Jodelle IP Whitelist  
*A secure and easy-to-use IP whitelist plugin for Velocity proxy servers.*  

![🚀 Velocity](https://img.shields.io/badge/Proxy-Velocity-blue)  

---

## 📌 Overview  
Jodelle IP Whitelist helps you **restrict server access** to only trusted IP addresses. With simple commands, you can **add, remove, reload, and view** whitelisted IPs, ensuring only authorized users connect to your server.

### ✨ Features  
✔ **Whitelist Management** – Easily add, remove, and reload whitelisted IPs.  
✔ **Valid IPv4 Enforcement** – Ensures only properly formatted IPs (e.g., `192.168.1.23`) are accepted.  
✔ **Command-Based Control** – Manage the whitelist without editing files manually.  
✔ **Automatic IP Validation** – Skips invalid IPs and logs warnings when loading.  
✔ **Simple & Secure** – Prevents unauthorized connections to your proxy server.

---

## 📥 Installation  
1. Download the latest `.jar` file from the [Releases](https://github.com/your-repo/jodelle-ip-whitelist/releases).  
2. Place it in the `plugins` folder of your **Velocity** proxy server.  
3. Restart or reload your server.  
4. Edit the `whitelist.txt` file in the plugin folder to add allowed IPs.  

---

## 🔧 Commands  

| Command | Description |
|---------|------------|
| `/jodellewhitelist reload` | Reloads the whitelist from the file. |
| `/jodellewhitelist addip <IP>` | Adds an IP address to the whitelist. |
| `/jodellewhitelist removeip <IP>` | Removes an IP address from the whitelist. |
| `/jodellewhitelist reloadips` | Reloads the IP list from memory. |
| `/jodellewhitelist showips` | Displays all currently whitelisted IPs. |

---

## 🔒 Permissions  

| Permission | Description |
|------------|------------|
| `jodellewhitelist.reload` | Allows reloading the whitelist. |
| `jodellewhitelist.addip` | Allows adding IPs to the whitelist. |
| `jodellewhitelist.removeip` | Allows removing IPs from the whitelist. |
| `jodellewhitelist.showips` | Allows viewing the whitelist. |

---

## 🛠 Configuration  
By default, the plugin creates a `whitelist.txt` file in the plugin folder. This file should contain one IP per line.  
Example:  

When reloading the whitelist, the plugin **skips invalid IPs** and logs warnings for malformed entries.

---

## 📜 License  
This plugin is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 💬 Support & Contributions  
- Found a bug? Report it in [Issues](https://github.com/your-repo/jodelle-ip-whitelist/issues).  
- Want to contribute? Fork the repo and submit a **pull request**!  
- Need help? Join our **Discord community** (add link if available).  

---

🚀 **Built for security and simplicity. Keep your Velocity proxy safe with Jodelle IP Whitelist!** 🚀


