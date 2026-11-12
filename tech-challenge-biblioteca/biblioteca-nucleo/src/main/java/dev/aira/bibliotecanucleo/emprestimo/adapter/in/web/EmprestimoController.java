package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web;

import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoRequest;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.SolicitacaoLivroResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.UsuarioOperacaoRequest;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.DevolverLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.EmprestarLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.ReservarLivroUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Empréstimos e reservas", description = "Solicitação e devolução de livros")
@RequestMapping("/api/biblioteca")
public class EmprestimoController {
    private final EmprestarLivroUseCase emprestarLivroUseCase;
    private final ReservarLivroUseCase reservarLivroUseCase;
    private final DevolverLivroUseCase devolverLivroUseCase;
    private final EmprestimoWebMapper mapper;

    @Operation(summary = "Solicita um empréstimo", description = "Registra o empréstimo se houver exemplar disponível; caso contrário, cria uma reserva.")
    @ApiResponse(responseCode = "201", description = "Empréstimo ou reserva criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Solicitação em conflito com as regras de empréstimo")
    @PostMapping("/emprestimo")
    public ResponseEntity<SolicitacaoLivroResponse> emprestarLivro(@Valid @RequestBody EmprestimoRequest request) {
        var resultado = emprestarLivroUseCase.emprestarLivro(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(resultado));
    }

    @Operation(summary = "Solicita a reserva de um livro", description = "Cria uma reserva ou registra o empréstimo se houver exemplar disponível.")
    @ApiResponse(responseCode = "201", description = "Reserva ou empréstimo criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Solicitação em conflito com as regras de empréstimo")
    @PostMapping("/reserva")
    public ResponseEntity<SolicitacaoLivroResponse> reservarLivro(@Valid @RequestBody EmprestimoRequest request) {
        var resultado = reservarLivroUseCase.reservarLivro(mapper.toCommandReserva(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(resultado));
    }

    @Operation(summary = "Devolve um livro emprestado")
    @ApiResponse(responseCode = "200", description = "Livro devolvido")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "403", description = "Empréstimo não pertence ao usuário informado")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    @ApiResponse(responseCode = "409", description = "Empréstimo já foi devolvido")
    @PutMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<EmprestimoResponse> devolverLivro(@PathVariable UUID emprestimoId, @Valid @RequestBody UsuarioOperacaoRequest request) {
        var detalhe = devolverLivroUseCase.devolverLivro(emprestimoId, request.usuarioId());
        return ResponseEntity.ok(mapper.toResponse(detalhe));
    }
}
