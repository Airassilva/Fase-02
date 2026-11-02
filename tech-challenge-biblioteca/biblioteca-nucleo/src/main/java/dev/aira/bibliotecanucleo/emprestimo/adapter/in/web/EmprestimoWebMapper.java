package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web;

import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoRequest;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.ReservaResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.SolicitacaoLivroResponse;
import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.model.ReservaDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.model.SolicitacaoLivroDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.EmprestarLivroUseCase.CadastrarEmprestimoCommand;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.ReservarLivroUseCase.ReservarLivroCommand;

public class EmprestimoWebMapper {

    public CadastrarEmprestimoCommand toCommand(EmprestimoRequest request) {
        return new CadastrarEmprestimoCommand(
                request.livroId(),
                request.usuarioId()
        );
    }

    public ReservarLivroCommand toCommandReserva(EmprestimoRequest request) {
        return new ReservarLivroCommand(
                request.livroId(),
                request.usuarioId()
        );
    }

    public EmprestimoResponse toResponse(EmprestimoDetalhe emprestimo) {
        return toEmprestimoResponse(emprestimo);
    }

    public SolicitacaoLivroResponse toResponse(SolicitacaoLivroDetalhe solicitacao) {
        return new SolicitacaoLivroResponse(
                solicitacao.tipo(),
                solicitacao.emprestimo() == null ? null : toEmprestimoResponse(solicitacao.emprestimo()),
                solicitacao.reserva() == null ? null : toReservaResponse(solicitacao.reserva())
        );
    }

    private EmprestimoResponse toEmprestimoResponse(EmprestimoDetalhe emprestimo) {
        return new EmprestimoResponse(
                emprestimo.id(),
                emprestimo.usuarioId(),
                emprestimo.livroId(),
                emprestimo.nomeLivro(),
                emprestimo.nomeUsuario(),
                emprestimo.dataEmprestimo(),
                emprestimo.dataPrevistaDevolucao(),
                emprestimo.status()
        );
    }

    private ReservaResponse toReservaResponse(ReservaDetalhe reserva) {
        return new ReservaResponse(
                reserva.id(),
                reserva.usuarioId(),
                reserva.livroId(),
                reserva.nomeLivro(),
                reserva.nomeUsuario(),
                reserva.dataReserva(),
                reserva.status()
        );
    }
}
