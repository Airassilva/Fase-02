package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class EmprestimoNaoEncontradoException extends RuntimeException {
    public EmprestimoNaoEncontradoException(UUID emprestimoId) {
        super("Empréstimo não encontrado: " + emprestimoId);
    }
}
