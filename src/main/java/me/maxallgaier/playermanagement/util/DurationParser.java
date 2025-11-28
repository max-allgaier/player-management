package me.maxallgaier.playermanagement.util;

import lombok.Getter;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class DurationParser {
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("(?<amount>[0-9]+)(?<unit>[^0-9]*)");
    @Getter private final KeywordConfig keywordConfig;

    public DurationParser(KeywordConfig keywordConfig) {
        this.keywordConfig = keywordConfig;
    }

    public Duration fromString(String string) {
        string = string.trim();
        if (string.isEmpty()) throw new RuntimeException("empty string provided");
        if (!Character.isDigit(string.charAt(0))) throw new RuntimeException("string does not start with a number");

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        var matcher = KEYWORD_PATTERN.matcher(string);
        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group("amount"));
            String unit = matcher.group("unit").toLowerCase();
            if (keywordConfig.dayKeywords.contains(unit)) days += amount;
            else if (keywordConfig.hourKeywords.contains(unit)) hours += amount;
            else if (keywordConfig.minuteKeywords.contains(unit)) minutes += amount;
            else if (keywordConfig.secondKeywords.contains(unit)) seconds += amount;
            else throw new RuntimeException("unknown unit: " + unit);
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    public record KeywordConfig(
        List<String> permanentKeywords, List<String> dayKeywords, List<String> hourKeywords,
        List<String> minuteKeywords, List<String> secondKeywords
    ) {
        public List<String> getAllNonPermanentKeywordsSortedByLength() {
            return Stream.of(this.dayKeywords, this.hourKeywords, this.minuteKeywords, this.secondKeywords)
                .flatMap(List::stream).sorted(Comparator.comparingInt(String::length).reversed()).toList();
        }

        public List<String> getAllPermanentKeywordsSortedByLength() {
            return this.permanentKeywords.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
        }
    }
}
