package me.maxallgaier.playermanagement.punishment.ban;

import lombok.Getter;

public class BanException extends RuntimeException {
    public enum Reason {
        ALREADY_BANNED,
        NOT_BANNED,
    }

    @Getter private final Reason reason;

    public BanException(Reason reason) {
        this.reason = reason;
    }
}
