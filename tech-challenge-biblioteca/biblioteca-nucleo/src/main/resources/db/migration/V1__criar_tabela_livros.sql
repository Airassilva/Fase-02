CREATE TABLE livro
(
    id UUID PRIMARY KEY,
    titulo                VARCHAR(255) NOT NULL,
    autor                 VARCHAR(255) NOT NULL,
    isbn                  VARCHAR(13)  NOT NULL UNIQUE,
    quantidade_total      INTEGER      NOT NULL CHECK (quantidade_total > 0),
    quantidade_disponivel INTEGER      NOT NULL CHECK (
        quantidade_disponivel >= 0 AND quantidade_disponivel <= quantidade_total
        )
);

CREATE INDEX idx_livro_titulo ON livro (titulo);
CREATE INDEX idx_livro_autor ON livro (autor);
