package io.github.cowboyoriginal.textwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

@SuppressWarnings("unused")
public class LoginListener implements Listener {

    private final TextWhitelist plugin;
    private final Set<String> playerWhitelist = new HashSet<>();
    private final Set<String> adminWhitelist = new HashSet<>();

    public LoginListener(TextWhitelist plugin) {
        this.plugin = plugin;
        reloadLists();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isWhitelistEnabled()) {
            return;
        }
        String playerName = event.getName();

        if (adminWhitelist.contains(playerName)) {
            return; // Admins can always join.
        }
        
        if (plugin.getCurrentMode() == WhitelistMode.PLAYERS) {
            if (!playerWhitelist.contains(playerName)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        ChatColor.RED + "You are not on the list for this server.");
            }
        } else { // ADMINS mode
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    ChatColor.GOLD + "The server is currently in maintenance mode.");
        }
    }
    
    public Set<String> getPlayerWhitelist() { return playerWhitelist; }
    public Set<String> getAdminWhitelist() { return adminWhitelist; }

    public boolean addPlayer(String playerName, WhitelistMode mode) {
        if (mode == WhitelistMode.PLAYERS) {
            if (adminWhitelist.remove(playerName)) {
                updateFileFromList(WhitelistMode.ADMINS);
            }
            if (playerWhitelist.add(playerName)) {
                updateFileFromList(WhitelistMode.PLAYERS);
                return true;
            }
        } else { // ADMINS mode
            if (playerWhitelist.remove(playerName)) {
                updateFileFromList(WhitelistMode.PLAYERS);
            }
            if (adminWhitelist.add(playerName)) {
                updateFileFromList(WhitelistMode.ADMINS);
                return true;
            }
        }
        return false;
    }

    public boolean removePlayer(String playerName, WhitelistMode mode) {
        Set<String> list = (mode == WhitelistMode.PLAYERS) ? playerWhitelist : adminWhitelist;
        if (list.remove(playerName)) {
            updateFileFromList(mode);
            checkOnlinePlayers();
            return true;
        }
        return false;
    }

    public void reloadLists() {
        plugin.getLogger().info("Reloading all whitelist files from disk...");
        loadListFromFile(WhitelistMode.PLAYERS);
        loadListFromFile(WhitelistMode.ADMINS);
        checkOnlinePlayers();
    }
    
    public void checkOnlinePlayers() {
        if (!plugin.isWhitelistEnabled()) {
            plugin.getLogger().info("Plugin is disabled, skipping online player check.");
            return;
        }
        
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            String playerName = player.getName();
            boolean isAllowed = adminWhitelist.contains(playerName) || 
                                (plugin.getCurrentMode() == WhitelistMode.PLAYERS && playerWhitelist.contains(playerName));

            if (!isAllowed) {
                String kickMessage = (plugin.getCurrentMode() == WhitelistMode.ADMINS) ? 
                    ChatColor.GOLD + "The server is now in maintenance mode." : 
                    ChatColor.YELLOW + "You are no longer on the active whitelist.";
                player.kickPlayer(kickMessage);
            }
        });
    }
    
    private File getFileForMode(WhitelistMode mode) {
        return new File(plugin.getDataFolder(), (mode == WhitelistMode.PLAYERS) ? "players.txt" : "admins.txt");
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
            list.addAll(Files.lines(file.toPath()).map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toSet()));
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