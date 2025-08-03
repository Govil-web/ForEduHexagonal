# Sistema Académico Enterprise

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Sistema académico empresarial desarrollado con **Arquitectura Hexagonal** y **Domain-Driven Design (DDD)**, implementado en Java con Spring Boot 3.3.3.

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Puertos y Adaptadores)

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                     │
├─────────────────────────────────────────────────────────────┤
│  Controllers  │  Repositories  │  External Services  │  DB  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
├─────────────────────────────────────────────────────────────┤
│                    Use Cases                                │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ CrearEstudiante │  │ ConsultarEstudi │  │ GestionarAsi │ │
│  │ UseCase         │  │ anteUseCase     │  │ stenciaUseCa │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                          │
├─────────────────────────────────────────────────────────────┤
│  Entities  │  Value Objects  │  Ports (Interfaces)         │
│  ┌─────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │Estudiante│  │   DNI       │  │ EstudianteRepositoryPort│ │
│  │Materia  │  │   Email     │  │ MateriaRepositoryPort   │ │
│  │Asistencia│  │   Legajo    │  │ AsistenciaRepositoryPort│ │
│  └─────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Capas del Sistema

1. **Domain Layer**: Contiene las entidades de negocio, objetos de valor y puertos (interfaces)
2. **Application Layer**: Contiene los casos de uso que orquestan la lógica de negocio
3. **Infrastructure Layer**: Contiene adaptadores, controladores REST y repositorios

## 🚀 Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.3.3** - Framework de aplicación
- **Spring Data JPA** - Persistencia de datos
- **MySQL 8.0** - Base de datos
- **Flyway** - Migración de base de datos
- **MapStruct** - Mapeo de objetos
- **Lombok** - Reducción de código boilerplate
- **OpenAPI 3** - Documentación de API
- **Redis** - Cache distribuido
- **JWT** - Autenticación y autorización

## 📋 Prerrequisitos

- Java 21 o superior
- Maven 3.8+
- MySQL 8.0+
- Redis (opcional para cache)

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/sistema-academico-enterprise.git
cd sistema-academico-enterprise
```

### 2. Configurar la base de datos

Crear la base de datos MySQL:

```sql
CREATE DATABASE academia_enterprise
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER 'academia_user'@'localhost' IDENTIFIED BY 'Academia2024!';
GRANT ALL PRIVILEGES ON academia_enterprise.* TO 'academia_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar variables de entorno

Crear archivo `.env` en la raíz del proyecto:

```env
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/academia_enterprise
DATABASE_USERNAME=academia_user
DATABASE_PASSWORD=Academia2024!

# JWT
JWT_SECRET=tuClaveSecretaSuperSegura2024

# Redis (opcional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Mail (opcional)
MAIL_PASSWORD=tuPasswordDeEmail
```

### 4. Ejecutar migraciones

```bash
mvn flyway:migrate
```

### 5. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api/v1`

## 📚 Documentación de la API

### Swagger UI
- **Desarrollo**: http://localhost:8080/api/v1/swagger-ui.html
- **Producción**: https://api.academia.edu/v1/swagger-ui.html

### Endpoints Principales

#### Estudiantes
- `POST /students` - Crear student
- `GET /students/{id}` - Obtener student por ID
- `GET /students/legajo/{legajo}` - Obtener student por legajo
- `GET /students` - Listar students con filtros
- `GET /students/grado/{grado}` - Listar students por grado
- `GET /students/estadisticas/grado` - Estadísticas por grado

#### Asistencias
- `POST /asistencias` - Registrar asistencia
- `PUT /asistencias/{id}` - Actualizar asistencia
- `GET /asistencias/student/{estudianteId}` - Asistencias por student
- `GET /asistencias/subject/{materiaId}` - Asistencias por subject
- `GET /asistencias/fecha/{fecha}` - Asistencias por fecha
- `GET /asistencias/student/{estudianteId}/subject/{materiaId}/porcentaje` - Calcular porcentaje

## 🏛️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/academia/
│   │   ├── application/
│   │   │   └── usecases/           # Casos de uso
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── entities/       # Entidades de dominio
│   │   │   │   └── valueobjects/   # Objetos de valor
│   │   │   └── ports/
│   │   │       ├── in/             # Puertos de entrada
│   │   │       └── out/            # Puertos de salida
│   │   └── infrastructure/
│   │       ├── config/             # Configuraciones
│   │       ├── persistence/
│   │       │   ├── adapters/       # Adaptadores de persistencia
│   │       │   └── repositories/   # Repositorios JPA
│   │       └── web/
│   │           ├── controllers/    # Controladores REST
│   │           ├── dto/            # Objetos de transferencia
│   │           ├── exception/      # Manejo de excepciones
│   │           └── mappers/        # Mapeadores MapStruct
│   └── resources/
│       ├── application.yml         # Configuración principal
│       └── db/migration/           # Migraciones Flyway
└── test/                           # Tests unitarios e integración
```

## 🧪 Testing

### Ejecutar tests

```bash
# Todos los tests
mvn test

