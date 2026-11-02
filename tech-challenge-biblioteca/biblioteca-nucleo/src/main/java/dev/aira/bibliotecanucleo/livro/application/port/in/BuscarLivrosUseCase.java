package dev.aira.bibliotecanucleo.livro.application.port.in;

import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;

import java.util.UUID;

public interface BuscarLivrosUseCase {
    PageResult<Livro> buscarLivros(LivroFiltro filtro, PageQuery pageQuery);
    Livro buscarLivroPorId(UUID id);
}
