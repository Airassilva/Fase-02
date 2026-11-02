package dev.aira.bibliotecanucleo.livro.domain.exception;

public class IsbnInvalidoException extends RuntimeException {
    public IsbnInvalidoException(String isbn) {
        super("ISBN inválido: " + isbn);
    }
}
