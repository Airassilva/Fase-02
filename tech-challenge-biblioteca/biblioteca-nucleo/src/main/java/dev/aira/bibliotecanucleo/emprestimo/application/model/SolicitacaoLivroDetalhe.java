package dev.aira.bibliotecanucleo.emprestimo.application.model;

public record SolicitacaoLivroDetalhe(
        TipoSolicitacaoLivro tipo,
        EmprestimoDetalhe emprestimo,
        ReservaDetalhe reserva
) {
    public static SolicitacaoLivroDetalhe emprestimo(EmprestimoDetalhe detalhe) {
        return new SolicitacaoLivroDetalhe(TipoSolicitacaoLivro.EMPRESTIMO, detalhe, null);
    }

    public static SolicitacaoLivroDetalhe reserva(ReservaDetalhe detalhe) {
        return new SolicitacaoLivroDetalhe(TipoSolicitacaoLivro.RESERVA, null, detalhe);
    }
}
