package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;

public class ReservaPersistenceMapper {
    public ReservaJpaEntity toEntity(Reserva reserva) {
        return new ReservaJpaEntity(
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getLivroId(),
                reserva.getDataReserva(),
                reserva.getStatus()
        );
    }

    public Reserva toDomain(ReservaJpaEntity entity) {
        return Reserva.reconstituir(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getLivroId(),
                entity.getDataReserva(),
                entity.getStatus()
        );
    }
}
