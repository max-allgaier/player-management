package me.maxallgaier.playermanagement.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.experimental.UtilityClass;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.command.argument.AsyncOfflinePlayerCustomArgument;
import me.maxallgaier.playermanagement.command.argument.DurationCustomArgument;
import me.maxallgaier.playermanagement.punishment.mute.MuteException;
import me.maxallgaier.playermanagement.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@UtilityClass
public final class MuteCommand {
    public static void register() {
        new CommandAPICommand("mute")
            .withArguments(
                new AsyncOfflinePlayerCustomArgument("targetId").setOptional(false),
                new DurationCustomArgument("duration").setOptional(true),
                new TextArgument("reason").setOptional(true)
            )
            .executes(MuteCommand::prepareExecuteAsync)
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
                PlayerManagementPlugin.instance().getLogger().log(Level.SEVERE, "mute", e);
                Messages.send(issuer, PlayerManagementPlugin.instance().configManager().messagesConfig().internalError());
            }
        });
    }

    private static void execute(OfflinePlayer target, CommandSender issuer, Duration duration, String reason) {
        try {
            PlayerManagementPlugin.instance().muteManager().mute(target, issuer, reason, duration);
        } catch (MuteException e) {
            handleException(e, target, issuer);
        }
    }

    private static void handleException(MuteException e, OfflinePlayer target, CommandSender issuer) {
        if (e.reason() == MuteException.Reason.ALREADY_MUTED) {
            var message = PlayerManagementPlugin.instance().configManager().messagesConfig()
                .playerAlreadyMutedError(target.getName());
            Messages.send(issuer, message);
        } else {
            throw new RuntimeException("unsupported mute exception reason");
        }
    }
}
