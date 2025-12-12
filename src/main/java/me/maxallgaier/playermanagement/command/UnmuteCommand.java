package me.maxallgaier.playermanagement.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.experimental.UtilityClass;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import me.maxallgaier.playermanagement.command.argument.AsyncOfflinePlayerCustomArgument;
import me.maxallgaier.playermanagement.punishment.mute.MuteException;
import me.maxallgaier.playermanagement.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@UtilityClass
public class UnmuteCommand {
    public static void register() {
        new CommandAPICommand("unmute")
            .withArguments(
                new AsyncOfflinePlayerCustomArgument("targetId").setOptional(false),
                new TextArgument("reason").setOptional(true)
            )
            .executes(UnmuteCommand::prepareExecute)
            .register(PlayerManagementPlugin.instance());
    }

    private static void prepareExecute(CommandSender issuer, CommandArguments args) {
        Bukkit.getScheduler().runTaskAsynchronously(PlayerManagementPlugin.instance(), () -> {
            try {
                @SuppressWarnings("unchecked,ConstantConditions")
                var target = ((CompletableFuture<OfflinePlayer>) args.get("targetId")).join();
                var reason = (String) args.get("reason");
                execute(target, issuer, reason);
            } catch (Exception e) {
                PlayerManagementPlugin.instance().getLogger().log(Level.SEVERE, "unmute", e);
                Messages.send(issuer, PlayerManagementPlugin.instance().configManager().messagesConfig().internalError());
            }
        });
    }

    private static void execute(OfflinePlayer target, CommandSender issuer, String reason) {
        try {
            PlayerManagementPlugin.instance().muteManager().unmute(target, issuer, reason);
        } catch (MuteException e) {
            handleException(e, target, issuer);
        }
    }

    private static void handleException(MuteException e, OfflinePlayer target, CommandSender issuer) {
        if (e.reason() == MuteException.Reason.NOT_MUTED) {
            String message = PlayerManagementPlugin.instance().configManager().messagesConfig()
                .playerIsNotMutedError(target.getName());
            Messages.send(issuer, message);
        } else {
            throw new RuntimeException("unsupported mute exception reason");
        }
    }
}
