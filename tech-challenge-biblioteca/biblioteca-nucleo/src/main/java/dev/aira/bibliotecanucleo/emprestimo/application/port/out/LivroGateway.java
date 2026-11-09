package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import java.util.UUID;

public interface LivroGateway {
    LivroInfo buscarLivro(UUID livroId);

    boolean retirarExemplar(UUID livroId);

    void devolverExemplar(UUID livroId);

    record LivroInfo(UUID id, String titulo, String autor, String isbn) {
    }
}
