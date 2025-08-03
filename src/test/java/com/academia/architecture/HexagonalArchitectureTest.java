package com.academia.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.academia", importOptions = {ImportOption.DoNotIncludeTests.class})
public class HexagonalArchitectureTest {

    // --- PAQUETES DE CAPAS ---
    private static final String DOMAIN_LAYER = "com.academia.domain..";
    private static final String APPLICATION_LAYER = "com.academia.application..";
    private static final String INFRASTRUCTURE_LAYER = "com.academia.infrastructure..";

    // --- PAQUETES ESPECÍFICOS ---
    private static final String DOMAIN_MODEL = "com.academia.domain.model..";

    // CORRECCIÓN: Paquetes de puertos de entrada más específicos para las interfaces
    private static final String DOMAIN_INPUT_PORTS_INTERFACES = "com.academia.domain.ports.in..*";


    @ArchTest
    public static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy(DOMAIN_LAYER)
            .layer("Application").definedBy(APPLICATION_LAYER)
            .layer("Infrastructure").definedBy(INFRASTRUCTURE_LAYER)

            // CORRECCIÓN: Reglas más realistas
            .whereLayer("Application").mayOnlyAccessLayers("Domain")
            .whereLayer("Domain").mayNotAccessAnyLayer() // El dominio sigue siendo puro
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain");


    @ArchTest
    public static final ArchRule domain_layer_should_not_depend_on_anything_external =
            noClasses().that().resideInAPackage(DOMAIN_MODEL)
                    .should().dependOnClassesThat().resideInAnyPackage(APPLICATION_LAYER, INFRASTRUCTURE_LAYER);

    // CORRECCIÓN: La regla ahora solo se aplica a los paquetes que contienen los casos de uso,
    // excluyendo los subpaquetes 'commands', 'dtos', 'queries'.
    @ArchTest
    public static final ArchRule input_ports_should_be_interfaces =
            classes().that().resideInAPackage(DOMAIN_INPUT_PORTS_INTERFACES)
                    .should().beInterfaces();
}