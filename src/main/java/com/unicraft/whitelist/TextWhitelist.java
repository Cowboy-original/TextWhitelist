package com.unicraft.whitelist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TextWhitelist extends JavaPlugin {

    // LoginListener objesini burada bir değişken olarak tutacağız ki ona erişebilelim.
    private LoginListener loginListener;

    @Override
    public void onEnable() {
        getLogger().info("TextWhitelist eklentisi baslatiliyor...");

        // LoginListener'ı oluştururken onu değişkene atıyoruz.
        this.loginListener = new LoginListener(this);
        getServer().getPluginManager().registerEvents(loginListener, this);

        getLogger().info("TextWhitelist eklentisi basariyla baslatildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TextWhitelist eklentisi devre disi birakildi.");
    }

    // Sunucudaki komutları dinleyen metot.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Gelen komut 'whitelistxt' mi diye kontrol et
        if (command.getName().equalsIgnoreCase("whitelistxt")) {
            // Komutun devamında 'reload' yazıyor mu diye kontrol et
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                // Komutu gönderenin yetkisi var mı diye kontrol et
                if (sender.hasPermission("textwhitelist.admin")) {
                    // LoginListener'daki reload metodunu çağır.
                    loginListener.reloadWhitelist();
                    sender.sendMessage("§aTextWhitelist başariyla yeniden yüklendi!");
                    return true;
                } else {
                    sender.sendMessage("§cBu komutu kullanmak için yetkiniz yok.");
                    return true;
                }
            }
        }
        return false;
    }
}