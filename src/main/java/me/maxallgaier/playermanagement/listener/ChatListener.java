package me.maxallgaier.playermanagement.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.AllArgsConstructor;
import me.maxallgaier.playermanagement.punishment.mute.MuteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public final class ChatListener implements Listener {
    private final MuteManager muteManager;

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        this.muteManager.findLatestActiveMuteByTargetId(player.getUniqueId()).ifPresent(mutePunishment -> {
            var muteMessageComponent = this.muteManager.toMuteMessageComponent(mutePunishment);
            player.sendMessage(muteMessageComponent);
            event.setCancelled(true);
        });
    }
}
