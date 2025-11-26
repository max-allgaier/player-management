package me.maxallgaier.playermanagement.punishment;

import java.time.Duration;
import java.util.UUID;

public interface DurationalPunishment extends Punishment {
    Duration duration();

    boolean pardoned();

    UUID pardonerId();

    String pardonReason();

    default boolean isPermanent() {
        return this.duration() == null;
    }
}
