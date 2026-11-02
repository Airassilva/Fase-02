package dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto;

import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @Schema(example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @Schema(example = "maria.silva@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        String email,

        @Schema(example = "ALUNO")
        @NotNull(message = "O tipo de usuário é obrigatório")
        TipoUsuario tipo) {
}
