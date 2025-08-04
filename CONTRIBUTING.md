# Guía de Contribución al SAE

Gracias por tu interés en contribuir. Para asegurar un flujo de trabajo ágil y de calidad, por favor sigue estos pasos.

## Flujo de Trabajo
1.  **Asigna una Tarea**: Asegúrate de tener una tarea o issue asignado desde el sistema de gestión de proyectos.
2.  **Crea una Rama**: Crea una nueva rama desde `dev` siguiendo la convención `feature/TICKET-ID-descripcion-corta`.
3.  **Desarrolla en Local**: Escribe tu código siguiendo las directrices del **[Manifiesto de Buenas Prácticas](./manifiesto-buenas-practicas.md)**.
4.  **Añade Pruebas**: Toda nueva lógica debe estar cubierta por pruebas unitarias o de integración.
5.  **Documenta tu Cambio**: Actualiza o crea la documentación necesaria en la carpeta `/docs`. Una funcionalidad sin documentación no está completa.
6.  **Crea una Pull Request (PR)**: Haz una PR contra la rama `dev`. En la descripción, enlaza el ticket y explica claramente qué hace tu cambio.
7.  **Espera la Revisión**: Al menos un miembro del equipo debe revisar y aprobar tu PR antes de que pueda ser fusionada.

## Checklist de la Pull Request
- [ ] El código compila y pasa todas las pruebas existentes.
- [ ] Se han añadido nuevas pruebas para cubrir la funcionalidad.
- [ ] La documentación ha sido actualizada.
- [ ] La rama está actualizada con los últimos cambios de `dev`.
- [ ] El título y la descripción de la PR son claros.