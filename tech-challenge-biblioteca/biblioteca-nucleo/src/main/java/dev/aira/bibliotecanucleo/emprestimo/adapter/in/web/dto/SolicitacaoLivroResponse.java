package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import dev.aira.bibliotecanucleo.emprestimo.application.model.TipoSolicitacaoLivro;

public record SolicitacaoLivroResponse(
        TipoSolicitacaoLivro tipo,
        EmprestimoResponse emprestimo,
        ReservaResponse reserva
) {
}
