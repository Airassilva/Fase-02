package dev.aira.bibliotecanucleo.emprestimo.domain.exception;

import java.util.UUID;

public class EmprestimoJaDevolvidoException extends RuntimeException {
    public EmprestimoJaDevolvidoException(UUID id) {
        super("O empréstimo " + id + " já foi devolvido");
    }
}
