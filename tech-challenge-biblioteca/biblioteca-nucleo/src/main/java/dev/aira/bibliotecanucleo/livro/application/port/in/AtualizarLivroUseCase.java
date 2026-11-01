package dev.aira.bibliotecanucleo.livro.application.port.in;

import dev.aira.bibliotecanucleo.livro.domain.model.Livro;

import java.util.UUID;

public interface AtualizarLivroUseCase {
    Livro atualizarLivro(UUID id, Integer quantidadeTotal, String descricao);
}