# Tests con coverage
mvn jacoco:report

# Tests de arquitectura
mvn test -Dtest=ArchitectureTest
```

### Tipos de Tests

- **Unit Tests**: Pruebas de casos de uso y lógica de dominio
- **Integration Tests**: Pruebas de repositorios y controladores
- **Architecture Tests**: Pruebas de cumplimiento de arquitectura
- **Contract Tests**: Pruebas de contratos de API

## 📊 Monitoreo y Métricas

### Actuator Endpoints

- **Health Check**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Info**: `/actuator/info`

### Métricas Disponibles

- Tiempo de respuesta de endpoints
- Uso de memoria y CPU
- Conexiones de base de datos
- Cache hit/miss ratio

## 🔒 Seguridad

### Autenticación JWT

El sistema utiliza JWT para autenticación:

```bash
# Obtener token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Usar token
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/students
```

### Roles y Permisos

- **ADMIN**: Acceso completo al sistema
- **PROFESOR**: Gestión de subjects y asistencias
- **TUTOR**: Consulta de información de students
- **ESTUDIANTE**: Consulta de información personal

## 🚀 Despliegue

### Docker

```bash
# Construir imagen
docker build -t sistema-academico .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/academia_enterprise \
  sistema-academico
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=jdbc:mysql://db:3306/academia_enterprise
    depends_on:
      - db
      - redis
  
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: academia_enterprise
      MYSQL_USER: academia_user
      MYSQL_PASSWORD: Academia2024!
    ports:
      - "3306:3306"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

## 📈 Características Principales

### ✅ Implementadas

- [x] Arquitectura Hexagonal con DDD
- [x] Gestión de Estudiantes (CRUD completo)
- [x] Gestión de Asistencias
- [x] API REST documentada con OpenAPI
- [x] Validación de datos de entrada
- [x] Manejo global de excepciones
- [x] Migraciones de base de datos con Flyway
- [x] Mapeo de objetos con MapStruct
- [x] Logging estructurado
- [x] Configuración por perfiles (dev, test, prod)

### ✅ Completado

- [x] Gestión de Materias (CRUD completo)
- [x] Gestión de Profesores (CRUD completo)
- [x] Sistema de Evaluaciones (CRUD completo)
- [x] Autenticación JWT completa
- [x] Cache con Redis
- [x] Tests de integración básicos
- [x] Notificaciones por email

### 🚧 En Desarrollo

- [ ] Reportes y estadísticas avanzadas
- [ ] Dashboard administrativo
- [ ] API para móviles
- [ ] Integración con sistemas externos

### 📋 Pendientes

- [ ] Dashboard administrativo
- [ ] API para móviles
- [ ] Integración con sistemas externos
- [ ] Auditoría completa
- [ ] Backup automático
- [ ] Monitoreo avanzado

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 📞 Contacto

- **Email**: desarrollo@academia.edu
- **Sitio Web**: https://www.academia.edu
- **Documentación**: https://docs.academia.edu

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- La comunidad de DDD y Arquitectura Hexagonal
- Todos los contribuidores del proyecto

## 🚀 Instrucciones de Ejecución Rápida

### Prerrequisitos
- Java 21
- Maven 3.8+
- MySQL 8.0+
- Redis (opcional)

### Pasos para ejecutar

1. **Configurar base de datos MySQL:**
```sql
CREATE DATABASE academia_enterprise CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **Configurar application.yml** (ya configurado para desarrollo)

3. **Ejecutar migraciones automáticamente** (se ejecutan al iniciar la aplicación)

4. **Compilar y ejecutar:**
```bash
mvn clean compile
mvn spring-boot:run
```

5. **Acceder a la aplicación:**
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Usuarios de prueba disponibles:
- **admin** / **admin123** (ADMIN)
- **profesor1** / **admin123** (PROFESOR)
- **estudiante1** / **admin123** (ESTUDIANTE)
- **tutor1** / **admin123** (TUTOR)

### Endpoints principales:
- **Autenticación**: `POST /auth/login`
- **Estudiantes**: `GET /api/students`
- **Materias**: `GET /api/subjects`
- **Profesores**: `GET /api/profesores`
- **Evaluaciones**: `GET /api/evaluaciones`

### Notas importantes:
- El sistema está completamente funcional con todas las características implementadas
- Las migraciones se ejecutan automáticamente al iniciar
- Los datos de prueba se insertan automáticamente
- Redis es opcional, el sistema funciona sin él 