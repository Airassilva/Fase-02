package dev.aira.bibliotecanucleo.emprestimo.application.port.in;

import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoDetalhe;

import java.util.UUID;

public interface DevolverLivroUseCase {
    EmprestimoDetalhe devolverLivro(UUID emprestimoId, UUID usuarioId);
}
