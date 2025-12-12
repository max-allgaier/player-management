package me.maxallgaier.playermanagement.service;

import lombok.Getter;
import me.maxallgaier.playermanagement.config.ConfigManager;
import me.maxallgaier.playermanagement.punishment.ban.BanPunishmentService;
import me.maxallgaier.playermanagement.punishment.ban.postgres.PostgresBanPunishmentRepository;
import me.maxallgaier.playermanagement.punishment.mute.MutePunishmentService;
import me.maxallgaier.playermanagement.punishment.mute.postgres.PostgresMutePunishmentRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ServiceManager {
    private final DatabaseHelper databaseHelper;
    @Getter private final BanPunishmentService banPunishmentService;
    @Getter private final MutePunishmentService mutePunishmentService;
    @Getter private final ExecutorService virtualThreadExecutorService = Executors.newVirtualThreadPerTaskExecutor();

    public ServiceManager(ConfigManager configManager) {
        var dbConfig = configManager.config().databaseConfig();
        var dbType = dbConfig.databaseType().toLowerCase();
        if ("postgres".equals(dbType)) {
            var pgDatabaseHelper = new PostgresDatabaseHelper(dbConfig);
            this.databaseHelper = pgDatabaseHelper;
            this.banPunishmentService = new BanPunishmentService(new PostgresBanPunishmentRepository(pgDatabaseHelper));
            this.mutePunishmentService = new MutePunishmentService(new PostgresMutePunishmentRepository(pgDatabaseHelper));
        } else {
            throw new RuntimeException("invalid database type");
        }
    }

    public void shutdown() {
        this.databaseHelper.shutdown();
    }
}
