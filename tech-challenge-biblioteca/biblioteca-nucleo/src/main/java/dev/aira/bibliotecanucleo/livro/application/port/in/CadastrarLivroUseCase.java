package dev.aira.bibliotecanucleo.livro.application.port.in;

import dev.aira.bibliotecanucleo.livro.domain.model.Livro;

public interface CadastrarLivroUseCase {

    Livro cadastrarLivro(CadastrarLivroCommand command);

    record CadastrarLivroCommand(
            String titulo,
            String autor,
            String isbn,
            int quantidadeTotal,
            String descricao) {
    }
}