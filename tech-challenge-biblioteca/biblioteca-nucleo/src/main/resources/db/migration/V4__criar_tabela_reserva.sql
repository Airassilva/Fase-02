CREATE TABLE reserva
(
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario (id),
    livro_id UUID NOT NULL REFERENCES livros (id),
    data_reserva TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT ck_reserva_status
        CHECK (status IN ('ATIVA', 'DISPONIVEL', 'ATENDIDA', 'CANCELADA'))
);

CREATE UNIQUE INDEX uk_reserva_aberta ON reserva (usuario_id, livro_id) WHERE status IN ('ATIVA', 'DISPONIVEL');

CREATE INDEX ix_reserva_fila ON reserva (livro_id, data_reserva) WHERE status = 'ATIVA';