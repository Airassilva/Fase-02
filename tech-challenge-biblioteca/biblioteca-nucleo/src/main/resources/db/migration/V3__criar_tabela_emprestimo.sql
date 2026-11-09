CREATE TABLE emprestimo
(
    id                      UUID        PRIMARY KEY,
    usuario_id              UUID        NOT NULL REFERENCES usuario (id),
    livro_id                UUID        NOT NULL REFERENCES livros (id),
    data_emprestimo         DATE        NOT NULL,
    data_prevista_devolucao DATE        NOT NULL,
    data_devolucao          DATE,
    status                  VARCHAR(20) NOT NULL,
    versao                  BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT ck_emprestimo_status
        CHECK (status IN ('ATIVO', 'DEVOLVIDO', 'ATRASADO')),
    -- só quem foi devolvido tem data de devolução, e vice-versa
    CONSTRAINT ck_emprestimo_devolucao
        CHECK ((status = 'DEVOLVIDO') = (data_devolucao IS NOT NULL)),
    CONSTRAINT ck_emprestimo_datas
        CHECK (data_prevista_devolucao >= data_emprestimo)
);

CREATE INDEX ix_emprestimo_usuario_data ON emprestimo (usuario_id, data_emprestimo DESC);
CREATE INDEX ix_emprestimo_ativos ON emprestimo (usuario_id) WHERE data_devolucao IS NULL;
CREATE INDEX ix_emprestimo_livro ON emprestimo (livro_id);