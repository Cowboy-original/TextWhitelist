# 💬 TextWhitelist

Tired of managing a cumbersome `whitelist.json` file? **TextWhitelist** is a powerful, lightweight whitelist plugin designed for server administrators who prefer simple, text-file-based management. Built for wide compatibility (**1.8 - 1.21+**), it's the perfect solution for offline-mode servers or any community that needs to quickly manage player access without dealing with UUIDs.

This plugin evolves beyond a simple whitelist into a full-fledged access management tool, allowing you to control every aspect directly from in-game commands without ever needing a server restart.

---
## ✨ Features

-   **Full In-Game Management:** Control every aspect of the plugin with a comprehensive command suite.
-   **Master On/Off Switch:** Globally enable or disable the plugin's functionality on the fly with `/wltxt enable|disable`.
-   **Dual Whitelist Modes:** Switch between a standard `PLAYERS` mode and a staff-only `ADMINS` maintenance mode.
-   **Context-Aware Commands:** The plugin is smart! It provides different kick messages for different scenarios and only kicks players when it makes sense.
-   **Instant Reloading:** Use `/wltxt reload` to apply changes to your `.txt` files instantly without a server restart.
-   **Enforce on Reload:** When reloaded, the plugin automatically kicks any online players who are no longer on the active whitelist.
-   **Wide Version Compatibility:** Works on most Spigot-based servers from version 1.8 to 1.21 and newer.
-   **High-Priority Login-Blocking:** Built to coexist with other plugins like AuthMe by using the highest event priority.

---
## 🚀 Installation

1.  Download the latest `.jar` file from the [**Releases**](https://github.com/Cowboy-original/TextWhitelist/releases) page.
2.  Place the `TextWhitelist.jar` file into your server's `plugins` directory.
3.  Restart your server. The plugin will automatically generate a `TextWhitelist` folder inside your `plugins` directory.

## 💻 Commands & Usage
The base command is `/whitelistxt` (alias: `/wltxt`). Permission for all commands: `textwhitelist.admin`

| Command | Description |
| :--- | :--- |
| `/wltxt help` | Displays this list of commands. |
| `/wltxt enable\|disable` | Toggles the plugin on or off. |
| `/wltxt change <players\|admins>` | Switches the active whitelist mode. |
| `/wltxt reload`| Reloads both `.txt` files from the disk. |
| `/wltxt add <player\|admin> <name>` | Adds a player to the specified list. |
| `/wltxt remove <player\|admin> <name>` | Removes a player from the specified list. |

---
## 🛡️ Permissions
-   **`textwhitelist.admin`**: Grants access to all `/wltxt` commands. (Given to OPs by default)
