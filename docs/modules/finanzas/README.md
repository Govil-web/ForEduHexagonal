# Módulo de Finanzas

Este módulo gestiona todas las operaciones financieras del sistema, desde la creación de cargos hasta el registro de pagos.

## Modelo de Datos Clave
* **`fee_templates`**: Plantillas para cargos recurrentes (ej: "Matrícula 2025").
* **`fees`**: Un cargo financiero específico asignado a un estudiante, con un monto y fecha de vencimiento.
* **`payments`**: El registro de un pago realizado contra un cargo.

## Casos de Uso Principales
* Generar cargos de matrícula para todos los estudiantes de un grado.
* Registrar un pago online o manual.
* Consultar el estado de cuenta de un estudiante/tutor.