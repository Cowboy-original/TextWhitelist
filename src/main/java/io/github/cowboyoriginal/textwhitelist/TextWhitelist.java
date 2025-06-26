package io.github.cowboyoriginal.textwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

// The main class of the plugin. Note that it does not need to "implements CommandExecutor"
// because the JavaPlugin class already handles it. This was the source of our previous errors.
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
        sender.sendMessage(ChatColor.GOLD + "--- TextWhitelist v1.6 Help ---");
        sender.sendMessage(ChatColor.AQUA + "/wltxt" + ChatColor.WHITE + " - Shows the current status of the plugin.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt help" + ChatColor.WHITE + " - Shows this help message.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt enable" + ChatColor.WHITE + " - Enables the whitelist check.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt disable" + ChatColor.WHITE + " - Disables the whitelist check.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt change <players|admins>" + ChatColor.WHITE + " - Switches the active mode.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt add <player|admin> <name>" + ChatColor.WHITE + " - Adds a player to the specified list.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt remove <player|admin> <name>" + ChatColor.WHITE + " - Removes a player from the specified list.");
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

            case "enable":
                isPluginEnabled = true;
                getConfig().set("whitelist-enabled", true);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Whitelist plugin has been enabled.");
                return true;

            case "disable":
                isPluginEnabled = false;
                getConfig().set("whitelist-enabled", false);
                saveConfig();
                sender.sendMessage(ChatColor.RED + "Whitelist plugin has been disabled.");
                return true;

            case "change":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wltxt change <players|admins>");
                    return true;
                }
                String targetModeStr = args[1].toLowerCase();
                if (targetModeStr.equals("players")) {
                    if (currentMode == WhitelistMode.PLAYERS) {
                        sender.sendMessage(ChatColor.YELLOW + "Server is already in PLAYERS mode.");
                    } else {
                        currentMode = WhitelistMode.PLAYERS;
                        sender.sendMessage(ChatColor.GREEN + "Whitelist mode changed to PLAYERS.");
                    }
                } else if (targetModeStr.equals("admins")) {
                    if (currentMode == WhitelistMode.ADMINS) {
                        sender.sendMessage(ChatColor.YELLOW + "Server is already in ADMINS mode.");
                    } else {
                        currentMode = WhitelistMode.ADMINS;
                        sender.sendMessage(ChatColor.GREEN + "Whitelist mode changed to ADMINS (MAINTENANCE).");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown mode. Use 'players' or 'admins'.");
                }
                return true;

            case "add":
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wltxt " + subCommand + " <player|admin> <name>");
                    return true;
                }
                String listType = args[1].toLowerCase();
                String playerName = args[2];
                WhitelistMode targetListMode = listType.equals("player") ? WhitelistMode.PLAYERS : WhitelistMode.ADMINS;

                if (!listType.equals("player") && !listType.equals("admin")) {
                     sender.sendMessage(ChatColor.RED + "Invalid list type. Use 'player' or 'admin'.");
                     return true;
                }

                boolean success = subCommand.equals("add") ? 
                    loginListener.addPlayer(playerName, targetListMode) : 
                    loginListener.removePlayer(playerName, targetListMode);
                
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + playerName + " has been " + (subCommand.equals("add") ? "added to" : "removed from") + " the " + listType + " list.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + playerName + (subCommand.equals("add") ? " is already on" : " was not found on") + " the " + listType + " list.");
                }
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use '/wltxt help' for a list of commands.");
                return true;
        }
    }
}