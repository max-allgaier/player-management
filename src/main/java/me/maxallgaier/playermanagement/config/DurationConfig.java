package me.maxallgaier.playermanagement.config;

import java.util.List;

public interface DurationConfig {
    List<String> permanentKeywords();

    List<String> dayKeywords();

    List<String> hourKeywords();

    List<String> minuteKeywords();

    List<String> secondKeywords();

    List<String> suggestions();
}
