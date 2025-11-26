package me.maxallgaier.playermanagement.punishment;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface Punishment {
    UUID id();

    UUID targetId();

    UUID issuerId();

    String reason();

    OffsetDateTime issuedDateTime();
}
