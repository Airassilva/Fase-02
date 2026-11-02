package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoUnitOfWork;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public class EmprestimoUnitOfWorkAdapter implements EmprestimoUnitOfWork {
    private final TransactionTemplate transactionTemplate;

    public EmprestimoUnitOfWorkAdapter(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T executar(Supplier<T> operacao) {
        return transactionTemplate.execute(status -> operacao.get());
    }
}
