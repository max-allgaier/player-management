package me.maxallgaier.playermanagement.service;

import lombok.Getter;
import me.maxallgaier.playermanagement.config.ConfigManager;
import me.maxallgaier.playermanagement.punishment.ban.BanPunishmentService;
import me.maxallgaier.playermanagement.punishment.ban.postgres.PostgresBanPunishmentRepository;

public final class ServiceManager {
    @Getter private final BanPunishmentService banPunishmentService;
    private final DatabaseHelper databaseHelper;

    public ServiceManager(ConfigManager configManager) {
        var dbConfig = configManager.getConfig().databaseConfig();
        var dbType = dbConfig.databaseType().toLowerCase();
        if ("postgres".equals(dbType)) {
            var pgDatabaseHelper = new PostgresDatabaseHelper(dbConfig);
            this.banPunishmentService = new BanPunishmentService(new PostgresBanPunishmentRepository(pgDatabaseHelper));
            this.databaseHelper = pgDatabaseHelper;
        } else {
            throw new RuntimeException("invalid database type");
        }
    }

    public void shutdown() {
        this.databaseHelper.shutdown();
    }
}
