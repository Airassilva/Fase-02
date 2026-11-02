CREATE TABLE usuario
(
    id UUID PRIMARY KEY,
    nome   VARCHAR(150) NOT NULL,
    email  VARCHAR(254) NOT NULL,
    tipo   VARCHAR(20)  NOT NULL,
    ativo  BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    versao BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT ck_usuario_tipo CHECK (tipo IN ('ALUNO', 'MEMBRO', 'PROFESSOR'))
);

CREATE UNIQUE INDEX uk_usuario_email ON usuario (lower(email) );