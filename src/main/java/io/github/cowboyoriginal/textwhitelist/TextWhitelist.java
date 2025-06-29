package io.github.cowboyoriginal.textwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

// The main class of the plugin. It does not need to "implements CommandExecutor"
// because the JavaPlugin class already handles it.
public class TextWhitelist extends JavaPlugin {

    private LoginListener loginListener;
    private WhitelistMode currentMode;
    private boolean isPluginEnabled;

    @Override
    public void onEnable() {
        getLogger().info("Enabling TextWhitelist...");
        
        // Load configuration from config.yml on startup
        loadConfiguration();

        // Initialize and register listener and commands
        this.loginListener = new LoginListener(this);
        getServer().getPluginManager().registerEvents(loginListener, this);
        getCommand("whitelistxt").setExecutor(this);
        
        getLogger().info("TextWhitelist has been enabled successfully! Whitelist active: " + isPluginEnabled + ", Mode: " + currentMode);
    }

    @Override
    public void onDisable() {
        getLogger().info("TextWhitelist has been disabled.");
    }

    // Public getters for other classes to access the plugin's current state.
    public WhitelistMode getCurrentMode() { return currentMode; }
    public boolean isWhitelistEnabled() { return isPluginEnabled; }
    
    // Loads the configuration from disk or creates a default one.
    private void loadConfiguration() {
        saveDefaultConfig(); // Creates config.yml from resources if it doesn't exist.
        reloadConfig();      // Loads the config from disk into memory.
        
        // Read values from config, with a default value of 'true' if not found.
        this.isPluginEnabled = getConfig().getBoolean("whitelist-enabled", true);
        this.currentMode = WhitelistMode.PLAYERS; // Default mode on startup.
    }

    // Sends the formatted help message to a command sender.
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- TextWhitelist Help ---");
        sender.sendMessage(ChatColor.AQUA + "/wltxt" + ChatColor.WHITE + " - Shows the current status of the plugin.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt help" + ChatColor.WHITE + " - Shows this help message.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt reload" + ChatColor.WHITE + " - Reloads players.txt and admins.txt.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt enable|disable" + ChatColor.WHITE + " - Toggles the plugin on or off.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt change <players|admins>" + ChatColor.WHITE + " - Switches the active mode.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt add <player|admin> <name>" + ChatColor.WHITE + " - Adds a player to the specified list.");
        sender.sendMessage(ChatColor.AQUA + "/wltxt remove <player|admin> <name>" + ChatColor.WHITE + " - Removes a player from the specified list.");
        sender.sendMessage(ChatColor.GOLD + "---------------------------");
    }

    // The main command handler for the plugin.
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
                        loginListener.checkOnlinePlayers();
                    }
                } else if (targetModeStr.equals("admins")) {
                    if (currentMode == WhitelistMode.ADMINS) {
                        sender.sendMessage(ChatColor.YELLOW + "Server is already in ADMINS mode.");
                    } else {
                        currentMode = WhitelistMode.ADMINS;
                        sender.sendMessage(ChatColor.GREEN + "Whitelist mode changed to ADMINS (MAINTENANCE).");
                        loginListener.checkOnlinePlayers();
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

            case "list":
                handleListCommand(sender, args);
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use '/wltxt help' for a list of commands.");
                return true;
        }
    }
    
    private void handleListCommand(CommandSender sender, String[] args) {
        String listType = "current";
        int page = 1;

        if (args.length > 1) {
            listType = args[1].toLowerCase();
            if (args.length > 2) {
                try { page = Integer.parseInt(args[2]); } catch (NumberFormatException e) { page = 1; }
            }
        }
        
        List<String> displayList = new ArrayList<>();
        String title;

        Set<String> players = loginListener.getPlayerWhitelist();
        Set<String> admins = loginListener.getAdminWhitelist();

        switch (listType) {
            case "players":
                title = "Players Whitelist";
                displayList.addAll(players);
                break;
            case "admins":
                title = "Admins Whitelist";
                displayList.addAll(admins);
                break;
            case "all":
                title = "All Whitelisted Players";
                admins.forEach(name -> displayList.add(name + ChatColor.YELLOW + " [Admin]"));
                players.forEach(name -> displayList.add(name));
                break;
            default: // "current"
                title = "Active Whitelist (" + currentMode + ")";
                if (currentMode == WhitelistMode.PLAYERS) {
                    displayList.addAll(players);
                }
                displayList.addAll(admins);
                break;
        }
        
        Collections.sort(displayList, String.CASE_INSENSITIVE_ORDER);
        sendPaginatedList(sender, title, displayList, page, listType);
    }

    private void sendPaginatedList(CommandSender sender, String title, List<String> list, int page, String listType) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GOLD + "--- " + title + " ---");
            sender.sendMessage(String.join(", ", list));
            return;
        }
        
        Player player = (Player) sender;
        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) list.size() / itemsPerPage);
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        player.sendMessage(ChatColor.GOLD + "--- " + title + " (Page " + page + "/" + totalPages + ") ---");

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, list.size());

        for (int i = startIndex; i < endIndex; i++) {
            String entry = list.get(i);
            String rawName = ChatColor.stripColor(entry).split(" ")[0];

            TextComponent message = new TextComponent(ChatColor.GRAY + "- " + ChatColor.AQUA + entry);
            
            TextComponent removeButton = new TextComponent(ChatColor.RED + " [Remove]");
            String removeCmd = "/wltxt remove " + (loginListener.getAdminWhitelist().contains(rawName) ? "admin" : "player") + " " + rawName;
            removeButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, removeCmd));
            removeButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to remove " + rawName).color(net.md_5.bungee.api.ChatColor.RED).create()));
            
            message.addExtra(removeButton);
            player.spigot().sendMessage(message);
        }

        TextComponent navigation = new TextComponent("");
        if (page > 1) {
            TextComponent prev = new TextComponent(ChatColor.YELLOW + "<< Prev ");
            prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wltxt list " + listType + " " + (page - 1)));
            navigation.addExtra(prev);
        }

        TextComponent pageInfo = new TextComponent(ChatColor.GRAY + "--- Page " + page + "/" + totalPages + " ---");
        navigation.addExtra(pageInfo);

        if (page < totalPages) {
            TextComponent next = new TextComponent(ChatColor.YELLOW + " Next >>");
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wltxt list " + listType + " " + (page + 1)));
            navigation.addExtra(next);
        }
        
        player.spigot().sendMessage(navigation);
    }
}