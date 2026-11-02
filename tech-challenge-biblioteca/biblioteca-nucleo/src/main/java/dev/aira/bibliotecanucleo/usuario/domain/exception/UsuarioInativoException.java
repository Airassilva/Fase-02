package dev.aira.bibliotecanucleo.usuario.domain.exception;

import java.util.UUID;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException(UUID id) {
        super("O usuário " + id + " está inativo e não pode realizar empréstimos");
    }
}