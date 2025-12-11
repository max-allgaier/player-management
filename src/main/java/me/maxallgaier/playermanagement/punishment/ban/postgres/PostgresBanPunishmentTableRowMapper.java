package me.maxallgaier.playermanagement.punishment.ban.postgres;

import me.maxallgaier.playermanagement.punishment.ban.BanPunishment;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PostgresBanPunishmentTableRowMapper {
    public BanPunishment fromResultSet(ResultSet rs, StatementContext ctx) throws SQLException {
        return new BanPunishment(
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

    public Map<String, Object> toMap(BanPunishment banPunishment) {
        var map = new HashMap<String, Object>();
        map.put("id", banPunishment.id());
        map.put("target_id", banPunishment.targetId());
        map.put("issuer_id", banPunishment.issuerId());
        map.put("reason", banPunishment.reason());
        map.put("issued_date_time", banPunishment.issuedDateTime());
        map.put("duration", banPunishment.duration());
        map.put("pardoned", banPunishment.pardoned());
        map.put("pardoner_id", banPunishment.pardonerId());
        map.put("pardon_reason", banPunishment.pardonReason());
        return map;
    }
}
