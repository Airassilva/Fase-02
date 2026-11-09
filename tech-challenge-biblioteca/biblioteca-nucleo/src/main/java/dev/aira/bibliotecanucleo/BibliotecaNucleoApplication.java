package dev.aira.bibliotecanucleo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BibliotecaNucleoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaNucleoApplication.class, args);
    }
}
