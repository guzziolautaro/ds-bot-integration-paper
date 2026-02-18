package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

public class StatusCommand implements BotCommand{

    @Override
    public String execute(JsonObject data, String requesterIp) {
        JsonObject response = new JsonObject();

        response.addProperty("online", Bukkit.getOnlinePlayers().size());
        response.addProperty("max", Bukkit.getMaxPlayers());

        return response.toString();
    }
}
