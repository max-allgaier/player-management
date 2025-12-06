package me.maxallgaier.playermanagement.punishment;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface DurationalPunishment extends Punishment {
    Duration duration();

    boolean pardoned();

    UUID pardonerId();

    String pardonReason();

    default Duration durationTillExpired() {
        if (this.isPermanent()) return null;
        var dateTimeExpired = this.issuedDateTime().plus(this.duration());
        var currentDateTime = OffsetDateTime.now();
        return Duration.between(currentDateTime, dateTimeExpired);
    }

    default boolean isPermanent() {
        return this.duration() == null;
    }
}
