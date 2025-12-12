package me.maxallgaier.playermanagement.config.yaml;

import lombok.NonNull;
import me.maxallgaier.playermanagement.config.MessagesConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public final class YamlMessagesConfig implements MessagesConfig {
    private final ConfigurationSection yamlConfig;

    public YamlMessagesConfig(@NonNull ConfigurationSection yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public String internalError() {
        return this.yamlConfig.getString("internal-error", "");
    }

    @Override
    public String consoleDisplayName() {
        return this.yamlConfig.getString("console-display-name", "");
    }

    @Override
    public String noReasonFallback() {
        return this.yamlConfig.getString("no-reason-fallback", "");
    }

    @Override
    public String playerBanned(String target, String issuer, String reason) {
        reason = Objects.requireNonNullElse(reason, this.noReasonFallback());
        return this.yamlConfig.getString("player-banned", "")
            .replace("{target}", target).replace("{issuer}", issuer).replace("{reason}", reason);
    }

    @Override
    public String playerUnbanned(String target, String issuer, String reason) {
        reason = Objects.requireNonNullElse(reason, this.noReasonFallback());
        return this.yamlConfig.getString("player-unbanned", "")
            .replace("{target}", target).replace("{issuer}", issuer).replace("{reason}", reason);
    }

    @Override
    public String playerAlreadyBannedError(String target) {
        return this.yamlConfig.getString("player-already-banned-error", "").replace("{target}", target);
    }

    @Override
    public String playerIsNotBannedError(String target) {
        return this.yamlConfig.getString("player-is-not-banned-error", "").replace("{target}", target);
    }

    @Override
    public String banScreen(String duration) {
        return this.yamlConfig.getString("ban-screen", "").replace("{duration}", duration);
    }

    @Override
    public String playerMuted(String target, String issuer, String reason) {
        reason = Objects.requireNonNullElse(reason, this.noReasonFallback());
        return this.yamlConfig.getString("player-muted", "")
            .replace("{target}", target).replace("{issuer}", issuer).replace("{reason}", reason);
    }

    @Override
    public String playerUnmuted(String target, String issuer, String reason) {
        reason = Objects.requireNonNullElse(reason, this.noReasonFallback());
        return this.yamlConfig.getString("player-unmuted", "")
            .replace("{target}", target).replace("{issuer}", issuer).replace("{reason}", reason);
    }

    @Override
    public String playerAlreadyMutedError(String target) {
        return this.yamlConfig.getString("player-already-muted-error", "").replace("{target}", target);
    }

    @Override
    public String playerIsNotMutedError(String target) {
        return this.yamlConfig.getString("player-is-not-muted-error", "").replace("{target}", target);
    }

    @Override
    public String mutedMessage(String duration) {
        return this.yamlConfig.getString("muted-message", "").replace("{duration}", duration);
    }
}
