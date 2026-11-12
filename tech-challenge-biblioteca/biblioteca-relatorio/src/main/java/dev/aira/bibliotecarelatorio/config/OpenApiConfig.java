package dev.aira.bibliotecarelatorio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI bibliotecaRelatorioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca Online - API de Relatórios")
                        .version("v1")
                        .description("Relatórios de uso da biblioteca. Datas em UTC (ISO-8601); "
                                + "semana de segunda a domingo (America/Recife)."))
                .addTagsItem(new Tag()
                        .name("Relatórios")
                        .description("Consultas e geração de relatórios"));
    }
}
