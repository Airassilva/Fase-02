package dev.aira.bibliotecanucleo.emprestimo.adapter.out.email;

import dev.aira.bibliotecanucleo.emprestimo.application.port.out.NotificadorAtrasoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EmailNotificadorAtrasoAdapter implements NotificadorAtrasoPort {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailNotificadorAtrasoAdapter(
            JavaMailSender mailSender,
            @Value("${app.mail.from:noreply@biblioteca.local}") String remetente
    ) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(NotificacaoAtraso notificacao) {
        if (notificacao.emailUsuario() == null || notificacao.emailUsuario().isBlank()) {
            throw new IllegalArgumentException("O usuário não possui e-mail para receber notificações");
        }

        var mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(notificacao.emailUsuario());
        mensagem.setSubject("Aviso de empréstimo em atraso: " + notificacao.tituloLivro());
        mensagem.setText("""
                Olá, %s.

                O prazo para devolução do livro "%s" venceu em %s.
                O atraso atual é de %d dia(s).

                Por favor, devolva o livro ou entre em contato com a biblioteca.
                """.formatted(
                notificacao.nomeUsuario(),
                notificacao.tituloLivro(),
                notificacao.dataPrevistaDevolucao().format(FORMATO_DATA),
                notificacao.diasAtraso()
        ));
        mailSender.send(mensagem);
    }
}
