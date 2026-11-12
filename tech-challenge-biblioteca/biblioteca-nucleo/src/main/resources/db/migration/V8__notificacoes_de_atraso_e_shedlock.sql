ALTER TABLE emprestimo
    ADD COLUMN ultima_notificacao_em TIMESTAMP WITH TIME ZONE;

CREATE INDEX ix_emprestimo_atrasados
    ON emprestimo (data_prevista_devolucao)
    WHERE data_devolucao IS NULL AND status = 'ATIVO';

CREATE TABLE shedlock
(
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at  TIMESTAMP(3) NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
