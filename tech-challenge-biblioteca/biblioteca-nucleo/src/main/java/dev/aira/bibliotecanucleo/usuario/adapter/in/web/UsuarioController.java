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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuários", description = "Cadastro, consulta e manutenção de usuários")
@RequestMapping("/api/biblioteca/usuarios")
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUsuario;
    private final BuscarUsuariosUseCase buscarUsuarios;
    private final AtualizarUsuarioUseCase atualizarUsuario;
    private final DesativarUsuarioUseCase desativarUsuario;
    private final UsuarioWebMapper mapper;

    @Operation(summary = "Cadastra um usuário")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    @PostMapping
    ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request){
        var usuario = cadastrarUsuario.cadastrar(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(usuario));
    }

    @Operation(summary = "Lista usuários", description = "Busca usuários por filtros opcionais e paginação.")
    @ApiResponse(responseCode = "200", description = "Usuários listados")
    @GetMapping
    ResponseEntity<PageResult<UsuarioResponse>> listar(@RequestParam(required = false) String nome, @RequestParam(required = false) String email, @RequestParam(required = false) TipoUsuario tipo, @RequestParam(required = false) Boolean ativo, Pageable pageable) {
        var filtro = new UsuarioFiltro(nome, email, tipo, ativo);
        var pageQuery = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        var usuarios = buscarUsuarios.listar(filtro, pageQuery);
        return ResponseEntity.ok(mapper.toResponsePage(usuarios));
    }

    @Operation(summary = "Busca um usuário por ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @GetMapping("/{id}")
    ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        var usuario = buscarUsuarios.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @Operation(summary = "Atualiza um usuário", description = "Atualiza o nome e o e-mail do usuário.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    @PutMapping("/{id}")
    ResponseEntity<UsuarioResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest request) {
        var usuario = atualizarUsuario.atualizar(id, request.nome(), request.email());
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @Operation(summary = "Desativa um usuário por ID")
    @ApiResponse(responseCode = "204", description = "Usuário desativado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> desativar(@PathVariable UUID id) {
        desativarUsuario.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
