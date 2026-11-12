package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoAtrasado;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoRepositoryPort;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;

import java.util.List;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDate;

public class EmprestimoPersistenceAdapter implements EmprestimoRepositoryPort {
    private static final List<StatusReserva> STATUS_RESERVA_ABERTA =
            List.of(StatusReserva.ATIVA, StatusReserva.DISPONIVEL);

    private final EmprestimoJpaRepository emprestimoJpaRepository;
    private final ReservaJpaRepository reservaJpaRepository;
    private final EmprestimoPersistenceMapper emprestimoMapper;
    private final ReservaPersistenceMapper reservaMapper;

    public EmprestimoPersistenceAdapter(
            EmprestimoJpaRepository emprestimoJpaRepository,
            ReservaJpaRepository reservaJpaRepository,
            EmprestimoPersistenceMapper emprestimoMapper,
            ReservaPersistenceMapper reservaMapper
    ) {
        this.emprestimoJpaRepository = emprestimoJpaRepository;
        this.reservaJpaRepository = reservaJpaRepository;
        this.emprestimoMapper = emprestimoMapper;
        this.reservaMapper = reservaMapper;
    }

    @Override
    public Emprestimo salvarEmprestimo(Emprestimo emprestimo) {
        var saved = emprestimoJpaRepository.save(emprestimoMapper.toEntity(emprestimo));
        return emprestimoMapper.toDomain(saved);
    }

    @Override
    public Reserva salvarReserva(Reserva reserva) {
        var saved = reservaJpaRepository.save(reservaMapper.toEntity(reserva));
        return reservaMapper.toDomain(saved);
    }

    @Override
    public java.util.Optional<Emprestimo> buscarEmprestimo(UUID emprestimoId) {
        return emprestimoJpaRepository.buscarPorIdComBloqueio(emprestimoId)
                .map(emprestimoMapper::toDomain);
    }

    @Override
    public Emprestimo atualizarEmprestimo(Emprestimo emprestimo) {
        var entity = emprestimoJpaRepository.buscarPorIdComBloqueio(emprestimo.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Empréstimo não encontrado durante a atualização: " + emprestimo.getId()
                ));
        entity.atualizarDe(emprestimoMapper.toEntity(emprestimo));
        return emprestimoMapper.toDomain(emprestimoJpaRepository.save(entity));
    }

    @Override
    public java.util.Optional<Reserva> buscarProximaReservaAtiva(UUID livroId) {
        return reservaJpaRepository.findFirstByLivroIdAndStatusOrderByDataReservaAsc(
                        livroId,
                        StatusReserva.ATIVA
                )
                .map(reservaMapper::toDomain);
    }

    @Override
    public java.util.Optional<Reserva> buscarReservaDisponivel(UUID livroId) {
        return reservaJpaRepository.findFirstByLivroIdAndStatusOrderByDataReservaAsc(
                        livroId,
                        StatusReserva.DISPONIVEL
                )
                .map(reservaMapper::toDomain);
    }

    @Override
    public Reserva atualizarReserva(Reserva reserva) {
        var entity = reservaJpaRepository.buscarPorIdComBloqueio(reserva.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Reserva não encontrada durante a atualização: " + reserva.getId()
                ));
        entity.atualizarDe(reservaMapper.toEntity(reserva));
        return reservaMapper.toDomain(reservaJpaRepository.save(entity));
    }

    @Override
    public long contarEmprestimosAtivos(UUID usuarioId) {
        return emprestimoJpaRepository.countByUsuarioIdAndStatusAndDataDevolucaoIsNull(
                usuarioId,
                StatusEmprestimo.ATIVO
        );
    }

    @Override
    public boolean existeEmprestimoAtivo(UUID usuarioId, UUID livroId) {
        return emprestimoJpaRepository.existsByUsuarioIdAndLivroIdAndStatusAndDataDevolucaoIsNull(
                usuarioId,
                livroId,
                StatusEmprestimo.ATIVO
        );
    }

    @Override
    public boolean existeReservaAberta(UUID usuarioId, UUID livroId) {
        return reservaJpaRepository.existsByUsuarioIdAndLivroIdAndStatusIn(
                usuarioId,
                livroId,
                STATUS_RESERVA_ABERTA
        );
    }

    @Override
    public List<EmprestimoAtrasado> buscarAtrasados(LocalDate hoje, Instant notificadoAntes) {
        return emprestimoJpaRepository.findAtivosAtrasados(
                        hoje,
                        notificadoAntes,
                        StatusEmprestimo.ATIVO
                )
                .stream()
                .map(emprestimo -> new EmprestimoAtrasado(
                        emprestimo.getId(),
                        emprestimo.getUsuarioId(),
                        emprestimo.getLivroId(),
                        emprestimo.getDataPrevistaDevolucao()
                ))
                .toList();
    }

    @Override
    public void registrarNotificacaoAtraso(UUID emprestimoId, Instant notificadoEm) {
        int atualizados = emprestimoJpaRepository.registrarNotificacaoAtraso(emprestimoId, notificadoEm);
        if (atualizados != 1) {
            throw new IllegalStateException("Empréstimo não encontrado ao registrar notificação: " + emprestimoId);
        }
    }
}
