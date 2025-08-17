# 🔐 CREDENCIALES DE SEGURIDAD ACTUALIZADAS

⚠️ **IMPORTANTE**: Las credenciales por defecto han sido actualizadas por motivos de seguridad.

## 📋 NUEVAS CREDENCIALES

### Sistema (Super Admin)
- **Email**: `superadmin@plataforma.com`
- **Contraseña**: `SecureAdm!n2024$`

### Universidad del Futuro (unifuturo)
- **Admin**: `admin@unifuturo.edu` / `AdminUni#2024!`
- **Profesor**: `profesor@unifuturo.edu` / `TeachUni@2024`
- **Estudiante**: `estudiante@unifuturo.edu` / `StudUni$2024`

### Colegio Primaria Feliz (primariafeliz)
- **Admin**: `admin@primariafeliz.edu` / `AdminPrim#2024`
- **Profesor**: `profesor@primariafeliz.edu` / `TeachPrim@2024`
- **Tutor**: `tutor@email.com` / `TutorGuard!2024`

## 🔒 NUEVAS POLÍTICAS DE SEGURIDAD

### Configuración de Contraseñas
- **Longitud mínima**: 12 caracteres
- **Complejidad**: Mayúsculas, minúsculas, números y caracteres especiales
- **Expiración**: 90 días
- **Historial**: No reutilizar últimas 5 contraseñas

### Intentos de Login
- **Máximo intentos**: 5 por usuario
- **Bloqueo**: 30 minutos tras exceder límite
- **Auditoría**: Todos los intentos registrados

### Sesiones
- **Timeout**: 60 minutos de inactividad
- **Token expiry**: 15 minutos (access), 7 días (refresh)

## ⚠️ RECOMENDACIONES

1. **Cambiar contraseñas**: Modificar estas credenciales antes de producción
2. **Habilitar 2FA**: Para usuarios administrativos
3. **Monitoreo**: Revisar logs de auditoría regularmente
4. **Rotación**: Implementar rotación automática de secrets

## 🔍 AUDITORÍA

Todos los cambios de contraseñas se registran en la tabla `password_audit_log` con:
- Timestamp del cambio
- Usuario que realizó la acción
- IP y User-Agent
- Tipo de acción (CHANGED, RESET, EXPIRED)