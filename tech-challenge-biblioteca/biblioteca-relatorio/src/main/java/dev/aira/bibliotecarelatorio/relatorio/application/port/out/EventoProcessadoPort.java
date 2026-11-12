package dev.aira.bibliotecarelatorio.relatorio.application.port.out;

import java.util.UUID;

public interface EventoProcessadoPort {
    boolean registrarSeNovo(UUID eventId);
}
