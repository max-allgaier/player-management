package me.maxallgaier.playermanagement.command.argument;

import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.maxallgaier.playermanagement.PlayerManagementPlugin;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.List;

public final class DurationCustomArgument extends CustomArgument<Duration, String> {
    public DurationCustomArgument(String name) {
        super(new StringArgument(name), DurationCustomArgument::execute);
        super.replaceSuggestions(ArgumentSuggestions.stringCollection(DurationCustomArgument::suggestions));
    }

    private static Duration execute(CustomArgumentInfo<String> info) {
        return PlayerManagementPlugin.instance().durationParser().fromString(info.currentInput());
    }

    private static List<String> suggestions(SuggestionInfo<CommandSender> info) {
        return PlayerManagementPlugin.instance().configManager().config().durationConfig().suggestions();
    }
}
