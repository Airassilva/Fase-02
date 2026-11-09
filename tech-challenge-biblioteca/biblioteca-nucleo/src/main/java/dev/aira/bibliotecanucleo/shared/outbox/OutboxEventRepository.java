package dev.aira.bibliotecanucleo.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Query(value = """
            SELECT * FROM outbox_event
            WHERE publicado_em IS NULL
            ORDER BY criado_em
            LIMIT :limite
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEventEntity> buscarPendentes(@Param("limite") int limite);

    @Modifying
    @Transactional
    @Query("DELETE FROM OutboxEventEntity e WHERE e.publicadoEm < :limite")
    int apagarPublicadosAntesDe(@Param("limite") Instant limite);
}
