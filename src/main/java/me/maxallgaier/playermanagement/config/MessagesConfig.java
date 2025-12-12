package me.maxallgaier.playermanagement.config;

public interface MessagesConfig {
    String internalError();

    String consoleDisplayName();
    String noReasonFallback();

    String playerBanned(String target, String issuer, String reason);
    String playerUnbanned(String target, String issuer, String reason);
    String playerAlreadyBannedError(String target);
    String playerIsNotBannedError(String target);
    String banScreen(String duration);

    String playerMuted(String target, String issuer, String reason);
    String playerUnmuted(String target, String issuer, String reason);
    String playerAlreadyMutedError(String target);
    String playerIsNotMutedError(String target);
    String mutedMessage(String duration);
}
