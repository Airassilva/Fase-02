package dev.aira.bibliotecanucleo.usuario.application.port.in;

import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;

public interface CadastrarUsuarioUseCase {
    Usuario cadastrar(CadastrarUsuarioCommand command);

    record CadastrarUsuarioCommand(
            String nome,
            String email,
            TipoUsuario tipo) {
    }
}
