package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.Config;
import me.maxallgaier.playermanagement.config.DatabaseConfig;
import me.maxallgaier.playermanagement.config.DurationConfig;
import org.bukkit.configuration.ConfigurationSection;

public final class YamlConfig implements Config {
    private final ConfigurationSection yamlConfig;
    private final YamlDatabaseConfig databaseConfig;
    private final YamlDurationConfig durationConfig;

    public YamlConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;

        var databaseSection = this.yamlConfig.getConfigurationSection("database");
        if (databaseSection == null) throw new RuntimeException("database section of config does not exist");
        this.databaseConfig = new YamlDatabaseConfig(databaseSection);

        var durationSection = this.yamlConfig.getConfigurationSection("duration-keywords");
        if (durationSection == null) throw new RuntimeException("duration section of config does not exist");
        this.durationConfig = new YamlDurationConfig(durationSection);
    }

    @Override
    public String version() {
        return this.yamlConfig.getString("version");
    }

    @Override
    public DatabaseConfig databaseConfig() {
        return this.databaseConfig;
    }

    @Override
    public DurationConfig durationConfig() {
        return this.durationConfig;
    }
}
