package dev.aira.bibliotecanucleo.usuario.application.service;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.exception.UsuarioJaCadastradoException;
import dev.aira.bibliotecanucleo.usuario.application.exception.UsuarioNaoEncontradoException;
import dev.aira.bibliotecanucleo.usuario.application.port.in.AtualizarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.BuscarUsuariosUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.CadastrarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.DesativarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.out.UsuarioRepositoryPort;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService implements CadastrarUsuarioUseCase, AtualizarUsuarioUseCase, BuscarUsuariosUseCase, DesativarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    @Transactional
    public Usuario cadastrar(CadastrarUsuarioCommand command) {
        Usuario usuario = Usuario.cadastrar(command.nome(), command.email(), command.tipo());
        if (usuarioRepositoryPort.existePorEmail(usuario.getEmail())) {
            throw new UsuarioJaCadastradoException(usuario.getEmail());
        }
        return usuarioRepositoryPort.salvar(usuario);
    }

    @Override
    @Transactional
    public Usuario atualizar(UUID id, String nome, String email) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        usuario.atualizar(nome, email);
        return usuarioRepositoryPort.salvar(usuario);
    }

    @Override
    @Transactional
    public Usuario buscarPorId(UUID id) {
        return usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Override
    @Transactional
    public PageResult<Usuario> listar(UsuarioFiltro filtro, PageQuery pageQuery) {
        return usuarioRepositoryPort.listar(filtro, pageQuery);
    }

    @Override
    @Transactional
    public void desativar(UUID id) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        usuario.desativar();
        usuarioRepositoryPort.salvar(usuario);
    }
}
