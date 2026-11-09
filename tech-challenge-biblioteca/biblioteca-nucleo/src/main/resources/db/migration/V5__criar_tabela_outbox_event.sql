CREATE TABLE outbox_event
(
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    tipo    VARCHAR(50)  NOT NULL,
    topico  VARCHAR(100) NOT NULL,
    chave   VARCHAR(100) NOT NULL,
    payload TEXT         NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    publicado_em TIMESTAMPTZ
);

CREATE INDEX ix_outbox_pendentes ON outbox_event (criado_em) WHERE publicado_em IS NULL;