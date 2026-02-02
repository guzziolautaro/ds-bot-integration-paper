package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class DsBotIntegration extends JavaPlugin {

    private String authToken;
    private int port;
    private HttpServer server;
    private final Map<String, BotCommand> commands = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerCommands();

        this.port = getConfig().getInt("port", this.port);
        this.authToken = getConfig().getString("auth-token");

        try {
            server = HttpServer.create(new InetSocketAddress(this.port), 0);

            server.createContext("/bot", exchange -> {
                try {
                    //check auth
                    String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                    if (authHeader == null || !authHeader.equals("Bearer " + this.authToken)) {
                        exchange.sendResponseHeaders(403, 0);
                        return;
                    }

                    //check method
                    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                        exchange.sendResponseHeaders(405, 0);
                        return;
                    }

                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String action;
                    if (json.has("action")) {
                        action = json.get("action").getAsString();
                    } else {
                        action = "none";
                    }

                    String response;
                    if (commands.containsKey(action)) {
                        response = commands.get(action).execute(json);
                    } else {
                        response = "Error: Unknown action '" + action + "'";
                    }

                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length); // Use the length of the byte array
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }

                } catch (Exception e) {
                    getLogger().severe(e.getMessage());
                    exchange.sendResponseHeaders(500, 0);
                } finally {
                    exchange.close();
                }
            });
                server.setExecutor(null);
                server.start();
                getLogger().info("Started bot bridge on port " + this.port);
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void registerCommands() {
        commands.put("status", data ->
                "Online: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        //todo
    }

    private void sendHeartbeat() {
        //todo
    }

    public void onDisable() {
        if (server != null) {
            server.stop(0);
            getLogger().info("Bot Bridge server stopped.");
        }
    }
}
