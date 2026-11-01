package dev.aira.bibliotecanucleo.livro.domain.model;

public class IsbnInvalidoException extends RuntimeException {
    public IsbnInvalidoException(String isbn) {
        super("ISBN inválido: " + isbn);
    }
}
