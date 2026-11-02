package dev.aira.bibliotecanucleo.usuario.adapter.in.web;

import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioRequest;
import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioResponse;
import dev.aira.bibliotecanucleo.usuario.application.port.in.CadastrarUsuarioUseCase.CadastrarUsuarioCommand;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class UsuarioWebMapper {

    public CadastrarUsuarioCommand toCommand(@Valid UsuarioRequest request) {
        return new CadastrarUsuarioCommand(
                request.nome(),
                request.email(),
                request.tipo()
        );
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo()
        );
    }

    public PageResult<UsuarioResponse> toResponsePage(PageResult<Usuario> usuarios) {
        return new PageResult<>(
                usuarios.content().stream().map(this::toResponse).toList(),
                usuarios.total(),
                usuarios.page(),
                usuarios.size()
        );
    }
}
