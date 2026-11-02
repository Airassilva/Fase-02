package dev.aira.bibliotecanucleo.emprestimo.application.port.in;

import dev.aira.bibliotecanucleo.emprestimo.application.model.SolicitacaoLivroDetalhe;

public interface EmprestarLivroUseCase {
    SolicitacaoLivroDetalhe emprestarLivro(CadastrarEmprestimoCommand command);

    record CadastrarEmprestimoCommand(
            java.util.UUID livroId,
            java.util.UUID usuarioId
    ) {}
}
