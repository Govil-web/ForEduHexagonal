# Pila Tecnológica del SAE

Este documento describe las tecnologías, frameworks y herramientas clave utilizadas en el proyecto.

## Backend
* **Lenguaje**: Java 21
* **Framework**: Spring Boot 3.x
* **Persistencia**:
    * Spring Data JPA / Hibernate: Como ORM para interactuar con la base de datos.
    * `JpaRepository` se utiliza para las operaciones CRUD básicas, mientras que los repositorios de dominio se implementan con adaptadores personalizados.
* **Mapeo de Objetos**: MapStruct, para convertir eficientemente entre DTOs, entidades de dominio y entidades JPA, reduciendo el código boilerplate.
* **Validación**: Jakarta Bean Validation, para validar las peticiones de entrada en la capa de controladores.
* **Servidor de Aplicaciones**: Apache Tomcat (embebido).

## Base de Datos
* **Motor Principal (Desarrollo/Producción)**: MySQL 8.x.
* **Base de Datos de Pruebas**: H2 in-memory, para ejecutar pruebas de integración rápidas y aisladas.
* **Gestión de Migraciones**: Flyway, para controlar la evolución del esquema de la base de datos de forma versionada y automática.

## Pruebas y Calidad
* **Framework de Pruebas**: JUnit 5
* **Mocks y Stubs**: Mockito
* **Validación Arquitectónica**: ArchUnit, para garantizar que se cumplan las reglas de dependencia de la arquitectura hexagonal.

## DevOps y Herramientas
* **Gestión de Dependencias y Construcción**: Apache Maven
* **Contenerización**: Docker y Docker Compose, para crear un entorno de desarrollo local consistente.
* **Documentación de API**: SpringDoc (OpenAPI 3), para generar automáticamente la documentación de la API a través de Swagger UI.