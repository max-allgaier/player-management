package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.DurationConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public final class YamlDurationConfig implements DurationConfig {
    private final ConfigurationSection yamlConfig;

    public YamlDurationConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public List<String> permanentKeywords() {
        return this.yamlConfig.getStringList("keywords.permanent");
    }

    @Override
    public List<String> dayKeywords() {
        return this.yamlConfig.getStringList("keywords.day");
    }

    @Override
    public List<String> hourKeywords() {
        return this.yamlConfig.getStringList("keywords.hour");
    }

    @Override
    public List<String> minuteKeywords() {
        return this.yamlConfig.getStringList("keywords.minute");
    }

    @Override
    public List<String> secondKeywords() {
        return this.yamlConfig.getStringList("keywords.second");
    }

    @Override
    public List<String> suggestions() {
        return this.yamlConfig.getStringList("suggestions");
    }
}
