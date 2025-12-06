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
        this.banPunishmentService.ban(target.getUniqueId(), this.toIssuerId(issuer), reason, duration);
        if (target.isOnline()) {
            var banScreenComponent = this.toBanScreenComponent(target.getUniqueId()).get();
            target.getPlayer().kick(banScreenComponent);
        }
        Messages.broadcast(messageConfig.playerBanned(target.getName(), this.toIssuerDisplayName(issuer), reason));
    }

    public void unban(OfflinePlayer target, CommandSender issuer, String reason) {
        var messageConfig = this.configManager.config().messagesConfig();
        this.banPunishmentService.unban(target.getUniqueId(), this.toIssuerId(issuer), reason);
        Messages.broadcast(messageConfig.playerBanned(target.getName(), this.toIssuerDisplayName(issuer), reason));
    }

    private UUID toIssuerId(CommandSender issuer) {
        return issuer instanceof Player player ? player.getUniqueId() : null;
    }

    private String toIssuerDisplayName(CommandSender issuer) {
        return issuer instanceof Player player ? player.getName() :
            this.configManager.config().messagesConfig().consoleDisplayName();
    }

    public Optional<Component> toBanScreenComponent(UUID targetId) {
        return this.banPunishmentService.findLatestActiveBanByTargetId(targetId)
            .map(banPunishment -> {
                var duration = banPunishment.durationTillExpired();
                return this.durationParser.toReadableString(duration);
            })
            .map(this.configManager.config().messagesConfig()::banScreen)
            .map(Messages::text);
    }
}
