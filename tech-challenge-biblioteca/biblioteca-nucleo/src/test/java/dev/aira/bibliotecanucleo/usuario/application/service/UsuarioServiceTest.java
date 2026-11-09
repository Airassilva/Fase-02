package dev.aira.bibliotecanucleo.usuario.application.service;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.exception.UsuarioJaCadastradoException;
import dev.aira.bibliotecanucleo.usuario.application.exception.UsuarioNaoEncontradoException;
import dev.aira.bibliotecanucleo.usuario.application.port.in.CadastrarUsuarioUseCase.CadastrarUsuarioCommand;
import dev.aira.bibliotecanucleo.usuario.application.port.out.UsuarioRepositoryPort;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class UsuarioServiceTest {
    private UsuarioRepositoryPort repository;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioRepositoryPort.class);
        service = new UsuarioService(repository);
    }

    @Test
    void cadastraUsuarioAtivoComEmailNormalizado() {
        var command = new CadastrarUsuarioCommand("  Maria Silva  ", "  MARIA@EMAIL.COM ", TipoUsuario.ALUNO);
        when(repository.existePorEmail("maria@email.com")).thenReturn(false);
        when(repository.salvar(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.cadastrar(command);

        assertEquals("Maria Silva", resultado.getNome());
        assertEquals("maria@email.com", resultado.getEmail());
        assertEquals(TipoUsuario.ALUNO, resultado.getTipo());
        assertTrue(resultado.isAtivo());
        verify(repository).existePorEmail("maria@email.com");
        verify(repository).salvar(any(Usuario.class));
    }

    @Test
    void rejeitaCadastroComEmailExistente() {
        var command = new CadastrarUsuarioCommand("Maria Silva", "maria@email.com", TipoUsuario.ALUNO);
        when(repository.existePorEmail("maria@email.com")).thenReturn(true);

        assertThrows(UsuarioJaCadastradoException.class, () -> service.cadastrar(command));

        verify(repository, never()).salvar(any(Usuario.class));
    }

    @Test
    void consultaUsuarioPorId() {
        Usuario usuario = usuario();
        when(repository.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));

        assertSame(usuario, service.buscarPorId(usuario.getId()));
    }

    @Test
    void atualizaDadosDoUsuarioExistente() {
        Usuario usuario = usuario();
        when(repository.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.salvar(usuario)).thenReturn(usuario);

        Usuario resultado = service.atualizar(usuario.getId(), "Maria Souza", "maria.souza@email.com");

        assertSame(usuario, resultado);
        assertEquals("Maria Souza", resultado.getNome());
        assertEquals("maria.souza@email.com", resultado.getEmail());
        verify(repository).salvar(usuario);
    }

    @Test
    void desativaUsuarioExistente() {
        Usuario usuario = usuario();
        when(repository.buscarPorId(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.salvar(usuario)).thenReturn(usuario);

        service.desativar(usuario.getId());

        assertFalse(usuario.isAtivo());
        verify(repository).salvar(usuario);
    }

    @Test
    void propagaUsuarioNaoEncontradoNasOperacoesPorId() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> service.buscarPorId(id));
        assertThrows(UsuarioNaoEncontradoException.class, () -> service.atualizar(id, "Maria", "maria@email.com"));
        assertThrows(UsuarioNaoEncontradoException.class, () -> service.desativar(id));

        verify(repository, never()).salvar(any(Usuario.class));
    }

    @Test
    void listaUsuariosComFiltrosEPaginacao() {
        UsuarioFiltro filtro = new UsuarioFiltro("Maria", "maria@email.com", TipoUsuario.ALUNO, true);
        PageQuery pageQuery = new PageQuery(1, 10);
        PageResult<Usuario> pagina = new PageResult<>(List.of(usuario()), 11, 1, 10);
        when(repository.listar(filtro, pageQuery)).thenReturn(pagina);

        assertSame(pagina, service.listar(filtro, pageQuery));
        verify(repository).listar(filtro, pageQuery);
    }

    private Usuario usuario() {
        return Usuario.reconstituir(
                UUID.randomUUID(),
                "Maria Silva",
                "maria@email.com",
                TipoUsuario.ALUNO,
                true,
                null);
    }
}
