package dev.aira.bibliotecanucleo.emprestimo.adapter.out.usuario;

import dev.aira.bibliotecanucleo.emprestimo.application.port.out.UsuarioGateway;
import dev.aira.bibliotecanucleo.usuario.application.exception.UsuarioNaoEncontradoException;
import dev.aira.bibliotecanucleo.usuario.application.port.out.UsuarioRepositoryPort;

import java.util.UUID;

public class UsuarioGatewayAdapter implements UsuarioGateway {
    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioGatewayAdapter(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioInfo buscarUsuario(UUID usuarioId) {
        var usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
        return new UsuarioInfo(
                usuario.getId(),
                usuario.getNome(),
                usuario.getTipo().prazoEmprestimo(),
                usuario.getTipo().limiteEmprestimosAtivos(),
                usuario.isAtivo()
        );
    }
}
