package me.maxallgaier.playermanagement.punishment.ban.postgres;

import lombok.NonNull;
import me.maxallgaier.playermanagement.punishment.ban.BanPunishment;
import me.maxallgaier.playermanagement.punishment.ban.BanPunishmentRepository;
import me.maxallgaier.playermanagement.service.PostgresDatabaseHelper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PostgresBanPunishmentRepository implements BanPunishmentRepository {
    private final PostgresDatabaseHelper databaseHelper;
    private final PostgresBanPunishmentTableRowMapper rowMapper = new PostgresBanPunishmentTableRowMapper();

    public PostgresBanPunishmentRepository(PostgresDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public Optional<BanPunishment> findById(@NonNull UUID id) {
        var sql = "SELECT * FROM ban_punishments WHERE id = :id;";
        return this.databaseHelper.getJdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("id", id)
                .map(this.rowMapper::fromResultSet)
                .findOne()
        );
    }

    @Override
    public List<BanPunishment> findByTargetId(@NonNull UUID targetId) {
        var sql = "SELECT * FROM ban_punishments WHERE target_id = :target_id ORDER BY issued_date_time DESC;";
        return this.databaseHelper.getJdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("target_id", targetId)
                .map(this.rowMapper::fromResultSet)
                .collectIntoList()
        );
    }

    @Override
    public Optional<BanPunishment> findLatestActiveBanByTargetId(@NonNull UUID targetId) {
        var sql = """
            SELECT * FROM ban_punishments
            WHERE target_id = :target_id
              AND pardoned = FALSE
              AND (duration IS NULL OR issued_date_time + duration > NOW())
            ORDER BY issued_date_time DESC
            LIMIT 1;
            """;
        return this.databaseHelper.getJdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("target_id", targetId)
                .map(this.rowMapper::fromResultSet)
                .findOne()
        );
    }

    @Override
    public BanPunishment create(@NonNull BanPunishment banPunishment) {
        if (banPunishment.id() != null) {
            throw new IllegalStateException("banPunishment is already registered");
        }
        var sql = """
            INSERT INTO ban_punishments
              (target_id, issuer_id, reason, issued_date_time, duration, pardoned, pardoner_id, pardon_reason)
            VALUES
              (:target_id, :issuer_id, :reason, :issued_date_time, :duration, :pardoned, :pardoner_id, :pardon_reason)
            RETURNING id;
            """;
        UUID id = this.databaseHelper.getJdbi().withHandle(handle ->
            handle.createUpdate(sql)
                .bindMap(this.rowMapper.toMap(banPunishment))
                .executeAndReturnGeneratedKeys()
                .map((rs, ctx) -> rs.getObject(1, UUID.class))
                .one()
        );
        return banPunishment.toBuilder().id(id).build();
    }

    @Override
    public void update(@NonNull BanPunishment banPunishment) {
        if (banPunishment.id() == null) {
            throw new IllegalStateException("banPunishment is not registered");
        }
        var sql = """
            UPDATE ban_punishments
            SET target_id = :target_id, issuer_id = :issuer_id, reason = :reason, issued_date_time = :issued_date_time,
              duration = :duration, pardoned = :pardoned, pardoner_id = :pardoner_id, pardon_reason = :pardon_reason
            WHERE id = :id;
            """;
        int updated = this.databaseHelper.getJdbi().withHandle(handle ->
            handle.createUpdate(sql)
                .bindMap(this.rowMapper.toMap(banPunishment))
                .execute()
        );

        if (updated == 0) {
            throw new IllegalStateException("BanPunishment not found for id " + banPunishment.id());
        }
    }
}
