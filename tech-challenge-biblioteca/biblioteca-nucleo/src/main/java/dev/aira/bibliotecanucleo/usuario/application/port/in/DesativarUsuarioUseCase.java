package dev.aira.bibliotecanucleo.usuario.application.port.in;

import java.util.UUID;

public interface DesativarUsuarioUseCase {
    void desativar(UUID id);
}
