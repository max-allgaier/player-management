package me.maxallgaier.playermanagement.util;

import me.maxallgaier.playermanagement.config.DurationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DurationParserTest {
    DurationParser durationParser;
    DurationConfig durationConfig;

    @BeforeEach
    void setUp() {
        durationConfig = new DurationConfig() {
            @Override public List<String> permanentKeywords() { return List.of("permanent", "perm"); }
            @Override public List<String> dayKeywords() { return List.of("days", "day", "d"); }
            @Override public List<String> hourKeywords() { return List.of("hours", "hour", "hrs", "hr", "h"); }
            @Override public List<String> minuteKeywords() { return List.of("minutes", "minute", "mins", "min", "m"); }
            @Override public List<String> secondKeywords() { return List.of("seconds", "second", "secs", "sec", "s"); }
            @Override public List<String> suggestions() { throw new RuntimeException("not implemented"); }
            @Override public String permanentDisplay() { throw new RuntimeException("not implemented"); }
            @Override public String timeUnitDisplay(TimeUnit timeUnit, boolean plural) { throw new RuntimeException("not implemented"); }
        };
        durationParser = new DurationParser(durationConfig);
    }

    @Test
    @DisplayName("Parse single day value")
    void testSingleDay() {
        Duration result = durationParser.fromString("5days");
        assertEquals(Duration.ofDays(5), result);
    }

    @Test
    @DisplayName("Parse single hour value")
    void testSingleHour() {
        Duration result = durationParser.fromString("3hours");
        assertEquals(Duration.ofHours(3), result);
    }

    @Test
    @DisplayName("Parse single minute value")
    void testSingleMinute() {
        Duration result = durationParser.fromString("45minutes");
        assertEquals(Duration.ofMinutes(45), result);
    }

    @Test
    @DisplayName("Parse single second value")
    void testSingleSecond() {
        Duration result = durationParser.fromString("30seconds");
        assertEquals(Duration.ofSeconds(30), result);
    }

    @Test
    @DisplayName("Parse multiple time units")
    void testMultipleUnits() {
        Duration result = durationParser.fromString("2days3hours30minutes45seconds");
        Duration expected = Duration.ofDays(2).plusHours(3).plusMinutes(30).plusSeconds(45);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Parse with whitespace trimming")
    void testWithWhitespace() {
        Duration result = durationParser.fromString("  5days  ");
        assertEquals(Duration.ofDays(5), result);
    }

    @Test
    @DisplayName("Parse with short unit names")
    void testShortUnitNames() {
        Duration result = durationParser.fromString("1d2h3m4s");
        Duration expected = Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Parse with mixed case units")
    void testMixedCaseUnits() {
        Duration result = durationParser.fromString("5DAYS3Hours");
        Duration expected = Duration.ofDays(5).plusHours(3);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Parse multiple values of same unit")
    void testMultipleSameUnit() {
        Duration result = durationParser.fromString("2days3days5days");
        assertEquals(Duration.ofDays(10), result);
    }

    @Test
    @DisplayName("Parse with no unit separator")
    void testNoUnitSeparator() {
        Duration result = durationParser.fromString("1h30m");
        Duration expected = Duration.ofHours(1).plusMinutes(30);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Throw exception for empty or whitespace-only strings")
    void testEmptyString(String input) {
        var runtimeException = assertThrows(RuntimeException.class, () -> durationParser.fromString(input));
        assertEquals("empty string provided", runtimeException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"days", "hello5days", "abc123"})
    @DisplayName("Throw exception when string doesn't start with number")
    void testNotStartingWithNumber(String input) {
        var runtimeException = assertThrows(RuntimeException.class, () -> durationParser.fromString(input));
        assertEquals("string does not start with a number", runtimeException.getMessage());
    }

    @Test
    @DisplayName("Throw exception for unknown unit")
    void testUnknownUnit() {
        var runtimeException = assertThrows(RuntimeException.class, () -> durationParser.fromString("5weeks"));
        assertTrue(runtimeException.getMessage().contains("unknown unit"));
    }

    @Test
    @DisplayName("Parse zero values")
    void testZeroValues() {
        Duration result = durationParser.fromString("0days");
        assertEquals(Duration.ZERO, result);
    }

    @Test
    @DisplayName("Parse large numbers")
    void testLargeNumbers() {
        Duration result = durationParser.fromString("999days");
        assertEquals(Duration.ofDays(999), result);
    }

    @ParameterizedTest
    @CsvSource({"1day, 1", "2hr, 2", "3min, 3", "4sec, 4"})
    @DisplayName("Parse alternative unit names")
    void testAlternativeUnitNames(String input, int expectedValue) {
        Duration result = durationParser.fromString(expectedValue + input.substring(1));
        assertNotNull(result);
    }

    @Test
    @DisplayName("Parse complex real-world example")
    void testComplexExample() {
        Duration result = durationParser.fromString("1d12h30m45s");
        Duration expected = Duration.ofDays(1).plusHours(12).plusMinutes(30).plusSeconds(45);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Handle unit with no space after number")
    void testNoSpaceAfterNumber() {
        Duration result = durationParser.fromString("50days60hours1minutes");
        Duration expected = Duration.ofDays(50).plusHours(60).plusMinutes(1);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Permanent returns null")
    void testPermanentReturnsNull() {
        assertNull(durationParser.fromString("permanent"));
        assertNull(durationParser.fromString("perm"));
    }

    @Test
    @DisplayName("Permanent partial returns error")
    void testPermanentPartialReturnsError() {
        assertThrows(Exception.class, () -> durationParser.fromString("permanen"));
        assertThrows(Exception.class, () -> durationParser.fromString("perma"));
    }

    @Test
    @DisplayName("null input throws exception")
    void testNullInputReturnsException() {
        assertThrows(Exception.class, () -> durationParser.fromString(null));
    }
}
