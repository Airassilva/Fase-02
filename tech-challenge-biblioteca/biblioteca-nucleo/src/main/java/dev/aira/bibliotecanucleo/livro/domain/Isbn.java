package dev.aira.bibliotecanucleo.livro.domain;

import java.util.Objects;

public record Isbn(String valor) {

    public Isbn {
        Objects.requireNonNull(valor, "ISBN é obrigatório");

        String normalizado = valor.replaceAll("[\\s-]", "").toUpperCase();
        valor = switch (normalizado.length()) {
            case 10 -> {
                validarIsbn10(normalizado, valor);
                yield converterParaIsbn13(normalizado);
            }
            case 13 -> {
                validarIsbn13(normalizado, valor);
                yield normalizado;
            }
            default -> throw new IsbnInvalidoException(valor);
        };
    }

    public static Isbn de(String valor) {
        return new Isbn(valor);
    }

    private static void validarIsbn13(String isbn, String original) {
        if (!isbn.matches("\\d{13}") || somaIsbn13(isbn) % 10 != 0) {
            throw new IsbnInvalidoException(original);
        }
    }

    private static void validarIsbn10(String isbn, String original) {
        if (!isbn.matches("\\d{9}[\\dX]")) {
            throw new IsbnInvalidoException(original);
        }
        int soma = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int digito = (c == 'X') ? 10 : c - '0';
            soma += digito * (10 - i);
        }
        if (soma % 11 != 0) {
            throw new IsbnInvalidoException(original);
        }
    }

    private static String converterParaIsbn13(String isbn10) {
        String base = "978" + isbn10.substring(0, 9);
        int digitoVerificador = (10 - somaIsbn13(base + "0") % 10) % 10;
        return base + digitoVerificador;
    }

    private static int somaIsbn13(String isbn) {
        int soma = 0;
        for (int i = 0; i < isbn.length(); i++) {
            int peso = (i % 2 == 0) ? 1 : 3;
            soma += (isbn.charAt(i) - '0') * peso;
        }
        return soma;
    }

    @Override
    public String toString() {
        return valor;
    }
}