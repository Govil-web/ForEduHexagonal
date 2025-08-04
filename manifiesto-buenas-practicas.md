# Manifiesto de Buenas Prácticas - Sistema Académico Enterprise (SAE)

Este documento establece los principios y estándares que rigen el desarrollo de este proyecto. Todo el código y las contribuciones deben adherirse a estas guías para mantener la calidad, consistencia y mantenibilidad del software.

## 📐 Principios de Arquitectura y Diseño

1.  **Arquitectura Hexagonal Siempre**: Toda nueva funcionalidad debe respetar la estricta separación de capas (Dominio, Aplicación, Infraestructura). La lógica de negocio pertenece al Dominio y nunca debe depender de detalles de infraestructura, como lo validan nuestras pruebas de arquitectura.
2.  **Dominio Rico, Servicios Anémicos**: El núcleo de la lógica (validaciones, reglas de negocio, transiciones de estado) debe residir en los Agregados y Entidades del Dominio (ej: `UserAccount`, `Course`). Los servicios de la capa de aplicación solo deben orquestar el flujo de los casos de uso.
3.  **Inmutabilidad por Defecto**: Los Value Objects (ej: `Name`, `Email`) y los DTOs (ej: `StudentDetailsDTO`) deben ser inmutables, preferiblemente usando `records` de Java, para garantizar la predictibilidad y evitar efectos secundarios.
4.  **Comunicación por Eventos de Dominio**: Para desacoplar módulos y funcionalidades, se priorizará la comunicación asíncrona mediante Eventos de Dominio (ej: `UserRegisteredEvent`, `StudentEnrolledInCourse`). Esto es gestionado a través del `DomainEventPublisher`.
5.  **Decisiones Arquitectónicas Documentadas**: Las decisiones de alto impacto se registrarán en la carpeta `/docs/adr` (Architecture Decision Records) para mantener un registro histórico del porqué de nuestra arquitectura.

## 💻 Estilo de Código y Convenciones

1.  **Guía de Estilo**: Se sigue la guía de estilo de Google para Java. Se espera que los desarrolladores usen plugins en su IDE para el formateo automático del código.
2.  **Nomenclatura Consistente**:
    * **Casos de Uso (Interfaces)**: Sufijo `UseCase` (ej: `RegisterNewStudentUseCase`).
    * **Implementaciones de Casos de Uso**: Sufijo `ServiceImpl` (ej: `EnrollStudentInCourseServiceImpl`).
    * **Adaptadores de Repositorio**: Sufijo `Adapter` (ej: `JpaStudentRepositoryAdapter`).
    * **Comandos y Consultas**: Nombres explícitos que representen la intención (ej: `RegisterNewStudentCommand`, `GetStudentDetailsQuery`).
3.  **Manejo de Nulos**: Se prohíbe estrictamente el uso de `null` como valor de retorno. Se debe utilizar `java.util.Optional` para representar la posible ausencia de un valor, como se observa en las firmas de los repositorios.

## 🌿 Control de Versiones (Git Workflow)

1.  **Rama `main`**: Contiene el código de la última versión estable desplegada en Producción. Las fusiones a `main` solo se realizan desde `dev` y representan un release.
2.  **Rama `dev`**: Rama principal de integración continua. Todas las nuevas funcionalidades se fusionan aquí.
3.  **Ramas de `feature`**: Todo nuevo desarrollo se realiza en una rama propia a partir de `dev`. El nombre debe seguir el formato `feature/TICKET-ID-descripcion-corta` (ej: `feature/SAE-101-payment-gateway`).
4.  **Pull Requests (PRs)**: Para fusionar a `dev`, es obligatoria una PR. La PR debe incluir:
    * Una descripción clara del cambio y el problema que resuelve.
    * Un enlace al ticket correspondiente en el sistema de gestión de proyectos.
    * Pruebas unitarias y de integración que validen la nueva funcionalidad.
    * **La actualización o creación de la documentación correspondiente en la carpeta `/docs`**.
    * Requiere la aprobación de al menos otro miembro del equipo.

## ✅ Calidad y Pruebas

1.  **Pruebas como Requisito**: No se aceptará ninguna PR sin pruebas que validen el cambio. El desarrollo guiado por pruebas (TDD) es la práctica recomendada.
2.  **Tipos de Pruebas**:
    * **Unitarias**: Aisladas y rápidas, para validar la lógica pura del Dominio y los mappers.
    * **De Integración (`@SpringBootTest`)**: Para validar el flujo completo desde el controlador hasta la base de datos, como en `StudentControllerIntegrationTest`.
    * **De Arquitectura (`ArchUnit`)**: Para asegurar que las dependencias entre capas no se violen, manteniendo la pureza de la arquitectura hexagonal.