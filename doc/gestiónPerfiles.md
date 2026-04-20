### Gestión de Perfiles y Entornos - Refugio
---

El sistema del refugio está diseñado para ser flexible y seguro, adaptándose al entorno de ejecución mediante los **Perfiles de Spring Boot**.

#### 1. Estrategia de Configuración
Utilizamos tres ficheros para separar las preocupaciones:

* **`application.properties` (Base):** Configuración común invariable.
* **`application-dev.properties` (Desarrollo):** 
    * DB **H2** en memoria.
    * Consola de inspección habilitada.
    * Carga de datos de prueba (animales y voluntarios de ejemplo).
* **`application-prod.properties` (Producción):** 
    * Conexión a **MySQL** via Docker.
    * Seguridad reforzada y validación de esquema.
    * Logs optimizados para el servidor.

#### 2. Activación Dinámica
El cambio de perfil se realiza sin tocar el código, inyectando la variable `SPRING_PROFILES_ACTIVE`. Esto permite que el mismo software se comporte de forma ligera en el portátil de un desarrollador o de forma robusta en el servidor del refugio.

---

[Volver](/README.md)
