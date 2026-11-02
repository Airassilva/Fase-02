package dev.aira.bibliotecanucleo.usuario.adapter.out.persistence;

import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant criadoEm;

    public UsuarioJpaEntity(UUID id, String nome, String email, TipoUsuario tipo, boolean ativo, Instant criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }
}
