package io.github.cowboyoriginal.textwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginListener implements Listener {

    private final TextWhitelist plugin;
    private final Set<String> playerWhitelist = new HashSet<>();
    private final Set<String> adminWhitelist = new HashSet<>();

    public LoginListener(TextWhitelist plugin) {
        this.plugin = plugin;
        loadListFromFile(WhitelistMode.PLAYERS);
        loadListFromFile(WhitelistMode.ADMINS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isWhitelistEnabled()) {
            return; // If disabled in config, do nothing.
        }

        String playerName = event.getName();
        WhitelistMode currentMode = plugin.getCurrentMode();

        if (currentMode == WhitelistMode.PLAYERS) {
            if (!playerWhitelist.contains(playerName)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        ChatColor.RED + "You are not whitelisted on this server.");
            }
        } else { // If mode is ADMINS
            if (!adminWhitelist.contains(playerName)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        ChatColor.GOLD + "The server is currently in maintenance mode.");
            }
        }
    }

    public boolean addPlayer(String playerName, WhitelistMode mode) {
        Set<String> list = (mode == WhitelistMode.PLAYERS) ? playerWhitelist : adminWhitelist;
        if (list.add(playerName)) {
            updateFileFromList(mode);
            return true;
        }
        return false;
    }

    public boolean removePlayer(String playerName, WhitelistMode mode) {
        Set<String> list = (mode == WhitelistMode.PLAYERS) ? playerWhitelist : adminWhitelist;
        if (list.remove(playerName)) {
            updateFileFromList(mode);
            return true;
        }
        return false;
    }

    public void reloadLists() {
        loadListFromFile(WhitelistMode.PLAYERS);
        loadListFromFile(WhitelistMode.ADMINS);
    }
    
    private File getFileForMode(WhitelistMode mode) {
        String fileName = (mode == WhitelistMode.PLAYERS) ? "players.txt" : "admins.txt";
        return new File(plugin.getDataFolder(), fileName);
    }

    private void loadListFromFile(WhitelistMode mode) {
        File file = getFileForMode(mode);
        Set<String> list = (mode == WhitelistMode.PLAYERS) ? playerWhitelist : adminWhitelist;

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            list.clear();
            list.addAll(Files.lines(file.toPath())
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toSet())); // Case-sensitive
            plugin.getLogger().info("Loaded " + list.size() + " names from " + file.getName());
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load " + file.getName() + "!");
            e.printStackTrace();
        }
    }

    private void updateFileFromList(WhitelistMode mode) {
        File file = getFileForMode(mode);
        Set<String> list = (mode == WhitelistMode.PLAYERS) ? playerWhitelist : adminWhitelist;

        try {
            Files.write(file.toPath(), list, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write to " + file.getName() + "!");
            e.printStackTrace();
        }
    }
}