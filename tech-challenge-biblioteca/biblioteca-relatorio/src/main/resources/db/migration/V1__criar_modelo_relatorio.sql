CREATE TABLE emprestimo_leitura (
    emprestimo_id           UUID         PRIMARY KEY,
    livro_id                UUID         NOT NULL,
    livro_titulo            VARCHAR(255) NOT NULL,
    livro_autor             VARCHAR(150) NOT NULL,
    livro_isbn              VARCHAR(13)  NOT NULL,
    usuario_id              UUID         NOT NULL,
    data_emprestimo         DATE         NOT NULL,
    data_prevista_devolucao DATE         NOT NULL,
    data_devolucao          DATE
);

CREATE INDEX ix_leitura_data_emprestimo ON emprestimo_leitura (data_emprestimo);
CREATE INDEX ix_leitura_data_devolucao ON emprestimo_leitura (data_devolucao)
    WHERE data_devolucao IS NOT NULL;
CREATE INDEX ix_leitura_livro ON emprestimo_leitura (livro_id);
CREATE INDEX ix_leitura_abertos ON emprestimo_leitura (data_prevista_devolucao)
    WHERE data_devolucao IS NULL;

CREATE TABLE evento_processado (
    event_id      UUID        PRIMARY KEY,
    processado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE relatorio_gerado (
    id             UUID        PRIMARY KEY,
    tipo           VARCHAR(30) NOT NULL,
    periodo_inicio DATE        NOT NULL,
    periodo_fim    DATE        NOT NULL,
    gerado_em      TIMESTAMPTZ NOT NULL,
    payload        TEXT        NOT NULL,
    CONSTRAINT uk_relatorio_tipo_periodo UNIQUE (tipo, periodo_inicio)
);
