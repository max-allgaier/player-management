package me.maxallgaier.playermanagement.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.experimental.UtilityClass;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.command.argument.AsyncOfflinePlayerCustomArgument;
import me.maxallgaier.playermanagement.command.argument.DurationCustomArgument;
import me.maxallgaier.playermanagement.punishment.ban.BanException;
import me.maxallgaier.playermanagement.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@UtilityClass
public final class BanCommand {
    public static void register() {
        new CommandAPICommand("ban")
            .withArguments(
                new AsyncOfflinePlayerCustomArgument("targetId").setOptional(false),
                new DurationCustomArgument("duration").setOptional(true),
                new TextArgument("reason").setOptional(true)
            )
            .executes(BanCommand::prepareExecuteAsync)
            .register(PlayerManagementPlugin.instance());
    }

    private static void prepareExecuteAsync(CommandSender issuer, CommandArguments args) {
        Bukkit.getScheduler().runTaskAsynchronously(PlayerManagementPlugin.instance(), () -> {
            try {
                @SuppressWarnings("unchecked,ConstantConditions")
                var target = ((CompletableFuture<OfflinePlayer>) args.get("targetId")).join();
                var duration = (Duration) args.get("duration");
                var reason = (String) args.get("reason");
                execute(target, issuer, duration, reason);
            } catch (Exception e) {
                PlayerManagementPlugin.instance().getLogger().log(Level.SEVERE, "ban", e);
                Messages.send(issuer, PlayerManagementPlugin.instance().configManager().messagesConfig().internalError());
            }
        });
    }

    private static void execute(OfflinePlayer target, CommandSender issuer, Duration duration, String reason) {
        try {
            PlayerManagementPlugin.instance().banManager().ban(target, issuer, reason, duration);
        } catch (BanException e) {
            handleException(e, target, issuer);
        }
    }

    private static void handleException(BanException e, OfflinePlayer target, CommandSender issuer) {
        if (e.reason() == BanException.Reason.ALREADY_BANNED) {
            var message = PlayerManagementPlugin.instance().configManager().messagesConfig()
                .playerAlreadyBannedError(target.getName());
            Messages.send(issuer, message);
        } else {
            throw new RuntimeException("unsupported ban exception reason");
        }
    }
}
