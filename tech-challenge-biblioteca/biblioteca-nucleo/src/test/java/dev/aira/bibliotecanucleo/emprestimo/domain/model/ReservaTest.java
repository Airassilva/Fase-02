package dev.aira.bibliotecanucleo.emprestimo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ReservaTest {

    @Test
    void reservasPersistidasComMesmoIdSaoIguais() {
        UUID id = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();
        Instant dataReserva = Instant.parse("2026-11-02T12:00:00Z");
        Reserva primeira = Reserva.reconstituir(id, usuarioId, livroId, dataReserva, StatusReserva.ATIVA);
        Reserva segunda = Reserva.reconstituir(id, usuarioId, livroId, dataReserva, StatusReserva.ATIVA);

        assertEquals(primeira, segunda);
        assertEquals(primeira.hashCode(), segunda.hashCode());
    }

    @Test
    void reservasSemIdSoSaoIguaisPorReferencia() {
        Reserva primeira = Reserva.registrar(
                UUID.randomUUID(), UUID.randomUUID(), Instant.parse("2026-11-02T12:00:00Z"));
        Reserva segunda = Reserva.registrar(
                UUID.randomUUID(), UUID.randomUUID(), Instant.parse("2026-11-02T12:00:00Z"));

        assertNotEquals(primeira, segunda);
    }
}
