package dev.aira.bibliotecarelatorio.relatorio.adapter.in.messaging;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmprestimoEventoMessageTest {

    @Test
    void aceitaEventoDoNucleoComCampoFuturoDesconhecido() {
        UUID eventId = UUID.fromString("7f1c2d3e-8a9b-4c5d-9e0f-1a2b3c4d5e6f");
        String json = """
                {
                  "eventId": "%s",
                  "tipo": "EMPRESTIMO_REALIZADO",
                  "ocorridoEm": "2026-11-08T14:30:00Z",
                  "emprestimoId": "b2c1d2e3-4f5a-6789-abcd-ef0123456789",
                  "usuarioId": "9a8f2d3e-4c5b-6a7d-8e9f-0123456789ab",
                  "livroId": "4d3e2f1a-8b7c-6d5e-4f3a-2b1c0d9e8f7a",
                  "livroTitulo": "Clean Architecture",
                  "livroAutor": "Robert C. Martin",
                  "livroIsbn": "9780134494166",
                  "dataEmprestimo": "2026-11-08",
                  "dataPrevistaDevolucao": "2026-11-22",
                  "dataDevolucao": null,
                  "campoAdicionadoNoFuturo": "compatível"
                }
                """.formatted(eventId);
        JsonMapper jsonMapper = JsonMapper.builder().build();

        EmprestimoEventoMessage evento = jsonMapper.readValue(json, EmprestimoEventoMessage.class);

        assertEquals(eventId, evento.eventId());
        assertEquals(LocalDate.of(2026, 11, 8), evento.dataEmprestimo());
        assertEquals("Clean Architecture", evento.livroTitulo());
    }
}
