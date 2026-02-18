package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

public class BroadcastCommand implements BotCommand {

    @Override
    public String execute(JsonObject data, String requesterIp) {
        JsonObject response = new JsonObject();

        if (!data.has("message")) {
            response.addProperty("status", "error");
            return response.toString();
        }
        String message = data.get("message").getAsString();
        String template = "<yellow>[Broadcast] <user_msg>";
        Component fullMessage = MiniMessage.miniMessage().deserialize(
                template,
                Placeholder.unparsed("user_msg", message)
        );
        Bukkit.broadcast(fullMessage);

        response.addProperty("status", "success");

        return response.toString();
    }
}
