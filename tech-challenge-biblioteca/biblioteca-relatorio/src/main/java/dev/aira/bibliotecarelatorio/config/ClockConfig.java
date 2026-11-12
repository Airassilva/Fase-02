package dev.aira.bibliotecarelatorio.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    /**
     * Define o dia civil dos empréstimos conforme o fuso de Recife:
     * meia-noite UTC corresponde a 21h do dia anterior em Recife.
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Recife"));
    }
}