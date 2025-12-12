package me.maxallgaier.playermanagement.punishment.mute;

import lombok.AllArgsConstructor;
import me.maxallgaier.playermanagement.config.ConfigManager;
import me.maxallgaier.playermanagement.util.DurationParser;
import me.maxallgaier.playermanagement.util.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public final class MuteManager {
    private final MutePunishmentService mutePunishmentService;
    private final ConfigManager configManager;
    private final DurationParser durationParser;

    public void mute(OfflinePlayer target, CommandSender issuer, String reason, Duration duration) {
        this.mutePunishmentService.mute(target.getUniqueId(), this.toIssuerId(issuer), reason, duration);
        var messageConfig = this.configManager.messagesConfig();
        var muteBroadcastComponent = messageConfig.playerMuted(target.getName(), this.toIssuerDisplayName(issuer), reason);
        Messages.broadcast(muteBroadcastComponent);
    }

    public void unmute(OfflinePlayer target, CommandSender issuer, String reason) {
        this.mutePunishmentService.unmute(target.getUniqueId(), this.toIssuerId(issuer), reason);
        var messageConfig = this.configManager.messagesConfig();
        Messages.broadcast(messageConfig.playerUnmuted(target.getName(), this.toIssuerDisplayName(issuer), reason));
    }

    public Optional<MutePunishment> findLatestActiveMuteByTargetId(UUID targetId) {
        return this.mutePunishmentService.findLatestActiveMuteByTargetId(targetId);
    }

    public Component toMuteMessageComponent(MutePunishment mutePunishment) {
        var readableDurationTillExpired = this.durationParser.toReadableString(mutePunishment.durationTillExpired());
        return Messages.text(this.configManager.messagesConfig().mutedMessage(readableDurationTillExpired));
    }

    private UUID toIssuerId(CommandSender issuer) {
        return issuer instanceof Player player ? player.getUniqueId() : null;
    }

    private String toIssuerDisplayName(CommandSender issuer) {
        return issuer instanceof Player player ? player.getName() :
            this.configManager.config().messagesConfig().consoleDisplayName();
    }
}
