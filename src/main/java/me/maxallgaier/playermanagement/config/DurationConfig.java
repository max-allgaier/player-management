package me.maxallgaier.playermanagement.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface DurationConfig {
    List<String> permanentKeywords();

    List<String> dayKeywords();

    List<String> hourKeywords();

    List<String> minuteKeywords();

    List<String> secondKeywords();

    List<String> suggestions();

    String permanentDisplay();

    String timeUnitDisplay(TimeUnit timeUnit, boolean plural);
}
