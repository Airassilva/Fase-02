package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import java.util.function.Supplier;

public interface EmprestimoUnitOfWork {
    <T> T executar(Supplier<T> operacao);
}
