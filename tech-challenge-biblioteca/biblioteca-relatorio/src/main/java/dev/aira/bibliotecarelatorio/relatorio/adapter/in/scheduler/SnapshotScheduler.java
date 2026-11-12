package dev.aira.bibliotecarelatorio.relatorio.adapter.in.scheduler;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.GerarSnapshotsUseCase;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class SnapshotScheduler {

    private final GerarSnapshotsUseCase gerarSnapshots;

    SnapshotScheduler(GerarSnapshotsUseCase gerarSnapshots) {
        this.gerarSnapshots = gerarSnapshots;
    }

    @Scheduled(cron = "${app.relatorio.cron:0 */5 * * * *}")
    @SchedulerLock(name = "gerarSnapshotsRelatorio")
    public void gerarSnapshots() {
        gerarSnapshots.gerar();
    }
}
