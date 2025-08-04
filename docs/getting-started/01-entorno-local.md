# Guía de Inicio Rápido: Entorno Local

Esta guía explica cómo configurar y ejecutar el proyecto en un entorno de desarrollo local.

## Requisitos Previos
* JDK 21 o superior.
* Maven 3.8+.
* Docker y Docker Compose (para la base de datos).

## Pasos de Configuración
1.  **Clonar el repositorio:**
    ```bash
    git clone [URL-DEL-REPOSITORIO]
    cd sistema-academico-enterprise
    ```
2.  **Levantar la Base de Datos:**
    El proyecto utiliza Docker para gestionar la base de datos MySQL. Desde la raíz del proyecto, ejecuta:
    ```bash
    docker-compose up -d
    ```
    Esto levantará un contenedor con MySQL. Las credenciales se encuentran en `src/main/resources/application-dev.yml`.
3.  **Ejecutar la Aplicación:**
    Utiliza Maven para compilar y ejecutar la aplicación Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
4.  **Verificar la Aplicación:**
    * La API estará disponible en `http://localhost:8080/api/v1`.
    * La documentación de la API (Swagger UI) se encuentra en `http://localhost:8080/api/v1/swagger-ui.html`.
    * La base de datos H2 para el perfil de pruebas (`test`) se configura en `application-test.yml` y es gestionada automáticamente por Hibernate (`ddl-auto: create-drop`).