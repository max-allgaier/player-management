package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.DatabaseConfig;
import org.bukkit.configuration.ConfigurationSection;

public final class YamlDatabaseConfig implements DatabaseConfig {
    private final ConfigurationSection yamlConfig;

    public YamlDatabaseConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public String databaseType() {
        return this.yamlConfig.getString("type");
    }

    @Override
    public String host() {
        return this.yamlConfig.getString("host");
    }

    @Override
    public int port() {
        return this.yamlConfig.getInt("port");
    }

    @Override
    public String databaseName() {
        return this.yamlConfig.getString("database-name");
    }

    @Override
    public String username() {
        return this.yamlConfig.getString("username");
    }

    @Override
    public String password() {
        return this.yamlConfig.getString("password");
    }
}
