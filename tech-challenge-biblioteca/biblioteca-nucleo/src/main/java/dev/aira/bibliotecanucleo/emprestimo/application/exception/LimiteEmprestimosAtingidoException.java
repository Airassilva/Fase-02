package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class LimiteEmprestimosAtingidoException extends RuntimeException {
    public LimiteEmprestimosAtingidoException(UUID usuarioId, int limite) {
        super("O usuário " + usuarioId + " atingiu o limite de " + limite + " empréstimos ativos");
    }
}
