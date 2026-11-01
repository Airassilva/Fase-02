package dev.aira.bibliotecanucleo.livro.domain;

public class IsbnInvalidoException extends RuntimeException {
    public IsbnInvalidoException(String isbn) {
        super("ISBN inválido: " + isbn);
    }
}
