# Diseño Impulsado por el Dominio (DDD) en el SAE

Utilizamos los patrones tácticos de Domain-Driven Design (DDD) para modelar el complejo dominio de la gestión académica de una manera que sea fiel a la realidad del negocio y fácil de traducir a código.

## Bloques de Construcción de DDD

1.  **Agregado (Aggregate)**:
    * Es un clúster de entidades y value objects que se trata como una única unidad de consistencia. Cada agregado tiene una raíz (la entidad principal) que es el único punto de acceso desde el exterior.
    * **Ejemplo**: El agregado `Course` es la raíz que gestiona un conjunto de `Enrollment` (inscripciones). Para inscribir un estudiante, no se interactúa directamente con una inscripción, sino que se le pide al `Course` que realice la operación (`course.enrollStudent(...)`), asegurando que se cumplan todas las reglas de negocio (invariantes).
    * **Ejemplo**: El agregado `UserAccount` encapsula la entidad `User` y la lógica de negocio relacionada con la cuenta, como su activación.

2.  **Entidad (Entity)**:
    * Es un objeto con identidad propia que persiste a lo largo del tiempo. Dos entidades son diferentes si tienen IDs diferentes, aunque sus otros atributos sean iguales.
    * **Ejemplo**: La entidad `Student` tiene una identidad única a través de su `accountId`.

3.  **Objeto de Valor (Value Object)**:
    * Es un objeto inmutable cuya identidad se basa en el valor de sus atributos, no en un ID. Se utilizan para describir características de una entidad.
    * **Ejemplo**: `Name` se define por el nombre y el apellido. Dos objetos `Name` son iguales si sus nombres y apellidos coinciden. Otros ejemplos son `Email` y `OrganizationId`.

4.  **Repositorio (Repository)**:
    * Es una interfaz definida en el Dominio que abstrae la colección de agregados, simulando una colección en memoria. Su implementación real se encuentra en la capa de Infraestructura.
    * **Ejemplo**: `CourseRepository` define métodos como `save(Course course)` y `findById(CourseId courseId)`, permitiendo a la capa de aplicación persistir y recuperar agregados de `Course` sin saber cómo se almacenan.

5.  **Evento de Dominio (Domain Event)**:
    * Representa algo significativo que ha ocurrido en el dominio. Se utilizan para comunicar cambios entre diferentes partes del sistema de forma desacoplada.
    * **Ejemplo**: Cuando un estudiante se inscribe en un curso, el agregado `Course` registra un evento `StudentEnrolledInCourse`. Otros módulos pueden suscribirse a este evento para realizar acciones, como enviar un email de bienvenida.