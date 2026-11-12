package dev.aira.bibliotecarelatorio.relatorio.application.service;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.EventoEmprestimoCommand;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ProcessarEventoEmprestimoUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.TipoEvento;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EmprestimoLeituraRepositoryPort;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EventoProcessadoPort;
import dev.aira.bibliotecarelatorio.relatorio.domain.EmprestimoLeitura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProcessamentoEventoService implements ProcessarEventoEmprestimoUseCase {

    private final EventoProcessadoPort eventoProcessado;
    private final EmprestimoLeituraRepositoryPort emprestimos;

    ProcessamentoEventoService(
            EventoProcessadoPort eventoProcessado,
            EmprestimoLeituraRepositoryPort emprestimos) {
        this.eventoProcessado = eventoProcessado;
        this.emprestimos = emprestimos;
    }

    @Override
    @Transactional
    public void processar(EventoEmprestimoCommand evento) {
        if (!eventoProcessado.registrarSeNovo(evento.eventId())) {
            return;
        }

        EmprestimoLeitura emprestimo = emprestimos.buscarPorId(evento.emprestimoId())
                .orElseGet(() -> EmprestimoLeitura.registrar(
                        evento.emprestimoId(),
                        evento.livroId(),
                        evento.livroTitulo(),
                        evento.livroAutor(),
                        evento.livroIsbn(),
                        evento.usuarioId(),
                        evento.dataEmprestimo(),
                        evento.dataPrevistaDevolucao(),
                        evento.tipo() == TipoEvento.LIVRO_DEVOLVIDO ? null : evento.dataDevolucao()));

        if (evento.tipo() == TipoEvento.LIVRO_DEVOLVIDO) {
            emprestimo.registrarDevolucao(evento.dataDevolucao());
        }
        emprestimos.salvar(emprestimo);
    }
}
