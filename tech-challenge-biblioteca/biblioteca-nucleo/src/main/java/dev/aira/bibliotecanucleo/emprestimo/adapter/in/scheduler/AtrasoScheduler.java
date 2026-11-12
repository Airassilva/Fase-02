package dev.aira.bibliotecanucleo.emprestimo.adapter.in.scheduler;

import dev.aira.bibliotecanucleo.emprestimo.application.port.in.NotificarEmprestimosAtrasadosUseCase;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AtrasoScheduler {
    private final NotificarEmprestimosAtrasadosUseCase notificarEmprestimosAtrasados;

    public AtrasoScheduler(NotificarEmprestimosAtrasadosUseCase notificarEmprestimosAtrasados) {
        this.notificarEmprestimosAtrasados = notificarEmprestimosAtrasados;
    }

    @Scheduled(cron = "${app.emprestimos.atraso.cron:0 0 9 * * *}", zone = "UTC")
    @SchedulerLock(
            name = "notificar-emprestimos-atrasados",
            lockAtMostFor = "PT1H",
            lockAtLeastFor = "PT1M"
    )
    public void notificarEmprestimosAtrasados() {
        notificarEmprestimosAtrasados.notificarEmprestimosAtrasados();
    }
}
