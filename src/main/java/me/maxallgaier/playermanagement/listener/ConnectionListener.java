package me.maxallgaier.playermanagement.listener;

import lombok.AllArgsConstructor;
import me.maxallgaier.playermanagement.punishment.ban.BanManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@AllArgsConstructor
public final class ConnectionListener implements Listener {
    private final BanManager banManager;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        var targetId = event.getUniqueId();
        this.banManager.toBanScreenComponent(targetId)
            .ifPresent(banScreenComponent -> event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banScreenComponent));
    }
}
