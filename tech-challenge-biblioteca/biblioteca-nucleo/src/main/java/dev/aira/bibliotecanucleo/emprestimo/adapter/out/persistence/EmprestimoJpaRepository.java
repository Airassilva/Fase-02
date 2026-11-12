package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmprestimoJpaRepository extends JpaRepository<EmprestimoJpaEntity, UUID> {

    @Query(value = """
            SELECT *
            FROM emprestimo
            WHERE data_prevista_devolucao < CURRENT_DATE
              AND data_devolucao IS NULL
            """, nativeQuery = true)
    List<EmprestimoJpaEntity> findAtivosAtrasados();

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
