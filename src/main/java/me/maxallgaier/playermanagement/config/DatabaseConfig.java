package me.maxallgaier.playermanagement.config;

public interface DatabaseConfig {
    String databaseType();

    String host();

    int port();

    String databaseName();

    String username();

    String password();
}
