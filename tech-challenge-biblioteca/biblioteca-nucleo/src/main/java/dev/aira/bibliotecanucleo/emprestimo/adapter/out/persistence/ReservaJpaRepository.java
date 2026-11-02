package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ReservaJpaRepository extends JpaRepository<ReservaJpaEntity, UUID> {
    boolean existsByUsuarioIdAndLivroIdAndStatusIn(
            UUID usuarioId,
            UUID livroId,
            Collection<StatusReserva> status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReservaJpaEntity> findFirstByLivroIdAndStatusOrderByDataReservaAsc(
            UUID livroId,
            StatusReserva status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select reserva from ReservaJpaEntity reserva where reserva.id = :id")
    Optional<ReservaJpaEntity> buscarPorIdComBloqueio(UUID id);
}
