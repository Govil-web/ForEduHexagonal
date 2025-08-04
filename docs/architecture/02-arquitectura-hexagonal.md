# Arquitectura Hexagonal en el SAE

La Arquitectura Hexagonal, también conocida como de Puertos y Adaptadores, es el pilar fundamental de nuestro sistema. Su objetivo es proteger el **Dominio** (el núcleo de la lógica de negocio) de las dependencias externas y tecnológicas, permitiendo que el sistema evolucione de forma independiente.

## El Hexágono: Capas y Límites

Visualizamos nuestro sistema como un hexágono con tres capas principales:

```ascii
+-------------------------------------------------+
|                 INFRAESTRUCTURA                 |
| (API REST, Persistencia JPA, Publicador Eventos)|
+----------------------+--------------------------+
|
+-------------------+--------------------+
|                 APLICACIÓN             |
| (Casos de Uso, Servicios de Aplicación)|
+-------------------+--------------------+
|
+----------+----------+
|         DOMINIO     |
| (Agregados, Lógica) |
+---------------------+

```


1.  **Capa de Dominio (El Núcleo)**:
    * Contiene la lógica de negocio más pura y crítica del sistema. Aquí residen los **Agregados**, **Entidades** y **Value Objects**.
    * **No depende de ninguna otra capa**. Es agnóstico a la base de datos, al framework web o a cualquier tecnología externa.
    * Define las interfaces (o **Puertos**) que necesita para interactuar con el mundo exterior (ej: `StudentRepository`).

2.  **Capa de Aplicación (La Orquestación)**:
    * Implementa los casos de uso del sistema. Su función es orquestar el flujo: recibe una solicitud, utiliza los puertos para cargar agregados del dominio, invoca la lógica de negocio en ellos y usa los puertos para persistir los resultados.
    * Depende del Dominio, pero **no depende de la Infraestructura**. No sabe si la base de datos es MySQL o si los eventos se publican en RabbitMQ.
    * Un ejemplo claro es `RegisterNewStudentServiceImpl`, que coordina la creación de un estudiante.

3.  **Capa de Infraestructura (El Mundo Exterior)**:
    * Contiene los **Adaptadores** que implementan los puertos definidos en el dominio. Es el "pegamento" con la tecnología.
    * **Adaptadores de Entrada (Driving Adapters)**: Conducen la aplicación. Nuestro principal adaptador de entrada es el controlador de la API REST (`StudentController`), que traduce las peticiones HTTP en llamadas a la capa de aplicación.
    * **Adaptadores de Salida (Driven Adapters)**: Son conducidos por la aplicación. Incluyen las implementaciones de los repositorios con JPA (`JpaStudentRepositoryAdapter`) y el publicador de eventos (`SpringDomainEventPublisher`).

Esta separación estricta, validada por nuestras pruebas de arquitectura, nos permite cambiar una tecnología (ej: migrar de MySQL a PostgreSQL) modificando solo un adaptador, sin tocar la lógica de negocio.