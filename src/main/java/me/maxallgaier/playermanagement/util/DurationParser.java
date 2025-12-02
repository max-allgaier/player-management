package me.maxallgaier.playermanagement.util;

import lombok.Getter;
import lombok.NonNull;
import me.maxallgaier.playermanagement.config.DurationConfig;

import java.time.Duration;
import java.util.regex.Pattern;

public final class DurationParser {
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("(?<amount>[0-9]+)(?<unit>[^0-9]*)");
    @Getter private final DurationConfig durationConfig;

    public DurationParser(DurationConfig durationConfig) {
        this.durationConfig = durationConfig;
    }

    public Duration fromString(@NonNull String string) {
        string = string.trim().toLowerCase();

        if (this.durationConfig.permanentKeywords().contains(string)) {
            return null;
        }

        if (string.isEmpty()) throw new RuntimeException("empty string provided");
        if (!Character.isDigit(string.charAt(0))) throw new RuntimeException("string does not start with a number");

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        var matcher = KEYWORD_PATTERN.matcher(string);
        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group("amount"));
            String unit = matcher.group("unit");
            if (this.durationConfig.dayKeywords().contains(unit)) days += amount;
            else if (this.durationConfig.hourKeywords().contains(unit)) hours += amount;
            else if (this.durationConfig.minuteKeywords().contains(unit)) minutes += amount;
            else if (this.durationConfig.secondKeywords().contains(unit)) seconds += amount;
            else throw new RuntimeException("unknown unit: " + unit);
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }
}
