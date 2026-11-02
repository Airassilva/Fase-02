package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class EmprestimoNaoPertenceAoUsuarioException extends RuntimeException {
    public EmprestimoNaoPertenceAoUsuarioException(UUID emprestimoId, UUID usuarioId) {
        super("O empréstimo " + emprestimoId + " não pertence ao usuário " + usuarioId);
    }
}
