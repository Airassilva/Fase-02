package dev.aira.bibliotecarelatorio.relatorio;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArquiteturaRelatorioTest {

    @Test
    void dominioNaoDependeDeFrameworks() {
        ArchRule regra = noClasses()
                .that().resideInAPackage("..relatorio.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..");

        regra.check(new ClassFileImporter().importPackages("dev.aira.bibliotecarelatorio"));
    }

    @Test
    void aplicacaoNaoDependeDosAdapters() {
        ArchRule regra = noClasses()
                .that().resideInAPackage("..relatorio.application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..relatorio.adapter..");

        regra.check(new ClassFileImporter().importPackages("dev.aira.bibliotecarelatorio"));
    }
}
