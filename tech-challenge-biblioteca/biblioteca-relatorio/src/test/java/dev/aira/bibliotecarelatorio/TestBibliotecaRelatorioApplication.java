package dev.aira.bibliotecarelatorio;

import org.springframework.boot.SpringApplication;

public class TestBibliotecaRelatorioApplication {
    public static void main(String[] args) {
        SpringApplication.from(BibliotecaRelatorioApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
