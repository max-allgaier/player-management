package me.maxallgaier.playermanagement.service;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;

public class PostgresDurationIntervalMapper {
    public static void register(Jdbi jdbi) {
        jdbi.registerArgument(new DurationArgumentFactory());
        jdbi.registerColumnMapper(Duration.class, new DurationColumnMapper());
    }
    
    private static class DurationArgumentFactory extends AbstractArgumentFactory<Duration> {
        public DurationArgumentFactory() {
            super(Types.OTHER);
        }
        
        @Override
        protected Argument build(Duration value, ConfigRegistry config) {
            if (value == null) {
                return (position, statement, ctx) -> statement.setNull(position, Types.OTHER);
            } else {
                return (position, statement, ctx) -> statement.setObject(position, toPGInterval(value));
            }
        }
    }
    
    private static class DurationColumnMapper implements ColumnMapper<Duration> {
        @Override
        public Duration map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
            PGInterval interval = (PGInterval) r.getObject(columnNumber);
            return toDuration(interval);
        }
    }
    
    private static Duration toDuration(PGInterval interval) {
        if (interval == null) return null;
        long totalSeconds = 0;
        totalSeconds += (long) interval.getYears() * 365 * 24 * 60 * 60;
        totalSeconds += (long) interval.getMonths() * 30 * 24 * 60 * 60;
        totalSeconds += (long) interval.getDays() * 24 * 60 * 60;
        totalSeconds += (long) interval.getHours() * 60 * 60;
        totalSeconds += (long) interval.getMinutes() * 60;
        totalSeconds += interval.getWholeSeconds();
        long nanos = (long) interval.getMicroSeconds() * 1000L;
        return Duration.ofSeconds(totalSeconds, nanos);
    }
    
    private static PGInterval toPGInterval(Duration duration) {
        if (duration == null) return null;
        long totalSeconds = duration.getSeconds();
        int nanos = duration.getNano();
        int days = (int) (totalSeconds / (24 * 60 * 60));
        totalSeconds %= (24 * 60 * 60);
        int hours = (int) (totalSeconds / (60 * 60));
        totalSeconds %= (60 * 60);
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        double secondsWithFraction = seconds + (nanos / 1_000_000_000.0);
        return new PGInterval(0, 0, days, hours, minutes, secondsWithFraction);
    }
}