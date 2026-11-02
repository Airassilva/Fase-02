package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class ReservaJaAbertaException extends RuntimeException {
    public ReservaJaAbertaException(UUID usuarioId, UUID livroId) {
        super("O usuário " + usuarioId + " já possui uma reserva aberta para o livro " + livroId);
    }
}
