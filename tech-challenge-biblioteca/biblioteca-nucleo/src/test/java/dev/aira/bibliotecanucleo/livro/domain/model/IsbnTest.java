package dev.aira.bibliotecanucleo.livro.domain.model;

import dev.aira.bibliotecanucleo.livro.domain.exception.IsbnInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IsbnTest {

    @Test
    void normalizesValidIsbn13() {
        assertEquals("9780306406157", Isbn.de("978-0-306-40615-7").valor());
    }

    @Test
    void convertsValidIsbn10ToIsbn13() {
        assertEquals("9780306406157", Isbn.de("0-306-40615-2").valor());
    }

    @Test
    void acceptsIsbn10WithXCheckDigit() {
        assertEquals("9780804429573", Isbn.de("080442957X").valor());
    }

    @Test
    void rejectsNullIsbn() {
        assertThrows(NullPointerException.class, () -> Isbn.de(null));
    }

    @Test
    void rejectsIsbnWithUnsupportedLength() {
        assertThrows(IsbnInvalidoException.class, () -> Isbn.de("123456789"));
    }

    @Test
    void rejectsIsbn13WithInvalidCharactersOrChecksum() {
        assertThrows(IsbnInvalidoException.class, () -> Isbn.de("9780306406158"));
        assertThrows(IsbnInvalidoException.class, () -> Isbn.de("978030640615A"));
    }

    @Test
    void rejectsIsbn10WithInvalidCharactersOrChecksum() {
        assertThrows(IsbnInvalidoException.class, () -> Isbn.de("0306406153"));
        assertThrows(IsbnInvalidoException.class, () -> Isbn.de("030640615A"));
    }
}
