package me.maxallgaier.playermanagement.config;

import lombok.Getter;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.config.yaml.YamlConfig;

public final class ConfigManager {
    private final PlayerManagementPlugin plugin;
    @Getter private Config config;

    public ConfigManager(PlayerManagementPlugin plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        this.plugin.saveDefaultConfig();
        var loadedConfig = new YamlConfig(this.plugin.getConfig());
        if (!loadedConfig.isUpToDate()) {
            throw new RuntimeException("config is not up to date; current config version: " +
                Config.CURRENT_VERSION + " loaded config version: " + loadedConfig.version());
        }
        this.config = loadedConfig;
    }
}
