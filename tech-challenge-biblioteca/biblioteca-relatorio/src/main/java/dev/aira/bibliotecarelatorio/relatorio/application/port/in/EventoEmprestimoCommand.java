package dev.aira.bibliotecarelatorio.relatorio.application.port.in;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EventoEmprestimoCommand(
        UUID eventId,
        TipoEvento tipo,
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

    public EventoEmprestimoCommand {
        if (eventId == null || tipo == null || ocorridoEm == null) {
            throw new IllegalArgumentException("O identificador, o tipo e a data do evento são obrigatórios");
        }
        if (tipo == TipoEvento.LIVRO_DEVOLVIDO && dataDevolucao == null) {
            throw new IllegalArgumentException("A data de devolução é obrigatória para esse evento");
        }
    }
}
