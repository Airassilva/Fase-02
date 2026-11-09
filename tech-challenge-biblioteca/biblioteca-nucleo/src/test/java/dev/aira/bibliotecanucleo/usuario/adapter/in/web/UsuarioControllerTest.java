package dev.aira.bibliotecanucleo.usuario.adapter.in.web;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioRequest;
import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioUpdateRequest;
import dev.aira.bibliotecanucleo.usuario.application.port.in.AtualizarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.BuscarUsuariosUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.CadastrarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.DesativarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioControllerTest {
    private CadastrarUsuarioUseCase cadastrar;
    private BuscarUsuariosUseCase buscar;
    private AtualizarUsuarioUseCase atualizar;
    private DesativarUsuarioUseCase desativar;
    private UsuarioController controller;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        cadastrar = mock(CadastrarUsuarioUseCase.class);
        buscar = mock(BuscarUsuariosUseCase.class);
        atualizar = mock(AtualizarUsuarioUseCase.class);
        desativar = mock(DesativarUsuarioUseCase.class);
        controller = new UsuarioController(cadastrar, buscar, atualizar, desativar, new UsuarioWebMapper());
        usuario = Usuario.reconstituir(
                UUID.randomUUID(),
                "Maria Silva",
                "maria@email.com",
                TipoUsuario.ALUNO,
                true,
                null);
    }

    @Test
    void cadastraUsuarioERetornaCreated() {
        when(cadastrar.cadastrar(any())).thenReturn(usuario);

        var response = controller.cadastrar(
                new UsuarioRequest("Maria Silva", "maria@email.com", TipoUsuario.ALUNO));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuario.getId(), response.getBody().id());
        assertEquals(usuario.getNome(), response.getBody().nome());
        assertEquals(usuario.getEmail(), response.getBody().email());
        assertEquals(usuario.getTipo(), response.getBody().tipoUsuario());
        verify(cadastrar).cadastrar(any());
    }

    @Test
    void listaUsuariosComFiltrosEPaginacao() {
        UsuarioFiltro filtro = new UsuarioFiltro("Maria", "maria@email.com", TipoUsuario.ALUNO, true);
        PageQuery pageQuery = new PageQuery(1, 5);
        when(buscar.listar(filtro, pageQuery))
                .thenReturn(new PageResult<>(List.of(usuario), 12, 1, 5));

        var response = controller.listar(
                filtro.nome(), filtro.email(), filtro.tipo(), filtro.ativo(), PageRequest.of(1, 5));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().size());
        assertEquals(usuario.getId(), response.getBody().content().get(0).id());
        assertEquals(12, response.getBody().total());
        assertEquals(1, response.getBody().page());
        assertEquals(5, response.getBody().size());
        verify(buscar).listar(filtro, pageQuery);
    }

    @Test
    void buscaUsuarioPorId() {
        when(buscar.buscarPorId(usuario.getId())).thenReturn(usuario);

        var response = controller.buscarPorId(usuario.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario.getId(), response.getBody().id());
        assertEquals(usuario.getEmail(), response.getBody().email());
    }

    @Test
    void atualizaUsuario() {
        when(atualizar.atualizar(usuario.getId(), "Maria Souza", "maria.souza@email.com"))
                .thenReturn(usuario);

        var response = controller.atualizar(
                usuario.getId(),
                new UsuarioUpdateRequest("Maria Souza", "maria.souza@email.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(atualizar).atualizar(usuario.getId(), "Maria Souza", "maria.souza@email.com");
    }

    @Test
    void desativaUsuarioERetornaNoContent() {
        var response = controller.desativar(usuario.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(desativar).desativar(usuario.getId());
    }
}
