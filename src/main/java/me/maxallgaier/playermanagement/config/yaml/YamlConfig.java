package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.Config;
import me.maxallgaier.playermanagement.config.DatabaseConfig;
import me.maxallgaier.playermanagement.config.DurationConfig;
import me.maxallgaier.playermanagement.config.MessagesConfig;
import org.bukkit.configuration.ConfigurationSection;

public final class YamlConfig implements Config {
    private final ConfigurationSection yamlConfig;
    private final YamlDatabaseConfig databaseConfig;
    private final YamlDurationConfig durationConfig;
    private final YamlMessagesConfig messagesConfig;

    public YamlConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;

        var databaseSection = this.yamlConfig.getConfigurationSection("database");
        if (databaseSection == null) throw new RuntimeException("database section of config does not exist");
        this.databaseConfig = new YamlDatabaseConfig(databaseSection);

        var durationSection = this.yamlConfig.getConfigurationSection("duration");
        if (durationSection == null) throw new RuntimeException("duration section of config does not exist");
        this.durationConfig = new YamlDurationConfig(durationSection);

        var messagesSection = this.yamlConfig.getConfigurationSection("messages");
        if (messagesSection == null) throw new RuntimeException("messages section of config does not exist");
        this.messagesConfig = new YamlMessagesConfig(messagesSection);
    }

    @Override
    public String version() {
        return this.yamlConfig.getString("version", "");
    }

    @Override
    public DatabaseConfig databaseConfig() {
        return this.databaseConfig;
    }

    @Override
    public DurationConfig durationConfig() {
        return this.durationConfig;
    }

    @Override
    public MessagesConfig messagesConfig() {
        return this.messagesConfig;
    }
}
