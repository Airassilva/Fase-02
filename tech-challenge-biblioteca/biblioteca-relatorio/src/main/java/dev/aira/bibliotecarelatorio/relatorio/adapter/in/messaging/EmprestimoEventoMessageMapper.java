package dev.aira.bibliotecarelatorio.relatorio.adapter.in.messaging;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.EventoEmprestimoCommand;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.TipoEvento;

final class EmprestimoEventoMessageMapper {

    private EmprestimoEventoMessageMapper() {
    }

    static EventoEmprestimoCommand paraCommand(EmprestimoEventoMessage message) {
        TipoEvento tipo;
        try {
            tipo = TipoEvento.valueOf(message.tipo());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("O tipo de evento de empréstimo é inválido", e);
        }

        return new EventoEmprestimoCommand(
                message.eventId(),
                tipo,
                message.ocorridoEm(),
                message.emprestimoId(),
                message.usuarioId(),
                message.livroId(),
                message.livroTitulo(),
                message.livroAutor(),
                message.livroIsbn(),
                message.dataEmprestimo(),
                message.dataPrevistaDevolucao(),
                message.dataDevolucao());
    }
}
