package dev.aira.bibliotecanucleo.livro.application.port.out;

import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;

import java.util.UUID;

public interface LivroRepositoryPort {
    boolean existePorIsbn(Isbn isbn);
    Livro salvar(Livro livro);
    Livro buscarPorId(UUID id);
    void excluir(UUID id);
    PageResult<Livro> buscar(LivroFiltro filtro, PageQuery pageQuery);
}
