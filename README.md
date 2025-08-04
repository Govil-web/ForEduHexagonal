# Sistema Académico Enterprise (SAE)

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring) ![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql) ![Arquitectura](https://img.shields.io/badge/Arquitectura-Hexagonal-informational?style=for-the-badge)

Backend para una plataforma SaaS de gestión académica, diseñado con principios de **Arquitectura Hexagonal** y **Domain-Driven Design (DDD)** para ofrecer una solución robusta, escalable y multi-tenant.

## 🚀 Empezando

Para configurar tu entorno de desarrollo, compilar y ejecutar el proyecto, consulta nuestra guía detallada:

* **[Guía de Inicio Rápido: Entorno Local](./docs/getting-started/01-entorno-local.md)**

## 🗺️ Portal de Documentación

Toda la documentación del proyecto está centralizada y versionada junto con el código fuente. Antes de contribuir, es fundamental familiarizarse con los siguientes documentos:

### **Principios y Gobernanza**
* **[Manifiesto de Buenas Prácticas](./manifiesto-buenas-practicas.md)**: Establece los estándares de arquitectura, código y colaboración que **deben** seguirse en este proyecto.
* **[Guía de Contribución](./CONTRIBUTING.md)**: Pasos y flujo de trabajo para proponer, desarrollar y fusionar nuevas funcionalidades.

### **Arquitectura y Diseño**
* **[Visión General del Proyecto](./docs/architecture/01-vision-general.md)**: Entiende el propósito, los objetivos y el público del sistema.
* **[Arquitectura Hexagonal Aplicada](./docs/architecture/02-arquitectura-hexagonal.md)**: Explicación de las capas, puertos y adaptadores.
* **[Diseño Impulsado por el Dominio (DDD)](./docs/architecture/03-ddd-aplicado.md)**: Cómo modelamos el negocio con Agregados, Entidades y Eventos.
* **[Esquema de Datos](./docs/architecture/04-esquema-de-datos.md)**: Descripción y diagrama de la base de datos multi-tenant.
* **[Pila Tecnológica](./docs/architecture/05-tecnologias.md)**: Lista de las tecnologías, frameworks y herramientas que utilizamos.

### **Decisiones y Evolución**
* **[Decisiones de Arquitectura (ADRs)](./docs/adr/)**: Bitácora de las decisiones técnicas más importantes que hemos tomado, incluyendo nuestra estrategia de **Monolito Modular**.
* **[Módulos Funcionales](./docs/modules/)**: Documentación específica para cada uno de los grandes módulos del sistema:
    * **[Módulo de Pedagogía](./docs/modules/pedagogia/README.md)**
    * **[Módulo de Finanzas](./docs/modules/finanzas/README.md)**

## 🛠️ Tecnologías Principales

| Área                    | Tecnología                                                                                                                                |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Backend** | Java 21, Spring Boot 3.x, Spring Data JPA / Hibernate, MapStruct, Lombok                                                                    |
| **Base de Datos** | MySQL (Desarrollo/Producción), H2 (Pruebas) |
| **Pruebas y Calidad** | JUnit 5, Mockito, ArchUnit                                  |
| **DevOps y Herramientas** | Maven, Flyway, Docker, SpringDoc (OpenAPI 3)                   |

---

Este `README.md` está diseñado para ser la puerta de entrada perfecta al proyecto, proporcionando una visión general rápida y enlaces directos a toda la información detallada que un desarrollador o stakeholder pueda necesitar.