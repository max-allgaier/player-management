package me.maxallgaier.playermanagement.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.config.DatabaseConfig;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;

public final class PostgresDatabaseHelper implements DatabaseHelper {
    private final HikariDataSource dataSource;
    @Getter private final Jdbi jdbi;

    public PostgresDatabaseHelper(DatabaseConfig dbConfig) {
        this(dbConfig.host(), dbConfig.port(), dbConfig.databaseName(), dbConfig.username(), dbConfig.password());
    }

    public PostgresDatabaseHelper(String host, int port, String databaseName, String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("postgres driver not found");
        }

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + databaseName);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        this.dataSource = new HikariDataSource(hikariConfig);

        this.jdbi = Jdbi.create(this.dataSource);
        this.jdbi.installPlugin(new PostgresPlugin());
        PostgresDurationIntervalMapper.register(this.jdbi);

        Flyway.configure(PlayerManagementPlugin.class.getClassLoader())
            .dataSource(this.dataSource)
            .locations("database/migrations/postgres/")
            .load()
            .migrate();
    }

    @Override
    public void shutdown() {
        this.dataSource.close();
    }
}
