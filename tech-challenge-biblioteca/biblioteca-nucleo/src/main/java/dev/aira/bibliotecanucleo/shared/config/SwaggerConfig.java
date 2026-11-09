package dev.aira.bibliotecanucleo.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Biblioteca Online - API Núcleo")
                .version("v1")
                .description("Livros, usuários, empréstimos e reservas. Datas em UTC, formato ISO-8601."));
    }
}
