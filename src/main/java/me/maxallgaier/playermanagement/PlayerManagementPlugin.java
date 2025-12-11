package me.maxallgaier.playermanagement;

import lombok.Getter;
import me.maxallgaier.playermanagement.command.BanCommand;
import me.maxallgaier.playermanagement.command.UnbanCommand;
import me.maxallgaier.playermanagement.config.ConfigManager;
import me.maxallgaier.playermanagement.listener.ConnectionListener;
import me.maxallgaier.playermanagement.punishment.ban.BanManager;
import me.maxallgaier.playermanagement.service.ServiceManager;
import me.maxallgaier.playermanagement.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerManagementPlugin extends JavaPlugin {
    @Getter private static PlayerManagementPlugin instance;
    @Getter private ConfigManager configManager;
    @Getter private ServiceManager serviceManager;
    @Getter private DurationParser durationParser;
    @Getter private BanManager banManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.serviceManager = new ServiceManager(this.configManager);
        this.durationParser = new DurationParser(this.configManager.config().durationConfig());
        this.banManager = new BanManager(this.serviceManager.banPunishmentService(), this.configManager, this.durationParser);

        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConnectionListener(this.banManager), this);

        instance = this;

        BanCommand.register();
        UnbanCommand.register();
    }

    @Override
    public void onDisable() {
        if (this.serviceManager != null) {
            this.serviceManager.shutdown();
        }
    }
}
