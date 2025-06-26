package io.github.cowboyoriginal.textwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TextWhitelist extends JavaPlugin {

    private LoginListener loginListener;
    private WhitelistMode currentMode;
    private boolean isPluginEnabled;

    @Override
    public void onEnable() {
        getLogger().info("Enabling TextWhitelist...");
        
        loadConfiguration();

        this.loginListener = new LoginListener(this);
        getServer().getPluginManager().registerEvents(loginListener, this);
        getCommand("whitelistxt").setExecutor(this);
        
        getLogger().info("TextWhitelist has been enabled successfully! Whitelist active: " + isPluginEnabled + ", Mode: " + currentMode);
    }

    @Override
    public void onDisable() {
        getLogger().info("TextWhitelist has been disabled.");
    }

    public WhitelistMode getCurrentMode() { return currentMode; }
    public boolean isWhitelistEnabled() { return isPluginEnabled; }
    
    private void loadConfiguration() {
        saveDefaultConfig();
        reloadConfig();
        this.isPluginEnabled = getConfig().getBoolean("whitelist-enabled", true);
        this.currentMode = WhitelistMode.PLAYERS; 
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- TextWhitelist Help ---");
        sender.sendMessage(ChatColor.AQUA + "/wltxt" + ChatColor.WHITE + " - Shows the current status.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt help" + ChatColor.WHITE + " - Shows this help message.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt reload" + ChatColor.WHITE + " - Reloads players.txt and admins.txt.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt enable|disable" + ChatColor.WHITE + " - Toggles the plugin on or off.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt change <players|admins>" + ChatColor.WHITE + " - Switches the active mode.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt add <player|admin> <name>" + ChatColor.WHITE + " - Adds a player.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt remove <player|admin> <name>" + ChatColor.WHITE + " - Removes a player.");
        sender.sendMessage(ChatColor.GOLD + "---------------------------");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("textwhitelist.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Whitelist active: " + ChatColor.GOLD + isPluginEnabled);
            sender.sendMessage(ChatColor.YELLOW + "Current whitelist mode: " + ChatColor.GOLD + currentMode);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                sendHelpMessage(sender);
                return true;

            case "reload":
                loginListener.reloadLists();
                sender.sendMessage(ChatColor.GREEN + "Whitelist files (players.txt and admins.txt) have been reloaded from disk.");
                return true;

            case "enable":
                isPluginEnabled = true;
                getConfig().set("whitelist-enabled", true);
                saveConfig();
                loginListener.reloadLists();
                sender.sendMessage(ChatColor.GREEN + "Whitelist plugin has been enabled.");
                return true;

            case "disable":
                isPluginEnabled = false;
                getConfig().set("whitelist-enabled", false);
                saveConfig();
                sender.sendMessage(ChatColor.RED + "Whitelist plugin has been disabled.");
                return true;

            case "change":
                if (args.length < 2) { /* ... */ return true; }
                String targetModeStr = args[1].toLowerCase();
                if (targetModeStr.equals("players")) {
                    if (currentMode == WhitelistMode.PLAYERS) { sender.sendMessage(ChatColor.YELLOW + "Server is already in PLAYERS mode."); } 
                    else { currentMode = WhitelistMode.PLAYERS; sender.sendMessage(ChatColor.GREEN + "Whitelist mode changed to PLAYERS."); }
                } else if (targetModeStr.equals("admins")) {
                    if (currentMode == WhitelistMode.ADMINS) { sender.sendMessage(ChatColor.YELLOW + "Server is already in ADMINS mode."); } 
                    else { currentMode = WhitelistMode.ADMINS; sender.sendMessage(ChatColor.GREEN + "Whitelist mode changed to ADMINS (MAINTENANCE)."); }
                } else { sender.sendMessage(ChatColor.RED + "Unknown mode. Use 'players' or 'admins'."); }
                loginListener.reloadLists();
                return true;

            case "add":
            case "remove":
                if (args.length < 3) { /* ... */ return true; }
                String listType = args[1].toLowerCase();
                String playerName = args[2];
                WhitelistMode targetListMode = listType.equals("player") ? WhitelistMode.PLAYERS : WhitelistMode.ADMINS;

                if (!listType.equals("player") && !listType.equals("admin")) { /* ... */ return true; }

                boolean success = subCommand.equals("add") ? 
                    loginListener.addPlayer(playerName, targetListMode) : 
                    loginListener.removePlayer(playerName, targetListMode);
                
                if (success) { sender.sendMessage(ChatColor.GREEN + playerName + " has been " + (subCommand.equals("add") ? "added to" : "removed from") + " the " + listType + " list."); } 
                else { sender.sendMessage(ChatColor.YELLOW + playerName + (subCommand.equals("add") ? " is already on" : " was not found on") + " the " + listType + " list."); }
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use '/wltxt help' for a list of commands.");
                return true;
        }
    }
}