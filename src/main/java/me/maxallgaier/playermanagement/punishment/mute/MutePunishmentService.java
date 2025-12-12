package me.maxallgaier.playermanagement.punishment.mute;

import lombok.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class MutePunishmentService {
    private final MutePunishmentRepository repository;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();

    public MutePunishmentService(MutePunishmentRepository repository) {
        this.repository = repository;
    }

    public MutePunishment mute(@NonNull UUID targetId, UUID issuerId, String reason, Duration duration) {
        this.writeLock.lock();
        try {
            var latestActiveMutePunishment = this.repository.findLatestActiveMuteByTargetId(targetId);
            if (latestActiveMutePunishment.isPresent()) {
                throw new MuteException(MuteException.Reason.ALREADY_MUTED);
            }
            var mutePunishment = MutePunishment.builder()
                .targetId(targetId).issuerId(issuerId).reason(reason).issuedDateTime(OffsetDateTime.now())
                .duration(duration)
                .build();
            return this.repository.create(mutePunishment);
        } finally {
            this.writeLock.unlock();
        }
    }

    public MutePunishment unmute(@NonNull UUID targetId, UUID pardonerId, String reason) {
        this.writeLock.lock();
        try {
            var lastestActiveMutePunishment = this.repository.findLatestActiveMuteByTargetId(targetId);
            if (lastestActiveMutePunishment.isEmpty()) {
                throw new MuteException(MuteException.Reason.NOT_MUTED);
            }
            var updatedLatestActiveMutePunishment = lastestActiveMutePunishment.get().toBuilder()
                .pardoned(true).pardonerId(pardonerId).pardonReason(reason)
                .build();
            this.repository.update(updatedLatestActiveMutePunishment);
            return updatedLatestActiveMutePunishment;
        } finally {
            this.writeLock.unlock();
        }
    }

    public Optional<MutePunishment> findLatestActiveMuteByTargetId(@NonNull UUID targetId) {
        this.readLock.lock();
        try {
            return this.repository.findLatestActiveMuteByTargetId(targetId);
        } finally {
            this.readLock.unlock();
        }
    }
}
