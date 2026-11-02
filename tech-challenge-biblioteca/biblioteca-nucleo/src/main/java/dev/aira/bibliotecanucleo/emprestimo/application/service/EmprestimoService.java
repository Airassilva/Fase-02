package dev.aira.bibliotecanucleo.emprestimo.application.service;

import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.model.ReservaDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.model.SolicitacaoLivroDetalhe;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoNaoEncontradoException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoNaoPertenceAoUsuarioException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.LimiteEmprestimosAtingidoException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaDisponivelParaOutroUsuarioException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaJaAbertaException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.UsuarioInativoParaEmprestimoException;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.EmprestarLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.ReservarLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.DevolverLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoRepositoryPort;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoUnitOfWork;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.LivroGateway;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.UsuarioGateway;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.PoliticaPrazo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class EmprestimoService implements EmprestarLivroUseCase, ReservarLivroUseCase, DevolverLivroUseCase {
    private final EmprestimoRepositoryPort emprestimoRepository;
    private final LivroGateway livroGateway;
    private final UsuarioGateway usuarioGateway;
    private final PoliticaPrazo politicaPrazo;
    private final EmprestimoUnitOfWork unitOfWork;
    private final Clock clock;

    public EmprestimoService(
            EmprestimoRepositoryPort emprestimoRepository,
            LivroGateway livroGateway,
            UsuarioGateway usuarioGateway,
            PoliticaPrazo politicaPrazo,
            EmprestimoUnitOfWork unitOfWork,
            Clock clock) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroGateway = livroGateway;
        this.usuarioGateway = usuarioGateway;
        this.politicaPrazo = politicaPrazo;
        this.unitOfWork = unitOfWork;
        this.clock = clock;
    }

    @Override
    public SolicitacaoLivroDetalhe emprestarLivro(CadastrarEmprestimoCommand command) {
        Objects.requireNonNull(command, "A solicitação de empréstimo é obrigatória");
        return solicitar(command.livroId(), command.usuarioId());
    }

    @Override
    public SolicitacaoLivroDetalhe reservarLivro(ReservarLivroCommand command) {
        Objects.requireNonNull(command, "A solicitação de reserva é obrigatória");
        return solicitar(command.livroId(), command.usuarioId());
    }

    @Override
    public EmprestimoDetalhe devolverLivro(java.util.UUID emprestimoId, java.util.UUID usuarioId) {
        Objects.requireNonNull(emprestimoId, "O empréstimo é obrigatório");
        Objects.requireNonNull(usuarioId, "O usuário é obrigatório");
        return unitOfWork.executar(() -> {
            var emprestimo = emprestimoRepository.buscarEmprestimo(emprestimoId)
                    .orElseThrow(() -> new EmprestimoNaoEncontradoException(emprestimoId));
            if (!emprestimo.getUsuarioId().equals(usuarioId)) {
                throw new EmprestimoNaoPertenceAoUsuarioException(emprestimoId, usuarioId);
            }

            var livro = livroGateway.buscarLivro(emprestimo.getLivroId());
            var usuario = usuarioGateway.buscarUsuario(emprestimo.getUsuarioId());
            emprestimo.devolver(LocalDate.now(clock));
            var devolvido = emprestimoRepository.atualizarEmprestimo(emprestimo);
            livroGateway.devolverExemplar(emprestimo.getLivroId());

            emprestimoRepository.buscarProximaReservaAtiva(emprestimo.getLivroId())
                    .ifPresent(reserva -> {
                        reserva.marcarDisponivel();
                        emprestimoRepository.atualizarReserva(reserva);
                    });

            return new EmprestimoDetalhe(
                    devolvido.getId(),
                    devolvido.getUsuarioId(),
                    devolvido.getLivroId(),
                    livro.titulo(),
                    usuario.nome(),
                    devolvido.getDataEmprestimo(),
                    devolvido.getDataPrevistaDevolucao(),
                    devolvido.getStatus()
            );
        });
    }

    private SolicitacaoLivroDetalhe solicitar(java.util.UUID livroId, java.util.UUID usuarioId) {
        Objects.requireNonNull(livroId, "O livro é obrigatório");
        Objects.requireNonNull(usuarioId, "O usuário é obrigatório");
        return unitOfWork.executar(() -> emprestarOuReservar(livroId, usuarioId));
    }

    private SolicitacaoLivroDetalhe emprestarOuReservar(java.util.UUID livroId, java.util.UUID usuarioId) {
        var usuario = usuarioGateway.buscarUsuario(usuarioId);
        if (usuario == null) {
            throw new IllegalStateException("O gateway de usuário retornou um usuário inexistente");
        }
        if (!usuario.ativo()) {
            throw new UsuarioInativoParaEmprestimoException(usuarioId);
        }

        var livro = livroGateway.buscarLivro(livroId);
        if (livro == null) {
            throw new IllegalStateException("O gateway de livro retornou um livro inexistente");
        }

        boolean exemplarRetirado = livroGateway.retirarExemplar(livroId);
        var reservaDisponivel = emprestimoRepository.buscarReservaDisponivel(livroId);
        if (reservaDisponivel.isPresent()
                && !reservaDisponivel.get().getUsuarioId().equals(usuarioId)) {
            throw new ReservaDisponivelParaOutroUsuarioException(livroId);
        }

        if (exemplarRetirado) {
            long emprestimosAtivos = emprestimoRepository.contarEmprestimosAtivos(usuarioId);
            if (emprestimosAtivos >= usuario.limiteEmprestimosAtivos()) {
                throw new LimiteEmprestimosAtingidoException(
                        usuarioId,
                        usuario.limiteEmprestimosAtivos()
                );
            }

            LocalDate hoje = LocalDate.now(clock);
            var emprestimo = Emprestimo.registrar(
                    usuarioId,
                    livroId,
                    hoje,
                    usuario.prazoEmprestimo(),
                    politicaPrazo
            );
            var salvo = emprestimoRepository.salvarEmprestimo(emprestimo);
            reservaDisponivel.ifPresent(reserva -> {
                reserva.atender();
                emprestimoRepository.atualizarReserva(reserva);
            });
            return SolicitacaoLivroDetalhe.emprestimo(new EmprestimoDetalhe(
                    salvo.getId(),
                    salvo.getUsuarioId(),
                    salvo.getLivroId(),
                    livro.titulo(),
                    usuario.nome(),
                    salvo.getDataEmprestimo(),
                    salvo.getDataPrevistaDevolucao(),
                    salvo.getStatus()
            ));
        }

        if (emprestimoRepository.existeReservaAberta(usuarioId, livroId)) {
            throw new ReservaJaAbertaException(usuarioId, livroId);
        }

        Instant agora = clock.instant();
        var reservaSalva = emprestimoRepository.salvarReserva(
                Reserva.registrar(usuarioId, livroId, agora)
        );
        return SolicitacaoLivroDetalhe.reserva(new ReservaDetalhe(
                reservaSalva.getId(),
                reservaSalva.getUsuarioId(),
                reservaSalva.getLivroId(),
                livro.titulo(),
                usuario.nome(),
                reservaSalva.getDataReserva(),
                reservaSalva.getStatus()
        ));
    }
}
