package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;

public interface BotCommand {
    String execute(JsonObject data, String requesterIp);
}
