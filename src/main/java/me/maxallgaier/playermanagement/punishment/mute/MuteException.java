package me.maxallgaier.playermanagement.punishment.mute;

import lombok.Getter;

public class MuteException extends RuntimeException {
    public enum Reason {
        ALREADY_MUTED,
        NOT_MUTED,
    }

    @Getter private final Reason reason;

    public MuteException(Reason reason) {
        this.reason = reason;
    }
}
