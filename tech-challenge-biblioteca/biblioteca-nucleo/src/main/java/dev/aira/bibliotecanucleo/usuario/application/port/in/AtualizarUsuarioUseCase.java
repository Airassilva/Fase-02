package dev.aira.bibliotecanucleo.usuario.application.port.in;


import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;

import java.util.UUID;

public interface AtualizarUsuarioUseCase {
    Usuario atualizar(UUID id, String nome, String email);
}
