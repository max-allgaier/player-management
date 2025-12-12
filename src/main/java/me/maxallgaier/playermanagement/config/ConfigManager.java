package me.maxallgaier.playermanagement.config;

import lombok.Getter;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.config.yaml.YamlConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;

public final class ConfigManager {
    private final PlayerManagementPlugin plugin;
    @Getter private Config config;

    public ConfigManager(PlayerManagementPlugin plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        this.plugin.saveDefaultConfig();
        var loadedConfig = new YamlConfig(this.loadBukkitYamlConfig());
        if (!loadedConfig.isUpToDate()) {
            throw new RuntimeException("config is not up to date; current config version: " +
                Config.CURRENT_VERSION + " loaded config version: " + loadedConfig.version());
        }
        this.config = loadedConfig;
    }

    private YamlConfiguration loadBukkitYamlConfig() {
        var configPath = this.plugin.getDataPath().resolve("config.yml");
        try (var bufferedReader = Files.newBufferedReader(configPath)) {
            return YamlConfiguration.loadConfiguration(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MessagesConfig messagesConfig() {
        return this.config.messagesConfig();
    }
}
