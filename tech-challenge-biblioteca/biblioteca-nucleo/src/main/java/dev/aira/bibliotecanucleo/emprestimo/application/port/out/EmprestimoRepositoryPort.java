package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;

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
}
