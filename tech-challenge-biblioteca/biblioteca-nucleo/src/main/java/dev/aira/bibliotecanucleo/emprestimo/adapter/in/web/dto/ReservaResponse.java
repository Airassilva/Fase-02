package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;

import java.time.Instant;
import java.util.UUID;

public record ReservaResponse(
        UUID id,
        UUID usuarioId,
        UUID livroId,
        String nomeLivro,
        String nomeUsuario,
        Instant dataReserva,
        StatusReserva status
) {
}
