package me.maxallgaier.playermanagement.punishment.mute.postgres;

import me.maxallgaier.playermanagement.punishment.mute.MutePunishment;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PostgresMutePunishmentTableRowMapper {
    public MutePunishment fromResultSet(ResultSet rs, StatementContext ctx) throws SQLException {
        return new MutePunishment(
            rs.getObject("id", UUID.class),
            rs.getObject("target_id", UUID.class),
            rs.getObject("issuer_id", UUID.class),
            rs.getString("reason"),
            rs.getObject("issued_date_time", OffsetDateTime.class),
            ctx.findColumnMapperFor(Duration.class).orElseThrow().map(rs, "duration", ctx),
            rs.getBoolean("pardoned"),
            rs.getObject("pardoner_id", UUID.class),
            rs.getString("pardon_reason")
        );
    }

    public Map<String, Object> toMap(MutePunishment mutePunishment) {
        var map = new HashMap<String, Object>();
        map.put("id", mutePunishment.id());
        map.put("target_id", mutePunishment.targetId());
        map.put("issuer_id", mutePunishment.issuerId());
        map.put("reason", mutePunishment.reason());
        map.put("issued_date_time", mutePunishment.issuedDateTime());
        map.put("duration", mutePunishment.duration());
        map.put("pardoned", mutePunishment.pardoned());
        map.put("pardoner_id", mutePunishment.pardonerId());
        map.put("pardon_reason", mutePunishment.pardonReason());
        return map;
    }
}
