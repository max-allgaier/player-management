package me.maxallgaier.playermanagement;

import lombok.Getter;
import me.maxallgaier.playermanagement.config.ConfigManager;
import me.maxallgaier.playermanagement.service.ServiceManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerManagementPlugin extends JavaPlugin {
    @Getter private static PlayerManagementPlugin instance;
    @Getter private ConfigManager configManager;
    @Getter private ServiceManager serviceManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.serviceManager = new ServiceManager(this.configManager);
        instance = this;
    }

    @Override
    public void onDisable() {
        if (this.serviceManager != null) this.serviceManager.shutdown();
    }
}
