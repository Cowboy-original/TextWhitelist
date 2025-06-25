package com.unicraft.whitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent; 
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

    // --- METODUN TAMAMI DEĞİŞTİ ---
    // Artık daha erken çalışan AsyncPlayerPreLoginEvent'i dinliyoruz.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        // Bu olayda oyuncu adı event.getName() ile alınır.
        String playerName = event.getName();

        // Kontrol mantığı aynı.
        if (!whitelistedNames.contains(playerName)) {
            // Oyuncuyu atmak için bu olayda disallow metodu kullanılır.
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    Component.text("§cBu sunucuya girmek için listede olmaniz gerekmektedir."));

            plugin.getLogger().info(playerName + " adli oyuncu (PreLogin asamasinda) listede olmadigi icin girisi engellendi.");
        }
    }

    public void reloadWhitelist() {
        plugin.getLogger().info("Komut ile whitelist.txt dosyasi yeniden yükleniyor...");
        loadWhitelistFromFile();

        // Not: Bu olay asenkron olduğu için, online oyuncuları kick'leme mantığı
        // burada daha karmaşık olabilir. Şimdilik reload sadece listeyi günceller.
    }

    private void loadWhitelistFromFile() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) { dataFolder.mkdir(); }

            File whitelistFile = new File(dataFolder, "whitelist.txt");
            if (!whitelistFile.exists()) {
                plugin.getLogger().info("whitelist.txt bulunamadi, bos bir tane olusturuluyor.");
                whitelistFile.createNewFile();
            }

            whitelistedNames.clear();
            // Büyük/küçük harfe duyarlı kontrol için toLowerCase() yok.
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