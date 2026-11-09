package dev.aira.bibliotecanucleo.emprestimo.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aira.bibliotecanucleo.shared.outbox.OutboxEventRepository;
import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoEvento;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EventoEmprestimoPublisherPort;
import dev.aira.bibliotecanucleo.shared.outbox.OutboxEventEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class OutboxEventoPublisherAdapter implements EventoEmprestimoPublisherPort {

    private final OutboxEventRepository outbox;
    private final ObjectMapper json;

    @Value("${app.kafka.topics.emprestimos}")
    private String topico;

    OutboxEventoPublisherAdapter(OutboxEventRepository outbox, ObjectMapper json) {
        this.outbox = outbox;
        this.json = json;
    }

    @Override
    public void publicar(EmprestimoEvento evento) {
        try {
            outbox.save(new OutboxEventEntity(
                    UUID.randomUUID(),
                    evento.emprestimoId(),
                    evento.tipo().name(),
                    topico,
                    evento.emprestimoId().toString(),
                    json.writeValueAsString(evento)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível serializar o evento de empréstimo", e);
        }
    }
}
