# 💬 TextWhitelist - The Ultimate Whitelist Management Tool

Tired of managing a cumbersome `whitelist.json` file or complex whitelist plugins? **TextWhitelist** is a powerful, yet lightweight plugin designed for server administrators who prefer simple, text-file-based management combined with a full suite of in-game commands.

Built for wide compatibility (**1.8 - 1.21+**), it's the perfect solution for offline-mode servers or any community that needs to quickly and dynamically manage player access without ever needing a server restart for list updates.

---
## ✨ Core Features

-   **Full In-Game Command Suite:** Control every aspect of the plugin without ever touching a file. Add, remove, change modes, reload, and get help directly in-game.
-   **Dual Whitelist System:**
    -   **`players.txt`:** Manage your general player base.
    -   **`admins.txt`:** Manage staff who can join even during maintenance mode.
-   **Intelligent Maintenance Mode:** With `/wltxt change admins`, you can instantly lock down your server for staff-only access. The plugin will even kick online players who are not on the admin list with a proper "maintenance" message.
-   **Interactive & Paginated Player Lists:** Use `/wltxt list` to view whitelisted players in a clean, paginated format with clickable `[Remove]` and page navigation buttons directly in chat!
-   **Master On/Off Switch:** Globally enable or disable the entire plugin on the fly with `/wltxt enable|disable`. This setting is saved in your `config.yml`.
-   **Smart List Sanitization:** The plugin is smart! When you add a user to the admin list, it automatically removes them from the player list to prevent redundancies.
-   **Wide Version Compatibility:** Built to run on most Spigot-based servers from version 1.8 all the way to 1.21 and newer.
-   **Robust & Compatible:** Uses the highest event priority to ensure its rules are final and not bypassed by other plugins like AuthMe.

---
## 🚀 Installation

1.  Download the latest `.jar` file from the [**Releases**](https://github.com/Cowboy-original/TextWhitelist/releases) page.
2.  Place the `.jar` file into your server's `plugins` directory.
3.  Restart your server. The plugin will automatically generate a `TextWhitelist` folder inside your `plugins` directory, containing `config.yml`, `players.txt`, and `admins.txt`.

---
## 💻 Commands & Usage

The base command is `/whitelistxt` (alias: `/wltxt`). All commands require the `textwhitelist.admin` permission.

| Command | Description |
| :--- | :--- |
| `/wltxt` | Shows the current status (enabled/disabled) and active mode. |
| `/wltxt help` | Displays a detailed list of all available commands. |
| `/wltxt enable` | Enables all whitelist checks. |
| `/wltxt disable`| Disables all whitelist checks. |
| `/wltxt change <players\|admins>` | Switches the active mode. Kicks non-whitelisted players if needed. |
| `/wltxt reload`| Reloads `players.txt` and `admins.txt` from the disk. |
| `/wltxt add <player\|admin> <name>` | Adds a player to the specified list and file. |
| `/wltxt remove <player\|admin> <name>` | Removes a player from the specified list and file. |
| `/wltxt list [players\|admins\|all] [page]` | Displays interactive, paginated lists with remove buttons. |

---
## 🛡️ Permissions

-   **`textwhitelist.admin`**: Grants access to all `/wltxt` commands. This permission is given to server operators (OPs) by default.
