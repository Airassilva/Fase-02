package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web;

import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoRequest;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.EmprestimoResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.SolicitacaoLivroResponse;
import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto.UsuarioOperacaoRequest;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.DevolverLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.EmprestarLivroUseCase;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.ReservarLivroUseCase;
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
@RequestMapping("/api/biblioteca")
public class EmprestimoController {
    private final EmprestarLivroUseCase emprestarLivroUseCase;
    private final ReservarLivroUseCase reservarLivroUseCase;
    private final DevolverLivroUseCase devolverLivroUseCase;
    private final EmprestimoWebMapper mapper;

    @PostMapping("/emprestimo")
    public ResponseEntity<SolicitacaoLivroResponse> emprestarLivro(@Valid @RequestBody EmprestimoRequest request) {
        var resultado = emprestarLivroUseCase.emprestarLivro(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(resultado));
    }

    @PostMapping("/reserva")
    public ResponseEntity<SolicitacaoLivroResponse> reservarLivro(@Valid @RequestBody EmprestimoRequest request) {
        var resultado = reservarLivroUseCase.reservarLivro(mapper.toCommandReserva(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(resultado));
    }

    @PutMapping("/emprestimo/{emprestimoId}/devolver")
    public ResponseEntity<EmprestimoResponse> devolverLivro(@PathVariable UUID emprestimoId, @Valid @RequestBody UsuarioOperacaoRequest request) {
        var detalhe = devolverLivroUseCase.devolverLivro(emprestimoId, request.usuarioId());
        return ResponseEntity.ok(mapper.toResponse(detalhe));
    }
}
