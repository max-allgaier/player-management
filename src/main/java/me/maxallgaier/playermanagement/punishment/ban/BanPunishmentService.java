package me.maxallgaier.playermanagement.punishment.ban;

import lombok.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BanPunishmentService {
    private final BanPunishmentRepository repository;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();

    public BanPunishmentService(BanPunishmentRepository repository) {
        this.repository = repository;
    }

    public BanPunishment ban(@NonNull UUID targetId, UUID issuerId, String reason, Duration duration) {
        this.writeLock.lock();
        try {
            var latestActiveBanPunishment = this.repository.findLatestActiveBanByTargetId(targetId);
            if (latestActiveBanPunishment.isPresent()) {
                throw new IllegalStateException("target with id " + targetId +
                    " is already banned under ban id " + latestActiveBanPunishment.get().id());
            }

            var banPunishment = BanPunishment.builder()
                .targetId(targetId).issuerId(issuerId).reason(reason).issuedDateTime(OffsetDateTime.now())
                .duration(duration)
                .build();
            return this.repository.create(banPunishment);
        } finally {
            this.writeLock.unlock();
        }
    }

    public BanPunishment unban(@NonNull UUID targetId, UUID pardonerId, String reason) {
        this.writeLock.lock();
        try {
            var latestUnpardonedBanPunishment = this.repository.findLatestActiveBanByTargetId(targetId);
            if (latestUnpardonedBanPunishment.isEmpty()) {
                throw new IllegalStateException("target with id " + targetId + " is not banned");
            }

            var updatedUnpardonedBanPunishment = latestUnpardonedBanPunishment.get().toBuilder()
                .pardoned(true).pardonerId(pardonerId).pardonReason(reason).build();
            this.repository.update(updatedUnpardonedBanPunishment);
            return updatedUnpardonedBanPunishment;
        } finally {
            this.writeLock.unlock();
        }
    }

    public List<BanPunishment> listBans(UUID targetId) {
        this.readLock.lock();
        try {
            return this.repository.findByTargetId(targetId);
        } finally {
            this.readLock.unlock();
        }
    }
}
