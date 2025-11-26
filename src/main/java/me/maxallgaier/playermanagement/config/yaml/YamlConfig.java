package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.Config;
import me.maxallgaier.playermanagement.config.DatabaseConfig;
import org.bukkit.configuration.ConfigurationSection;

public final class YamlConfig implements Config {
    private final ConfigurationSection yamlConfig;
    private final YamlDatabaseConfig databaseConfig;

    public YamlConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;
        var dbConfig = this.yamlConfig.getConfigurationSection("database");
        if (dbConfig == null) throw new RuntimeException("database section of config does not exist");
        this.databaseConfig = new YamlDatabaseConfig(dbConfig);
    }

    @Override
    public String version() {
        return this.yamlConfig.getString("version");
    }

    @Override
    public DatabaseConfig databaseConfig() {
        return this.databaseConfig;
    }
}
