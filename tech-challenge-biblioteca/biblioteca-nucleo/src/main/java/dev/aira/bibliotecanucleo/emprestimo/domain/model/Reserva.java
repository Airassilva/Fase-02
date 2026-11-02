package dev.aira.bibliotecanucleo.emprestimo.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Reserva {

    private UUID id;
    private final UUID usuarioId;
    private final UUID livroId;
    private final Instant dataReserva;
    private StatusReserva status;

    private Reserva(UUID id, UUID usuarioId, UUID livroId, Instant dataReserva,
                    StatusReserva status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
        this.status = status;
    }

    public static Reserva registrar(UUID usuarioId, UUID livroId, Instant dataReserva) {
        if (usuarioId == null || livroId == null || dataReserva == null) {
            throw new IllegalArgumentException("Usuário, livro e data da reserva são obrigatórios");
        }
        return new Reserva(null, usuarioId, livroId, dataReserva, StatusReserva.ATIVA);
    }

    public static Reserva reconstituir(UUID id, UUID usuarioId, UUID livroId,
                                       Instant dataReserva, StatusReserva status) {
        return new Reserva(id, usuarioId, livroId, dataReserva, status);
    }

    public void marcarDisponivel() {
        exigirStatus(StatusReserva.ATIVA, "ficar disponível");
        this.status = StatusReserva.DISPONIVEL;
    }

    public void atender() {
        exigirStatus(StatusReserva.DISPONIVEL, "ser atendida");
        this.status = StatusReserva.ATENDIDA;
    }

    public void cancelar() {
        if (status == StatusReserva.ATENDIDA || status == StatusReserva.CANCELADA) {
            throw new IllegalStateException("Uma reserva " + status + " não pode ser cancelada");
        }
        this.status = StatusReserva.CANCELADA;
    }

    private void exigirStatus(StatusReserva esperado, String acao) {
        if (status != esperado) {
            throw new IllegalStateException(
                    "A reserva precisa estar " + esperado + " para " + acao + " (atual: " + status + ")");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getLivroId() {
        return livroId;
    }

    public Instant getDataReserva() {
        return dataReserva;
    }

    public StatusReserva getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Reserva outra)) {
            return false;
        }
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Reserva.class.hashCode();
    }
}
