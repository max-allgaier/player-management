CREATE TABLE ban_punishments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_id        UUID NOT NULL,
    issuer_id        UUID,
    reason           TEXT,
    issued_date_time TIMESTAMPTZ NOT NULL,
    duration         INTERVAL,
    pardoned         BOOLEAN NOT NULL,
    pardoner_id      UUID,
    pardon_reason    TEXT
);
