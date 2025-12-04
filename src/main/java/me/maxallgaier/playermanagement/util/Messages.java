package me.maxallgaier.playermanagement.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class Messages {
    public static Component text(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public static void broadcast(String message) {
        Bukkit.broadcast(text(message));
    }
}
