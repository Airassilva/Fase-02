CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_livros_titulo_trgm ON livros USING GIN (lower(titulo) gin_trgm_ops);
CREATE INDEX idx_livros_autor_trgm ON livros USING GIN (lower(autor) gin_trgm_ops);
CREATE INDEX idx_livros_isbn_trgm ON livros USING GIN (lower(isbn) gin_trgm_ops);
CREATE INDEX idx_usuario_nome_trgm ON usuario USING GIN (lower(nome) gin_trgm_ops);
CREATE INDEX idx_usuario_email_trgm ON usuario USING GIN (lower(email) gin_trgm_ops);
