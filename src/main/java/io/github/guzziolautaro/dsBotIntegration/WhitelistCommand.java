package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class WhitelistCommand implements BotCommand{
    private final JavaPlugin plugin;

    public WhitelistCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String execute(JsonObject data, String requesterIp) {
        JsonObject response = new JsonObject();

        if (!data.has("operation")) {
            response.addProperty("status", "error");
            return response.toString();
        }
        if (!Bukkit.hasWhitelist()) {
            response.addProperty("status", "unavailable");
            return response.toString();
        }

        switch (data.get("operation").getAsString()) {
            case "add":
                if (!data.has("name")) {
                    response.addProperty("status", "error");
                    return response.toString();
                }

                String playerName = data.get("name").getAsString();
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

                if (player.isWhitelisted()) {
                    response.addProperty("status", "conflict");
                    return response.toString();
                }

                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.setWhitelisted(true);
                    plugin.getLogger().info("Successfully whitelisted: " + playerName);
                });

                response.addProperty("status", "success");
                return response.toString();
            case "remove":
                //todo
                break;
            case "list":
                //todo
                break;
        }

        return response.toString();
    }
}
