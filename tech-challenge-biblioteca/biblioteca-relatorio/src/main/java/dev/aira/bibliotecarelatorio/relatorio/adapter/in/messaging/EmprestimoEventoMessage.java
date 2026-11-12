package dev.aira.bibliotecarelatorio.relatorio.adapter.in.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmprestimoEventoMessage(
        UUID eventId,
        String tipo,
        Instant ocorridoEm,
        UUID emprestimoId,
        UUID usuarioId,
        UUID livroId,
        String livroTitulo,
        String livroAutor,
        String livroIsbn,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao) {
}
