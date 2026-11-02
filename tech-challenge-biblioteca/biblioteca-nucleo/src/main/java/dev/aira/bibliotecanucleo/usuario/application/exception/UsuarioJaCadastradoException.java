package dev.aira.bibliotecanucleo.usuario.application.exception;

public class UsuarioJaCadastradoException extends RuntimeException {
    public UsuarioJaCadastradoException(String email) {
        super("Usuário já cadastrado com esse email: " + email);
    }
}
