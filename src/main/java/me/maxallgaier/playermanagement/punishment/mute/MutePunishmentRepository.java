package me.maxallgaier.playermanagement.punishment.mute;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MutePunishmentRepository {
    Optional<MutePunishment> findById(@NonNull UUID id);

    List<MutePunishment> findByTargetId(@NonNull UUID targetId);

    Optional<MutePunishment> findLatestActiveMuteByTargetId(@NonNull UUID targetId);

    MutePunishment create(@NonNull MutePunishment mutePunishment);

    void update(@NonNull MutePunishment mutePunishment);
}
