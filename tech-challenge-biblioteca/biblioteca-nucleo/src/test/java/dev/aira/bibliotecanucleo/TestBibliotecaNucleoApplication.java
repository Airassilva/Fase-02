package dev.aira.bibliotecanucleo;

import org.springframework.boot.SpringApplication;

public class TestBibliotecaNucleoApplication {

    public static void main(String[] args) {
        SpringApplication.from(BibliotecaNucleoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
