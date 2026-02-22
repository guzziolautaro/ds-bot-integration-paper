package io.github.guzziolautaro.dsBotIntegration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
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
    private DataStorage storage;
    private final Map<String, BotCommand> commands = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerCommands();

        this.storage = new DataStorage(this);
        this.port = getConfig().getInt("port", 8080);
        this.authToken = getConfig().getString("auth-token");

        try {
            server = HttpServer.create(new InetSocketAddress(this.port), 0);

            server.createContext("/bot", exchange -> {

                try {

                    String requesterIp = exchange.getRemoteAddress().getAddress().getHostAddress();

                    String whitelistedIp = getConfig().getString("whitelisted-bot-ip", "none");
                    if (!whitelistedIp.equals("none") && !requesterIp.equals(whitelistedIp)) {
                        exchange.sendResponseHeaders(403, -1);
                        return;
                    }

                    //check auth
                    String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                    if (authHeader == null || !authHeader.equals("Bearer " + this.authToken)) {
                        exchange.sendResponseHeaders(403, -1);
                        return;
                    }

                    //check method
                    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                        exchange.sendResponseHeaders(405, -1);
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
                        response = commands.get(action).execute(json, requesterIp);
                    } else {
                        response = "Error: Unknown action '" + action + "'";
                    }

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }

                } catch (Exception e) {
                    getLogger().severe(e.getMessage());
                    exchange.sendResponseHeaders(500, -1);
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

    public DataStorage getStorage() {
        return storage;
    }

    private void registerCommands() {
        commands.put("status", new StatusCommand());
        commands.put("sync", new SyncCommand(this, this.getStorage()));
        commands.put("broadcast", new BroadcastCommand());
        commands.put("whitelist", new WhitelistCommand(this));
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
