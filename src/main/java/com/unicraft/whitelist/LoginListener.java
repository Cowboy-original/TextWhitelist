package com.unicraft.whitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginListener implements Listener {

    private final TextWhitelist plugin;
    private final Set<String> whitelistedNames = new HashSet<>();

    public LoginListener(TextWhitelist plugin) {
        this.plugin = plugin;
        loadWhitelistFromFile();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        if (!whitelistedNames.contains(playerName)) {
            event.kickMessage(Component.text("§cBu sunucuya girmek için listede olmaniz gerekmektedir."));
            plugin.getLogger().info(playerName + " adli oyuncu listede olmadigi icin girisi engellendi.");
        }
    }

    // Bu metot, dışarıdan çağrılarak whitelist'in yeniden yüklenmesini sağlar.
    public void reloadWhitelist() {
        plugin.getLogger().info("whitelist.txt dosyasi yeniden yükleniyor...");
        // 1. Adım: Yeni listeyi hafızaya yükle
        loadWhitelistFromFile();

        // --- YENİ EKLENEN ENFORCE MANTIĞI ---
        plugin.getLogger().info("Online oyuncular yeni whitelist'e göre kontrol ediliyor...");
        
        // Sunucudaki her bir online oyuncu için döngü başlat
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Online oyuncunun adı yeni listede YOKSA...
            if (!whitelistedNames.contains(player.getName())) {
                // Oyuncuyu sunucudan at.
                player.kick(Component.text("§cWhitelist'ten çikarildiniz."));
                plugin.getLogger().info(player.getName() + " adli oyuncu, reload sonrasi listede olmadigi icin sunucudan atildi.");
            }
        });
        // --- ENFORCE MANTIĞI SONU ---
    }

    private void loadWhitelistFromFile() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File whitelistFile = new File(dataFolder, "whitelist.txt");
            if (!whitelistFile.exists()) {
                plugin.getLogger().info("whitelist.txt bulunamadi, bos bir tane olusturuluyor.");
                whitelistFile.createNewFile();
            }

            whitelistedNames.clear();
            Set<String> names = Files.lines(whitelistFile.toPath())
                                    .map(String::trim)
                                    .filter(line -> !line.isEmpty())
                                    .collect(Collectors.toSet());
            whitelistedNames.addAll(names);
            
            plugin.getLogger().info(whitelistedNames.size() + " oyuncu whitelist'e yüklendi.");

        } catch (IOException e) {
            plugin.getLogger().severe("whitelist.txt dosyasi okunurken bir hata olustu!");
            e.printStackTrace();
        }
    }
}