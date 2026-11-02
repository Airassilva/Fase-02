package dev.aira.bibliotecanucleo.usuario.adapter.in.web;

import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioRequest;
import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioResponse;
import dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto.UsuarioUpdateRequest;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.port.in.AtualizarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.BuscarUsuariosUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.CadastrarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.port.in.DesativarUsuarioUseCase;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUsuario;
    private final BuscarUsuariosUseCase buscarUsuarios;
    private final AtualizarUsuarioUseCase atualizarUsuario;
    private final DesativarUsuarioUseCase desativarUsuario;
    private final UsuarioWebMapper mapper;

    @PostMapping
    ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request){
        var usuario = cadastrarUsuario.cadastrar(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(usuario));
    }

    @GetMapping
    ResponseEntity<PageResult<UsuarioResponse>> listar(@RequestParam(required = false) String nome, @RequestParam(required = false) String email, @RequestParam(required = false) TipoUsuario tipo, @RequestParam(required = false) Boolean ativo, Pageable pageable) {
        var filtro = new UsuarioFiltro(nome, email, tipo, ativo);
        var pageQuery = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        var usuarios = buscarUsuarios.listar(filtro, pageQuery);
        return ResponseEntity.ok(mapper.toResponsePage(usuarios));
    }

    @GetMapping("/{id}")
    ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        var usuario = buscarUsuarios.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @PutMapping("/{id}")
    ResponseEntity<UsuarioResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest request) {
        var usuario = atualizarUsuario.atualizar(id, request.nome(), request.email());
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> desativar(@PathVariable UUID id) {
        desativarUsuario.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
