package dev.aira.bibliotecarelatorio.relatorio.application.query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SnapshotGeradoConsulta(
        UUID id,
        String tipo,
        LocalDate periodoInicio,
        LocalDate periodoFim,
        Instant geradoEm,
        String payload) {
}
