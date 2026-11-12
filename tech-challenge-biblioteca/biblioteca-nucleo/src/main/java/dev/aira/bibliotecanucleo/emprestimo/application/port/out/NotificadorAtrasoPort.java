package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import java.time.LocalDate;

public interface NotificadorAtrasoPort {
    void enviar(NotificacaoAtraso notificacao);

    record NotificacaoAtraso(
            String nomeUsuario,
            String emailUsuario,
            String tituloLivro,
            LocalDate dataPrevistaDevolucao,
            int diasAtraso
    ) {
    }
}
