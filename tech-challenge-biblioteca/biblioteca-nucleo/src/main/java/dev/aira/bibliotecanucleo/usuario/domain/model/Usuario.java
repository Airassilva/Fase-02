package dev.aira.bibliotecanucleo.usuario.domain.model;

import dev.aira.bibliotecanucleo.usuario.domain.exception.UsuarioInativoException;

import java.time.Instant;
import java.util.UUID;

public class Usuario {
    private UUID id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
    private boolean ativo;
    private Instant criadoEm;

    private Usuario(UUID id, String nome, String email, TipoUsuario tipo, boolean ativo, Instant criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }

    public static Usuario cadastrar(String nome, String email, TipoUsuario tipo) {
        return new Usuario(null,
                validarNome(nome),
                validarEmail(email),
                validarTipo(tipo),
                true,
                null);
    }

    public static Usuario reconstituir(UUID id, String nome, String email, TipoUsuario tipo, boolean ativo, Instant criadoEm) {
        return new Usuario(id, nome, email, tipo, ativo, criadoEm);
    }

    public void atualizar(String novoNome, String novoEmail) {
        this.nome = validarNome(novoNome);
        this.email = validarEmail(novoEmail);
    }

    public void desativar() {
        if (!ativo) {
            throw new IllegalStateException("O usuário já está inativo");
        }
        this.ativo = false;
    }

    public void reativar() {
        if (ativo) {
            throw new IllegalStateException("O usuário já está ativo");
        }
        this.ativo = true;
    }

    public void validarPodeEmprestar() {
        if (!ativo) {
            throw new UsuarioInativoException(id);
        }
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório");
        }
        String normalizado = nome.strip();
        if (normalizado.length() > 150) {
            throw new IllegalArgumentException("O nome deve ter no máximo 150 caracteres");
        }
        return normalizado;
    }

    private static String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }
        String normalizado = email.strip().toLowerCase();
        if (normalizado.length() > 254 || !normalizado.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        return normalizado;
    }

    private static TipoUsuario validarTipo(TipoUsuario tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de usuário é obrigatório");
        }
        return tipo;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
