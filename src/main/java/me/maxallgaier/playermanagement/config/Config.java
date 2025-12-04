package me.maxallgaier.playermanagement.config;

public interface Config {
    String CURRENT_VERSION = "0.0.1";

    String version();

    DatabaseConfig databaseConfig();

    DurationConfig durationConfig();

    MessagesConfig messagesConfig();

    default boolean isUpToDate() {
        return this.version().equals(CURRENT_VERSION);
    }
}
