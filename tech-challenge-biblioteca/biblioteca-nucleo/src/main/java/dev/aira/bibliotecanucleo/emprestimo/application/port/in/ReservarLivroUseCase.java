package dev.aira.bibliotecanucleo.emprestimo.application.port.in;

import dev.aira.bibliotecanucleo.emprestimo.application.model.SolicitacaoLivroDetalhe;

import java.util.UUID;

public interface ReservarLivroUseCase {
    SolicitacaoLivroDetalhe reservarLivro(ReservarLivroCommand command);

    record ReservarLivroCommand(UUID livroId, UUID usuarioId) {
    }
}
