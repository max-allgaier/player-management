package me.maxallgaier.playermanagement.command.argument;

import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class AsyncOfflinePlayerCustomArgument extends CustomArgument<CompletableFuture<OfflinePlayer>, String> {
    private static final Pattern MC_USERNAME_PATTERN = Pattern.compile("^\\w{1,16}$");

    public AsyncOfflinePlayerCustomArgument(String name) {
        super(new StringArgument(name), AsyncOfflinePlayerCustomArgument::execute);
        super.replaceSuggestions(ArgumentSuggestions.stringCollection(AsyncOfflinePlayerCustomArgument::suggestions));
    }

    private static CompletableFuture<OfflinePlayer> execute(CustomArgumentInfo<String> info) throws CustomArgumentException {
        String usernameInput = info.currentInput();
        if (MC_USERNAME_PATTERN.matcher(usernameInput).matches()) {
            var executorService = PlayerManagementPlugin.instance().serviceManager().virtualThreadExecutorService();
            return CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(usernameInput), executorService);
        } else {
            throw CustomArgument.CustomArgumentException.fromString("invalid username provided");
        }
    }

    private static List<String> suggestions(SuggestionInfo<CommandSender> info) {
        var onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return StringUtil.copyPartialMatches(info.currentArg(), onlinePlayers, new ArrayList<>());
    }
}
