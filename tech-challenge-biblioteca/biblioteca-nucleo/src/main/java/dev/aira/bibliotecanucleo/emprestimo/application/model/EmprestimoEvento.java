package dev.aira.bibliotecanucleo.emprestimo.application.model;

import dev.aira.bibliotecanucleo.emprestimo.application.port.out.LivroGateway;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoEvento(
        UUID eventId,
        TipoEvento tipo,
        Instant ocorridoEm,

        UUID emprestimoId,
        UUID usuarioId,
        UUID livroId,
        String livroTitulo,
        String livroAutor,
        String livroIsbn,

        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao) {

    public enum TipoEvento {
        EMPRESTIMO_REALIZADO,
        LIVRO_DEVOLVIDO
    }

    public static EmprestimoEvento realizado(Emprestimo e, LivroGateway.LivroInfo livro, Instant agora) {
        return de(TipoEvento.EMPRESTIMO_REALIZADO, e, livro, agora);
    }

    public static EmprestimoEvento devolvido(Emprestimo e, LivroGateway.LivroInfo livro, Instant agora) {
        return de(TipoEvento.LIVRO_DEVOLVIDO, e, livro, agora);
    }

    private static EmprestimoEvento de(TipoEvento tipo, Emprestimo e, LivroGateway.LivroInfo livro, Instant agora) {
        return new EmprestimoEvento(
                UUID.randomUUID(),
                tipo,
                agora,
                e.getId(),
                e.getUsuarioId(),
                e.getLivroId(),
                livro.titulo(),
                livro.autor(),
                livro.isbn(),
                e.getDataEmprestimo(),
                e.getDataPrevistaDevolucao(),
                e.getDataDevolucao());
    }
}