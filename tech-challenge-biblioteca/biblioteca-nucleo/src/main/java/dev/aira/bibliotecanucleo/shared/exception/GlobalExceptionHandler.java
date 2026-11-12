package dev.aira.bibliotecanucleo.shared.exception;

import dev.aira.bibliotecanucleo.emprestimo.application.exception.LimiteEmprestimosAtingidoException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoAtivoJaExisteException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoNaoEncontradoException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoNaoPertenceAoUsuarioException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaDisponivelParaOutroUsuarioException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaJaAbertaException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.UsuarioInativoParaEmprestimoException;
import dev.aira.bibliotecanucleo.emprestimo.domain.exception.EmprestimoJaDevolvidoException;
import dev.aira.bibliotecanucleo.livro.application.exception.LivroJaCadastradoException;
import dev.aira.bibliotecanucleo.livro.domain.exception.IsbnInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IsbnInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> isbnInvalido(IsbnInvalidoException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler({
            EmprestimoAtivoJaExisteException.class,
            LivroJaCadastradoException.class,
            ReservaJaAbertaException.class,
            ReservaDisponivelParaOutroUsuarioException.class,
            LimiteEmprestimosAtingidoException.class,
            UsuarioInativoParaEmprestimoException.class
    })
    public ResponseEntity<ApiErrorResponse> conflitoDeSolicitacao(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }

    @ExceptionHandler(EmprestimoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> emprestimoNaoEncontrado(EmprestimoNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(EmprestimoNaoPertenceAoUsuarioException.class)
    public ResponseEntity<ApiErrorResponse> emprestimoDeOutroUsuario(EmprestimoNaoPertenceAoUsuarioException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage()));
    }

    @ExceptionHandler(EmprestimoJaDevolvidoException.class)
    public ResponseEntity<ApiErrorResponse> emprestimoJaDevolvido(EmprestimoJaDevolvidoException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }
}
