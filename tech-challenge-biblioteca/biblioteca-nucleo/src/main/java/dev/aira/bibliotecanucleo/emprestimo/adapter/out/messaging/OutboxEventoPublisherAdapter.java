package dev.aira.bibliotecanucleo.emprestimo.adapter.out.messaging;

import dev.aira.bibliotecanucleo.shared.outbox.OutboxEventRepository;
import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoEvento;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EventoEmprestimoPublisherPort;
import dev.aira.bibliotecanucleo.shared.outbox.OutboxEventEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

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
        } catch (JacksonException e) {
            throw new IllegalStateException("Não foi possível serializar o evento de empréstimo", e);
        }
    }
}
