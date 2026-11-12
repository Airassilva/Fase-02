package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmprestimoJpaRepository extends JpaRepository<EmprestimoJpaEntity, UUID> {

    @Query("""
            select emprestimo
            from EmprestimoJpaEntity emprestimo
            where emprestimo.status = :status
              and emprestimo.dataDevolucao is null
              and emprestimo.dataPrevistaDevolucao < :hoje
              and (emprestimo.ultimaNotificacaoEm is null
                   or emprestimo.ultimaNotificacaoEm <= :notificadoAntes)
            """)
    List<EmprestimoJpaEntity> findAtivosAtrasados(
            @Param("hoje") LocalDate hoje,
            @Param("notificadoAntes") Instant notificadoAntes,
            @Param("status") StatusEmprestimo status
    );

    @Modifying
    @Transactional
    @Query("""
            update EmprestimoJpaEntity emprestimo
            set emprestimo.ultimaNotificacaoEm = :notificadoEm,
                emprestimo.versao = emprestimo.versao + 1
            where emprestimo.id = :emprestimoId
            """)
    int registrarNotificacaoAtraso(
            @Param("emprestimoId") UUID emprestimoId,
            @Param("notificadoEm") Instant notificadoEm
    );

    long countByUsuarioIdAndStatusAndDataDevolucaoIsNull(
            UUID usuarioId,
            StatusEmprestimo status
    );

    boolean existsByUsuarioIdAndLivroIdAndStatusAndDataDevolucaoIsNull(
            UUID usuarioId,
            UUID livroId,
            StatusEmprestimo status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select emprestimo from EmprestimoJpaEntity emprestimo where emprestimo.id = :id")
    Optional<EmprestimoJpaEntity> buscarPorIdComBloqueio(UUID id);
}
