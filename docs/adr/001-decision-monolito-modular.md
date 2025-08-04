# ADR-001: Evolución hacia un Monolito Modular

* **Fecha**: 2025-08-03
* **Estado**: Aceptado

## Contexto
El sistema necesita incorporar funcionalidades complejas (Finanzas, Pedagogía, Comunicaciones). Se ha evaluado la opción de migrar a una arquitectura de microservicios frente a evolucionar el monolito actual basado en arquitectura hexagonal.

## Decisión
Se ha decidido continuar con la arquitectura monolítica, evolucionándola hacia un **Monolito Modular**. Las nuevas funcionalidades se desarrollarán como módulos internos lógicos y desacoplados, respetando los límites de la arquitectura hexagonal ya establecida.

## Justificación
* **Velocidad de Desarrollo**: Permite al equipo centrarse en entregar valor de negocio en lugar de gestionar la complejidad operativa de una arquitectura distribuida (red, despliegues, consistencia de datos).
* **Bajo Riesgo**: La arquitectura actual ya fomenta un alto nivel de desacoplamiento a través de puertos, adaptadores y eventos de dominio, mitigando los problemas típicos de los monolitos tradicionales.
* **Preparado para el Futuro**: Si un módulo específico (ej: Notificaciones) crece y requiere escalar de forma independiente, sus límites claros y su bajo acoplamiento facilitarán enormemente su extracción a un microservicio en el futuro.