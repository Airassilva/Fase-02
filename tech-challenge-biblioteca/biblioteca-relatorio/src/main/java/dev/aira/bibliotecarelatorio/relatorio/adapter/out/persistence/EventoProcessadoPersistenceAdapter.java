package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence;

import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EventoProcessadoPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class EventoProcessadoPersistenceAdapter implements EventoProcessadoPort {

    private final JdbcTemplate jdbcTemplate;

    EventoProcessadoPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean registrarSeNovo(UUID eventId) {
        int inseridos = jdbcTemplate.update(
                "INSERT INTO evento_processado (event_id) VALUES (?) ON CONFLICT (event_id) DO NOTHING",
                eventId);
        return inseridos == 1;
    }
}
