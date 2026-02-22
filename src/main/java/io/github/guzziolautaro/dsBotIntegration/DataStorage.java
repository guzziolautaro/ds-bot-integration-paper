package io.github.guzziolautaro.dsBotIntegration;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Properties;

public class DataStorage {
    private final File file;
    private final Properties props = new Properties();

    public DataStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), ".system");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        try (InputStream is = new FileInputStream(file)) {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String botIp, long guildId) {
        props.setProperty("bot-ip", botIp);
        props.setProperty("guild-id", String.valueOf(guildId));
        try (OutputStream os = new FileOutputStream(file)) {
            props.store(os, "INTERNAL DATA - DO NOT MODIFY");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBotIp() { return props.getProperty("bot-ip", "none"); }
    public long getGuildId() { return Long.parseLong(props.getProperty("guild-id", "0")); }
}
