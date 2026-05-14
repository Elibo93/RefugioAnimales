# Manual de Usuario - Gestión del Refugio
---

> **Versión:** 1.1 · **Proyecto:** Refugio de Animales · **Fecha:** Mayo 2026
> Este manual proporciona las instrucciones necesarias para la puesta en marcha y operación diaria del sistema de gestión del refugio.

---

## 1. Instalación y Puesta en Marcha

El sistema está contenedorizado con **Docker**, lo que garantiza que funcione de la misma manera en cualquier equipo sin instalar dependencias adicionales (Java, MySQL, etc.).

### 1.1. Iniciar el sistema
1. Abra una terminal en la raíz del proyecto.
2. Ejecute el comando:
   ```bash
   docker compose up -d
   ```
3. Verifique que los contenedores estén corriendo:
   ```bash
   docker compose ps
   ```

### 1.2. Detener el sistema
Para apagar el servidor y liberar recursos:
```bash
docker compose stop
```
Si desea borrar los contenedores (sin perder los datos de la base de datos si están en volúmenes):
```bash
docker compose down
```

### 1.3. Acceso a la Aplicación
Abra su navegador (se recomienda Chrome o Firefox) y acceda a:
**[https://localhost:8443/web/home](https://localhost:8443/web/home)**

> [!IMPORTANT]
> Al acceder por primera vez, verá una advertencia de "Conexión no privada". Esto es normal en entornos de desarrollo. Haga clic en **Configuración avanzada** y luego en **Continuar a localhost (no seguro)**.

---

## 2. Guía de Módulos Operativos

### 2.1. Gestión de Animales (Inventario Vivo)
Es el núcleo del sistema. Permite mantener un registro exhaustivo de cada animal.
- **Registro de Entrada:** Use el botón "Nuevo Ingreso". Es vital completar el número de **Microchip** y la **Fecha de Entrada**.
- **Ficha Médica:** Dentro de cada animal, puede añadir observaciones de salud que se guardarán cronológicamente.
- **Exportación:** Puede generar un **Certificado de Ingreso en PDF** desde la vista detallada del animal para entregarlo a las autoridades o adoptantes.

### 2.2. Gestión de Voluntariado e Intervenciones
Orientado a la operativa diaria del refugio.
- **Asignación:** Los administradores pueden asignar animales específicos a voluntarios para su cuidado.
- **Tareas Diarias:** Los voluntarios ven su lista de tareas pendientes (limpieza, medicación, paseo). Al completar una, el sistema registra quién y cuándo la realizó.

### 2.3. Proceso de Adopción y Contratos
Gestiona el flujo legal desde que entra una solicitud hasta que el animal sale del refugio.
1. **Revisión de Solicitudes:** Aparecerán en el panel de control. Se pueden filtrar por estado (Pendiente, En Estudio, Aprobada).
2. **Generación de Contrato:** Una vez aprobada, el sistema permite descargar el **Contrato de Adopción Legal** autocompletado con los datos del adoptante y el animal.

---

## 3. Roles y Seguridad

El sistema adapta su interfaz según quién lo use:
- **Administrador:** Acceso total a configuración, usuarios y estadísticas.
- **Voluntario:** Vista simplificada centrada en el cuidado animal y tareas.
- **Adoptante:** Acceso al catálogo interactivo, realización de solicitudes y seguimiento de sus trámites.
- **Visitante (Público):** Exploración del catálogo público y registro inicial.

> [!TIP]
> **Cambio de Rol:** Si su usuario tiene permisos duales (ej. Admin y Voluntario), encontrará un selector en la parte superior derecha para cambiar de vista sin cerrar sesión.

---

## 4. Resolución de Problemas Comunes

| Problema | Causa Posible | Solución |
| :--- | :--- | :--- |
| **No carga la página** | Docker no ha arrancado | Ejecute `docker compose up -d` de nuevo. |
| **Error de base de datos** | El puerto 3306 está ocupado | Detenga cualquier instancia local de MySQL. |
| **PDF no se genera** | Error de permisos | Asegúrese de que la carpeta `/tmp` o la definida para informes es escribible. |
| **Login incorrecto** | Sesión expirada | Limpie las cookies del navegador o use una ventana de incógnito. |

---

[Volver al Índice de Documentación](/README.md)

