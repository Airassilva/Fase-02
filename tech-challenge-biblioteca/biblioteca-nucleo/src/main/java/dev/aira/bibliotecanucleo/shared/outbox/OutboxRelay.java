package dev.aira.bibliotecanucleo.shared.outbox;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
class OutboxRelay {

    private final OutboxEventRepository outbox;
    private final KafkaTemplate<String, String> kafka;

    OutboxRelay(OutboxEventRepository outbox, KafkaTemplate<String, String> kafka) {
        this.outbox = outbox;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelayString = "${app.outbox.intervalo-ms:5000}")
    @Transactional
    void publicarPendentes() throws InterruptedException {
        for (var evento : outbox.buscarPendentes(20)) {
            try {
                kafka.send(evento.getTopico(), evento.getChave(), evento.getPayload())
                        .get(10, TimeUnit.SECONDS);
                evento.marcarPublicado(Instant.now());
            } catch (ExecutionException | TimeoutException e) {
                log.warn("Falha ao publicar evento {}, tentará de novo", evento.getId(), e);
                break;
            }
        }
    }

    @Scheduled(fixedDelayString = "${app.outbox.limpeza-intervalo-ms:86400000}")
    @Transactional
    void limparPublicados() {
        int removidos = outbox.apagarPublicadosAntesDe(Instant.now().minus(Duration.ofDays(7)));
        if (removidos > 0) {
            log.info("Eventos publicados antigos removidos da outbox: {}", removidos);
        }
    }
}
