package dev.aira.bibliotecanucleo.livro.application.port.out;

import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;

import java.util.List;
import java.util.UUID;

public interface LivroRepositoryPort {
    boolean existePorIsbn(Isbn isbn);
    Livro salvar(Livro livro);
    Livro buscarPorId(UUID id);
    void excluir(UUID id);
    List<Livro> buscarTodos();
}
