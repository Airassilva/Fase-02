package dev.aira.bibliotecarelatorio.relatorio.adapter.in.messaging;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ProcessarEventoEmprestimoUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
class EmprestimoEventoConsumer {

    private final JsonMapper jsonMapper;
    private final ProcessarEventoEmprestimoUseCase processarEvento;

    EmprestimoEventoConsumer(
            JsonMapper jsonMapper,
            ProcessarEventoEmprestimoUseCase processarEvento) {
        this.jsonMapper = jsonMapper;
        this.processarEvento = processarEvento;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.emprestimos}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(String valor) {
        EmprestimoEventoMessage mensagem;
        try {
            mensagem = jsonMapper.readValue(valor, EmprestimoEventoMessage.class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("A mensagem de empréstimo contém JSON inválido", e);
        }
        processarEvento.processar(EmprestimoEventoMessageMapper.paraCommand(mensagem));
    }
}
