CREATE TABLE emprestimo
(
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario (id),
    livro_id UUID NOT NULL REFERENCES livro (id),
    data_emprestimo         DATE        NOT NULL,
    data_prevista_devolucao DATE        NOT NULL,
    data_devolucao          DATE,
    status                  VARCHAR(20) NOT NULL,
    versao                  BIGINT      NOT NULL DEFAULT 0
);

CREATE INDEX ix_emprestimo_usuario_data ON emprestimo (usuario_id, data_emprestimo DESC);

CREATE INDEX ix_emprestimo_ativos ON emprestimo (usuario_id) WHERE data_devolucao IS NULL;