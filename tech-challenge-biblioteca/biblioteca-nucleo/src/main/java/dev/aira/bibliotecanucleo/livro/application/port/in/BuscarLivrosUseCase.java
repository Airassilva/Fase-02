package dev.aira.bibliotecanucleo.livro.application.port.in;

import dev.aira.bibliotecanucleo.livro.domain.model.Livro;

import java.util.List;
import java.util.UUID;

public interface BuscarLivrosUseCase {
    List<Livro> buscarLivros();
    Livro buscarLivroPorId(UUID id);
}
