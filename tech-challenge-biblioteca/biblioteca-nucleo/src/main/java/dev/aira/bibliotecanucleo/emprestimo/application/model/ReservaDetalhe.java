package dev.aira.bibliotecanucleo.emprestimo.application.model;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;

import java.time.Instant;
import java.util.UUID;

public record ReservaDetalhe(
        UUID id,
        UUID usuarioId,
        UUID livroId,
        String nomeLivro,
        String nomeUsuario,
        Instant dataReserva,
        StatusReserva status
) {
}
