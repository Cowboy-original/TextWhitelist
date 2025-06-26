# TextWhitelist

Tired of managing a cumbersome `whitelist.json` file? TextWhitelist is a powerful, lightweight whitelist plugin designed for server administrators who prefer simple, text-file-based management. Built for wide compatibility (1.8 - 1.21+), it's the perfect solution for offline-mode servers or any community that needs to quickly manage player access without dealing with UUIDs.

With a full suite of in-game commands, you can toggle the whitelist, switch between a public player list and a staff-only maintenance mode, and add or remove users on the fly, all without needing a server restart.

## Features

-   **Full In-Game Management:** Control every aspect of the plugin with commands.
-   **Master Switch:** Enable or disable the entire plugin with `/wltxt enable` and `/wltxt disable`. The setting is saved in `config.yml`.
-   **Dual Whitelist Modes:**
    -   **PLAYERS Mode:** Standard operation. Allows players listed in `players.txt`.
    -   **ADMINS Mode:** Maintenance mode. Only allows staff listed in `admins.txt`.
-   **Easy Mode Switching:** Instantly switch between modes with `/wltxt change <players|admins>`.
-   **In-Game List Editing:** Add or remove players from `players.txt` or `admins.txt` directly with `/wltxt add` and `/wltxt remove`.
-   **Wide Version Compatibility:** Built against the Spigot 1.8 API to ensure functionality across a vast range of Minecraft versions.
-   **High-Priority Login-Blocking:** Uses the `AsyncPlayerPreLoginEvent` at the highest priority to ensure whitelist checks are performed before other plugins (like AuthMe) can interfere.
-   **Case-Sensitive:** Player name checks are case-sensitive for precise control.

## Installation

1.  Download the latest `.jar` file from the [**Releases**](https://github.com/Cowboy-original/TextWhitelist/releases) page.
2.  Place the `TextWhitelist-X.X.jar` file into your server's `plugins` directory.
3.  Restart your server. The plugin will generate a `TextWhitelist` folder inside your `plugins` directory, containing `config.yml`, `players.txt`, and `admins.txt`.

## Commands & Usage

The base command is `/whitelistxt` (alias: `/wltxt`). All subcommands require the `textwhitelist.admin` permission.

-   **Check Status:**
    `/wltxt`
    *Shows whether the plugin is enabled and the current active mode.*

-   **Show Help:**
    `/wltxt help`
    *Displays a list of all available commands.*

-   **Enable/Disable Plugin:**
    `/wltxt enable`
    `/wltxt disable`
    *Toggles the master switch for the plugin. This setting persists after restarts.*

-   **Change Whitelist Mode:**
    `/wltxt change <players|admins>`
    *Examples:*
    -   `/wltxt change players` (Switches to the general player list)
    -   `/wltxt change admins` (Activates maintenance mode)

-   **Add a Player to a List:**
    `/wltxt add <player|admin> <PlayerName>`
    *Examples:*
    -   `/wltxt add player Notch` (Adds Notch to `players.txt`)
    -   `/wltxt add admin Herobrine` (Adds Herobrine to `admins.txt`)

-   **Remove a Player from a List:**
    `/wltxt remove <player|admin> <PlayerName>`
    *Examples:*
    -   `/wltxt remove player Notch`
    -   `/wltxt remove admin Herobrine`

## Permissions

-   **`textwhitelist.admin`**: Grants access to all `/wltxt` commands. This permission is given to server operators (OPs) by default.
