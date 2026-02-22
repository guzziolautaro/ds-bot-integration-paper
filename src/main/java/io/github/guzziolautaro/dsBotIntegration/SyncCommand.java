package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncCommand implements BotCommand {
    private final JavaPlugin plugin;
    private final DataStorage storage;

    public SyncCommand(JavaPlugin plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public String execute(JsonObject data, String requesterIp) {
        long guildId = data.get("guild_id").getAsLong();

        storage.save(requesterIp, guildId);

        JsonObject response = new JsonObject();
        response.addProperty("status", "success");

        return response.toString();
    }
}