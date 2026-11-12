package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;
import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoAtrasado;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface EmprestimoRepositoryPort {
    Emprestimo salvarEmprestimo(Emprestimo emprestimo);

    Reserva salvarReserva(Reserva reserva);

    Optional<Emprestimo> buscarEmprestimo(UUID emprestimoId);

    Emprestimo atualizarEmprestimo(Emprestimo emprestimo);

    Optional<Reserva> buscarProximaReservaAtiva(UUID livroId);

    Optional<Reserva> buscarReservaDisponivel(UUID livroId);

    Reserva atualizarReserva(Reserva reserva);

    long contarEmprestimosAtivos(UUID usuarioId);

    boolean existeEmprestimoAtivo(UUID usuarioId, UUID livroId);

    boolean existeReservaAberta(UUID usuarioId, UUID livroId);

    List<EmprestimoAtrasado> buscarAtrasados(LocalDate hoje, Instant notificadoAntes);

    void registrarNotificacaoAtraso(UUID emprestimoId, Instant notificadoEm);
}
