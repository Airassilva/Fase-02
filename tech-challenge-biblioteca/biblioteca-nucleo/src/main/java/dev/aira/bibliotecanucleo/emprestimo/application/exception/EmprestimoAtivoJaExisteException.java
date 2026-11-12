package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class EmprestimoAtivoJaExisteException extends RuntimeException {
    public EmprestimoAtivoJaExisteException(UUID usuarioId, UUID livroId) {
        super("O usuário " + usuarioId + " já possui um empréstimo ativo para o livro " + livroId);
    }
}
