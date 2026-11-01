package dev.aira.bibliotecanucleo.livro.domain.model;

import java.util.UUID;

public class LivroIndisponivelException extends RuntimeException {
    public LivroIndisponivelException(UUID id) {
        super("Livro com ID " + id + " está indisponível.");
    }
}
