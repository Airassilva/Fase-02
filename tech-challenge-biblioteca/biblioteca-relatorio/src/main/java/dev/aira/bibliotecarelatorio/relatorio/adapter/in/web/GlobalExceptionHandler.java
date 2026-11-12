package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail tratarArgumentoInvalido(IllegalArgumentException excecao) {
        return problema(excecao.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail tratarConversaoInvalida(MethodArgumentTypeMismatchException excecao) {
        return problema("O parâmetro '%s' possui formato inválido".formatted(excecao.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ProblemDetail tratarParametroAusente(MissingServletRequestParameterException excecao) {
        return problema("O parâmetro '%s' é obrigatório".formatted(excecao.getParameterName()));
    }

    private ProblemDetail problema(String detalhe) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
        problema.setTitle("Requisição inválida");
        problema.setType(URI.create("about:blank"));
        return problema;
    }
}
