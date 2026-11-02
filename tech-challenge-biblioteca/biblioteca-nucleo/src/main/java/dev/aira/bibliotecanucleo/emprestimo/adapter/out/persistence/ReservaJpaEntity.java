package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reserva")
@Getter
@NoArgsConstructor
public class ReservaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "livro_id", nullable = false)
    private UUID livroId;

    @Column(name = "data_reserva", nullable = false)
    private Instant dataReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    ReservaJpaEntity(UUID id, UUID usuarioId, UUID livroId, Instant dataReserva, StatusReserva status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
        this.status = status;
    }

    void atualizarDe(ReservaJpaEntity origem) {
        this.status = origem.status;
    }
}
