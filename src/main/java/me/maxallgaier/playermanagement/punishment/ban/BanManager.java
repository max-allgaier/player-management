package me.maxallgaier.playermanagement.punishment.ban;

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
public final class BanManager {
    private final BanPunishmentService banPunishmentService;
    private final ConfigManager configManager;
    private final DurationParser durationParser;

    public void ban(OfflinePlayer target, CommandSender issuer, String reason, Duration duration) {
        var messageConfig = this.configManager.config().messagesConfig();
        var banPunishment = this.banPunishmentService.ban(target.getUniqueId(), this.toIssuerId(issuer), reason, duration);
        if (target.isOnline()) {
            var banScreenComponent = this.toBanScreenComponent(banPunishment);
            target.getPlayer().kick(banScreenComponent);
        }
        var banBroadcastComponent = messageConfig.playerBanned(target.getName(), this.toIssuerDisplayName(issuer), reason);
        Messages.broadcast(banBroadcastComponent);
    }

    public void unban(OfflinePlayer target, CommandSender issuer, String reason) {
        var messageConfig = this.configManager.config().messagesConfig();
        this.banPunishmentService.unban(target.getUniqueId(), this.toIssuerId(issuer), reason);
        Messages.broadcast(messageConfig.playerUnbanned(target.getName(), this.toIssuerDisplayName(issuer), reason));
    }

    public Optional<BanPunishment> findLatestActiveBanByTargetId(UUID targetId) {
        return this.banPunishmentService.findLatestActiveBanByTargetId(targetId);
    }

    private UUID toIssuerId(CommandSender issuer) {
        return issuer instanceof Player player ? player.getUniqueId() : null;
    }

    private String toIssuerDisplayName(CommandSender issuer) {
        return issuer instanceof Player player ? player.getName() :
            this.configManager.config().messagesConfig().consoleDisplayName();
    }

    public Component toBanScreenComponent(BanPunishment banPunishment) {
        var readableDurationTillExpired = this.durationParser.toReadableString(banPunishment.durationTillExpired());
        return Messages.text(this.configManager.messagesConfig().banScreen(readableDurationTillExpired));
    }
}
