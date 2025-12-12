package me.maxallgaier.playermanagement.punishment.mute;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.NonNull;
import me.maxallgaier.playermanagement.punishment.DurationalPunishment;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder(toBuilder = true)
public record MutePunishment(
    UUID id, @NonNull UUID targetId, UUID issuerId, String reason, @NonNull OffsetDateTime issuedDateTime,
    Duration duration, boolean pardoned, UUID pardonerId, String pardonReason
) implements DurationalPunishment {
    public MutePunishment {
        Preconditions.checkArgument(duration == null || !duration.isNegative(), "duration can not be negative");
        issuedDateTime = issuedDateTime.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }
}
