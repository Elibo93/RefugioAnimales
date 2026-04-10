# Manual de Usuario - Gestión del Refugio
---

> **Versión:** 1.0 · **Proyecto:** Refugio de Animales · **Fecha:** Marzo 2026

---

## Puesta en Marcha

El sistema funciona mediante **Docker**, por lo que no requiere instalaciones complejas en su ordenador.

### Paso 1 — Lanzar el Sistema
Abra una terminal en la carpeta del proyecto y ejecute:
```bash
docker compose up -d
```
Espere a que los servicios de "API" y "Base de Datos" indiquen que están activos.

### Paso 2 — Acceso Web
Abra su navegador y acceda a:
```
https://localhost:8443/web/home
```
*(Nota: Al usar un certificado de desarrollo, deberá "Aceptar el riesgo" o "Continuar" en la advertencia de seguridad de su navegador).*

---

## Guía de Gestión

#### 1. Gestión de Animales
Desde el panel central, acceda a **Animales** para:
* **Ver listado:** Consulta todos los residentes actuales.
* **Nuevo Ingreso:** Registre un animal (nombre, especie, chip, salud).
* **Ficha Técnica:** Edite datos o actualice el estado a "Adoptado" o "En Acogida".

#### 2. Gestión de Voluntariado
Acceda a **Voluntarios** para administrar el equipo de cuidadores y asignarles animales para su seguimiento diario.

#### 3. Procesos de Adopción
En la sección de **Adopciones** puede:
* Revisar nuevas solicitudes de familias.
* Validar el proceso y generar el **Contrato de Adopción (PDF)**.
* Registrar visitas de seguimiento post-adopción.

---

[Volver](/README.md)
