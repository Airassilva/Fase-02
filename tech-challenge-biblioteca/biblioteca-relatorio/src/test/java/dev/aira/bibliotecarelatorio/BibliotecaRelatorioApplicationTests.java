package dev.aira.bibliotecarelatorio;

import org.junit.jupiter.api.Test;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.messaging.EmprestimoEventoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public class BibliotecaRelatorioApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private tools.jackson.databind.json.JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void consomeEventosEConsultaRelatoriosComEmprestimosNasExtremidadesDaSemana() throws Exception {
        LocalDate segundaSemanaAnterior = LocalDate.now(ZoneId.of("America/Recife"))
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .minusWeeks(1);
        LocalDate domingoSemanaAnterior = segundaSemanaAnterior.plusDays(6);
        UUID primeiroEmprestimo = UUID.randomUUID();
        UUID segundoEmprestimo = UUID.randomUUID();

        publicarEvento(primeiroEmprestimo, segundaSemanaAnterior);
        publicarEvento(segundoEmprestimo, domingoSemanaAnterior);
        aguardarProcessamento(primeiroEmprestimo, segundoEmprestimo);

        mockMvc.perform(get("/api/v1/relatorios/semanal")
                        .param("referencia", domingoSemanaAnterior.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo.inicio").value(segundaSemanaAnterior.toString()))
                .andExpect(jsonPath("$.periodo.fim").value(domingoSemanaAnterior.toString()))
                .andExpect(jsonPath("$.totalEmprestimos").value(2))
                .andExpect(jsonPath("$.porDia[0].total").value(1))
                .andExpect(jsonPath("$.porDia[6].total").value(1));

        mockMvc.perform(get("/api/v1/relatorios/emprestimos-ativos")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].usuarioId").doesNotExist());
    }

    private void publicarEvento(UUID emprestimoId, LocalDate dataEmprestimo) throws Exception {
        EmprestimoEventoMessage evento = new EmprestimoEventoMessage(
                UUID.randomUUID(),
                "EMPRESTIMO_REALIZADO",
                Instant.now(),
                emprestimoId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Livro de integração",
                "Autoria de integração",
                "9781234567890",
                dataEmprestimo,
                dataEmprestimo.plusDays(14),
                null);
        kafkaTemplate.send(
                        "biblioteca.emprestimos",
                        emprestimoId.toString(),
                        jsonMapper.writeValueAsString(evento))
                .get(10, TimeUnit.SECONDS);
    }

    private void aguardarProcessamento(UUID primeiroEmprestimo, UUID segundoEmprestimo) {
        long limite = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (System.nanoTime() < limite) {
            Integer processados = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT(*) FROM emprestimo_leitura
                            WHERE emprestimo_id IN (?, ?)
                            """,
                    Integer.class,
                    primeiroEmprestimo,
                    segundoEmprestimo);
            if (processados != null && processados == 2) {
                return;
            }
            LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
        }
        fail("Os eventos Kafka não atualizaram o modelo de leitura dentro do prazo");
    }
}
