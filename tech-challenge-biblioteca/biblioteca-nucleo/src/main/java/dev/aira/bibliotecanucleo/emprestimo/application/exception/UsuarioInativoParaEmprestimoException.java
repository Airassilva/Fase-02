package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class UsuarioInativoParaEmprestimoException extends RuntimeException {
    public UsuarioInativoParaEmprestimoException(UUID usuarioId) {
        super("O usuário está inativo e não pode solicitar livros: " + usuarioId);
    }
}
