package io.github.cowboyoriginal.textwhitelist;

// This enum represents the current operating mode of the plugin.
public enum WhitelistMode {
    PLAYERS, // Normal mode: allows players from players.txt
    ADMINS   // Maintenance mode: only allows players from admins.txt
}