package dev.aira.bibliotecanucleo.emprestimo.application.exception;

import java.util.UUID;

public class ReservaDisponivelParaOutroUsuarioException extends RuntimeException {
    public ReservaDisponivelParaOutroUsuarioException(UUID livroId) {
        super("O exemplar está disponível para o próximo usuário da fila de reservas do livro " + livroId);
    }
}
