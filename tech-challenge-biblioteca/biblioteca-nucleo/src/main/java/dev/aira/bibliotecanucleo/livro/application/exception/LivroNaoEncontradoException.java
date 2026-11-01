package dev.aira.bibliotecanucleo.livro.application.exception;

import java.util.UUID;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(UUID id) {
        super("Livro não encontrado: " + id);
    }
}
