package me.maxallgaier.playermanagement.punishment.ban;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BanPunishmentRepository {
    Optional<BanPunishment> findById(@NonNull UUID id);

    List<BanPunishment> findByTargetId(@NonNull UUID targetId);

    Optional<BanPunishment> findLatestActiveBanByTargetId(@NonNull UUID targetId);

    BanPunishment create(@NonNull BanPunishment banPunishment);

    void update(@NonNull BanPunishment banPunishment);
}
