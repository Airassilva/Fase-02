package dev.aira.bibliotecanucleo.livro.application.port.in;

import java.util.UUID;

public interface ExcluirLivroUseCase {
    void excluirLivro(UUID id);
}
