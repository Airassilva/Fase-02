package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence;

import dev.aira.bibliotecarelatorio.relatorio.application.port.out.SnapshotRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.SnapshotGeradoConsulta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
class SnapshotRelatorioPersistenceAdapter implements SnapshotRelatorioPort {

    private final JdbcTemplate jdbcTemplate;
    private final JsonMapper jsonMapper;

    SnapshotRelatorioPersistenceAdapter(JdbcTemplate jdbcTemplate, JsonMapper jsonMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void salvarOuAtualizar(String tipo, RelatorioSemanalConsulta relatorio) {
        String payload;
        try {
            payload = jsonMapper.writeValueAsString(relatorio);
        } catch (JacksonException e) {
            throw new IllegalStateException("Não foi possível serializar o relatório semanal", e);
        }

        jdbcTemplate.update(
                """
                        INSERT INTO relatorio_gerado
                            (id, tipo, periodo_inicio, periodo_fim, gerado_em, payload)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT (tipo, periodo_inicio) DO UPDATE SET
                            periodo_fim = EXCLUDED.periodo_fim,
                            gerado_em = EXCLUDED.gerado_em,
                            payload = EXCLUDED.payload
                        """,
                UUID.randomUUID(),
                tipo,
                relatorio.periodo().inicio(),
                relatorio.periodo().fim(),
                relatorio.geradoEm(),
                payload);
    }

    @Override
    public Optional<SnapshotGeradoConsulta> buscar(String tipo, LocalDate periodoInicio) {
        return jdbcTemplate.query(
                        """
                                SELECT id, tipo, periodo_inicio, periodo_fim, gerado_em, payload
                                FROM relatorio_gerado
                                WHERE tipo = ? AND periodo_inicio = ?
                                """,
                        (rs, rowNum) -> new SnapshotGeradoConsulta(
                                rs.getObject("id", UUID.class),
                                rs.getString("tipo"),
                                rs.getObject("periodo_inicio", LocalDate.class),
                                rs.getObject("periodo_fim", LocalDate.class),
                                rs.getTimestamp("gerado_em").toInstant(),
                                rs.getString("payload")),
                        tipo,
                        periodoInicio)
                .stream()
                .findFirst();
    }
}
