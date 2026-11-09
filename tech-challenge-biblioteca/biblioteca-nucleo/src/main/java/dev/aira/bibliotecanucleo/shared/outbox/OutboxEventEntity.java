package dev.aira.bibliotecanucleo.shared.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String topico;

    @Column(nullable = false, length = 100)
    private String chave;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, updatable = false)
    private Instant criadoEm;

    private Instant publicadoEm;

    @Transient
    private boolean novo = true;

    public OutboxEventEntity(UUID id, UUID aggregateId, String tipo,
                             String topico, String chave, String payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.tipo = tipo;
        this.topico = topico;
        this.chave = chave;
        this.payload = payload;
        this.criadoEm = Instant.now();
    }

    public void marcarPublicado(Instant quando) {
        this.publicadoEm = quando;
    }

    @Override
    public boolean isNew() {
        return novo;
    }

    @PostPersist
    @PostLoad
    void marcarComoNaoNovo() {
        this.novo = false;
    }
}
