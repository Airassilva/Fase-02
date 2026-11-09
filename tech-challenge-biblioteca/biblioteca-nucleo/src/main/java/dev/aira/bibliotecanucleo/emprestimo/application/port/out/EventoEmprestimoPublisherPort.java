package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoEvento;

public interface EventoEmprestimoPublisherPort {
    void publicar(EmprestimoEvento evento);
}
