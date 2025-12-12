package me.maxallgaier.playermanagement.punishment.mute.postgres;

import lombok.NonNull;
import me.maxallgaier.playermanagement.punishment.mute.MutePunishment;
import me.maxallgaier.playermanagement.punishment.mute.MutePunishmentRepository;
import me.maxallgaier.playermanagement.service.PostgresDatabaseHelper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PostgresMutePunishmentRepository implements MutePunishmentRepository {
    private final PostgresDatabaseHelper databaseHelper;
    private final PostgresMutePunishmentTableRowMapper rowMapper = new PostgresMutePunishmentTableRowMapper();

    public PostgresMutePunishmentRepository(PostgresDatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public Optional<MutePunishment> findById(@NonNull UUID id) {
        var sql = "SELECT * FROM mute_punishments WHERE id = :id;";
        return this.databaseHelper.jdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("id", id)
                .map(this.rowMapper::fromResultSet)
                .findOne()
        );
    }

    @Override
    public List<MutePunishment> findByTargetId(@NonNull UUID targetId) {
        var sql = "SELECT * FROM mute_punishments WHERE target_id = :target_id ORDER BY issued_date_time DESC;";
        return this.databaseHelper.jdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("target_id", targetId)
                .map(this.rowMapper::fromResultSet)
                .collectIntoList()
        );
    }

    @Override
    public Optional<MutePunishment> findLatestActiveMuteByTargetId(@NonNull UUID targetId) {
        var sql = """
            SELECT * FROM mute_punishments
            WHERE target_id = :target_id
              AND pardoned = FALSE
              AND (duration IS NULL OR issued_date_time + duration > NOW())
            ORDER BY issued_date_time DESC
            LIMIT 1;
            """;
        return this.databaseHelper.jdbi().withHandle(handle ->
            handle.createQuery(sql)
                .bind("target_id", targetId)
                .map(this.rowMapper::fromResultSet)
                .findOne()
        );
    }

    @Override
    public MutePunishment create(@NonNull MutePunishment mutePunishment) {
        if (mutePunishment.id() != null) {
            throw new IllegalStateException("mutePunishment is already registered");
        }
        var sql = """
            INSERT INTO mute_punishments
              (target_id, issuer_id, reason, issued_date_time, duration, pardoned, pardoner_id, pardon_reason)
            VALUES
              (:target_id, :issuer_id, :reason, :issued_date_time, :duration, :pardoned, :pardoner_id, :pardon_reason)
            RETURNING id;
            """;
        UUID id = this.databaseHelper.jdbi().withHandle(handle ->
            handle.createUpdate(sql)
                .bindMap(this.rowMapper.toMap(mutePunishment))
                .executeAndReturnGeneratedKeys()
                .map((rs, ctx) -> rs.getObject(1, UUID.class))
                .one()
        );
        return mutePunishment.toBuilder().id(id).build();
    }

    @Override
    public void update(@NonNull MutePunishment mutePunishment) {
        if (mutePunishment.id() == null) {
            throw new IllegalStateException("mutePunishment is not registered");
        }
        var sql = """
            UPDATE mute_punishments
            SET target_id = :target_id, issuer_id = :issuer_id, reason = :reason, issued_date_time = :issued_date_time,
              duration = :duration, pardoned = :pardoned, pardoner_id = :pardoner_id, pardon_reason = :pardon_reason
            WHERE id = :id;
            """;
        int updated = this.databaseHelper.jdbi().withHandle(handle ->
            handle.createUpdate(sql)
                .bindMap(this.rowMapper.toMap(mutePunishment))
                .execute()
        );
        if (updated == 0) {
            throw new IllegalStateException("MutePunishment not found for id " + mutePunishment.id());
        }
    }
}
