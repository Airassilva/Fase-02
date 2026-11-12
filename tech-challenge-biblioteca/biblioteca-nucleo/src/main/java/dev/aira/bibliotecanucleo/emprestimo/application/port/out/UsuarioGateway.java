package dev.aira.bibliotecanucleo.emprestimo.application.port.out;

import java.time.Period;
import java.util.UUID;

public interface UsuarioGateway {
    UsuarioInfo buscarUsuario(UUID usuarioId);

    UsuarioInfo buscarUsuarioParaEmprestimo(UUID usuarioId);

    UsuarioResumo buscarResumo(UUID usuarioId);

    record UsuarioInfo(UUID id, String nome, Period prazoEmprestimo, int limiteEmprestimosAtivos, boolean ativo) {
    }

    record UsuarioResumo(UUID id, String nome, String email) {
    }
}
