package dev.aira.bibliotecanucleo.livro.application.exception;

public class LivroJaCadastradoException extends RuntimeException {
    public LivroJaCadastradoException(String isbn) {
        super("Já existe um livro cadastrado com o ISBN " + isbn);
    }
}
