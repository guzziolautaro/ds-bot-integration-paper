package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

public class StatusCommand implements BotCommand{

    @Override
    public String execute(JsonObject data, String requesterIp) {
        return "Online: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers();
    }
}
