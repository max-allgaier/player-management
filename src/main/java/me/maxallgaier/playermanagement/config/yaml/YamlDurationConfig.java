package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.DurationConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Override
    public String permanentDisplay() {
        return this.yamlConfig.getString("display.permanent", "");
    }

    @Override
    public String timeUnitDisplay(TimeUnit timeUnit, boolean plural) {
        var unitName = switch (timeUnit) {
            case SECONDS -> "second";
            case MINUTES -> "minute";
            case HOURS -> "hour";
            case DAYS -> "day";
            default -> throw new RuntimeException("unsupported time unit: " + timeUnit);
        };
        return this.yamlConfig.getString("display." + unitName + (plural ? "-plural" : "-singular"), "");
    }
}
