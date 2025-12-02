package me.maxallgaier.playermanagement.command.argument;

import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import org.bukkit.command.CommandSender;

import java.time.Duration;

public final class DurationCustomArgument extends CustomArgument<Duration, String> {
    public DurationCustomArgument(String name) {
        super(new StringArgument(name), DurationCustomArgument::execute);
        super.replaceSuggestions(ArgumentSuggestions.strings(DurationCustomArgument::suggestions));
    }

    private static Duration execute(CustomArgumentInfo<String> info) {
        return PlayerManagementPlugin.getInstance().getDurationParser().fromString(info.currentInput());
    }

    private static String[] suggestions(SuggestionInfo<CommandSender> info) {
        var durationConfig = PlayerManagementPlugin.getInstance().getConfigManager().getConfig().durationConfig();
        return durationConfig.suggestions().toArray(new String[0]);
    }
}
