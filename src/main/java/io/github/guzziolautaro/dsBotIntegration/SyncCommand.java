package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncCommand implements BotCommand {
    private final JavaPlugin plugin;

    public SyncCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String execute(JsonObject data, String requesterIp) {
        // "Finalize the lock" by updating config.yml
        plugin.getConfig().set("whitelisted-bot-ip", requesterIp);
        plugin.saveConfig();

        return "Sync Successful: IP Locked ";
    }
}