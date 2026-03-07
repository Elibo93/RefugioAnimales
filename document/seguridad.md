### Seguridad Integral - Gestión del Refugio
---

#### Diseño de Seguridad y Arquitectura de Autenticación

La seguridad del **Refugio de Animales** se implementa utilizando **Spring Security** como capa de protección transversal, integrada dentro de la **Arquitectura Clean** mediante el patrón de *Vertical Slicing*.

##### 1. Integración en Arquitectura Clean
La seguridad se trata como una responsabilidad de la capa de **Infraestructura**. El Dominio de animales y adopciones permanece agnóstico a Spring Security.

* **Vertical Slice:** Se ha definido un slice `auth` (`com.refugio.auth`) que encapsula la identidad corporativa y del voluntariado.
* **Separación de Responsabilidades:**
    * **Dominio:** Define `Usuario`, `Roles` y `Permisos` (ej. `ANIMAL_CREATE`, `ADOPTION_UPDATE`).
    * **Aplicación:** Interfaces para la gestión de usuarios del refugio.
    * **Infraestructura:** Configuración del `SecurityFilterChain` y `UserDetails`.

##### 2. Modelo de Dominio de Seguridad (RBAC)
Se ha diseñado un sistema de permisos granulares agrupados en roles adaptados a la operativa del centro:

1.  **Enum `Permiso`:** 
    * `ANIMAL_READ`, `ANIMAL_WRITE` (Gestión de fichas).
    * `VOLUNTEER_READ`, `VOLUNTEER_WRITE` (Gestión de personal).
    * `ADOPTION_READ`, `ADOPTION_WRITE` (Gestión de procesos).
2.  **Enum `Rol`:**
    * `ROLE_PUBLICO`: Lectura del catálogo de animales y solicitud de adopción.
    * `ROLE_VOLUNTARIO`: Gestión diaria de animales y tareas de cuidado.
    * `ROLE_ADMIN`: Control total (Gestión de usuarios, auditoría de adopciones).

##### 3. Flujo de Autenticación (Spring Security Flow)
El proceso de login sigue el flujo estándar adaptado a nuestra persistencia JPA, asegurando que cada voluntario o administrador acceda solo a sus funciones asignadas.

```mermaid
sequenceDiagram
    autonumber
    
    actor User as 👤 Voluntario / Admin
    
    box "Framework & Drivers" #f9f9f9
        participant Filter as SecurityFilterChain
        participant AuthMgr as AuthenticationManager
    end
    
    box "Infrastructure (Interface Adapters)" #e8f8f5
        participant Service as UserDetailService
        participant Mapper as UserMapper
    end
    
    box "Persistencia (Base de Datos)" #fff5e6
        participant Repo as UserRepository
        participant DB as MySQL (Contenedor)
    end

    %% Flujo
    User->>Filter: POST /login (credenciales)
    Filter->>AuthMgr: authenticate(user, pass)
    
    AuthMgr->>Service: loadUserByUsername(email)
    
    Service->>Repo: findByEmail(email)
    Repo->>DB: SELECT * FROM users WHERE...
    DB-->>Repo: ResultSet
    Repo-->>Service: Retorna UserEntity (JPA)
    
    rect rgb(200, 255, 200)
        Note over Service, Mapper: 🔄 ADAPTACIÓN DE DATOS (Clean Arch)
        Service->>Mapper: toUserDetails(UserEntity)
        Mapper-->>Service: Retorna UserAuth (Spring Security Safe)
    end
    
    Service-->>AuthMgr: Retorna UserDetails
    AuthMgr->>AuthMgr: checkPassword(BCrypt)
    
    alt Contraseña Correcta
        AuthMgr-->>Filter: Authentication Success
        Filter-->>User: 200 OK / Dashboard Refugio
    else Contraseña Incorrecta
        AuthMgr-->>Filter: BadCredentialsException
        Filter-->>User: 401 Unauthorized
    end
```

##### 4. Configuración de Seguridad
* **Rutas Públicas:** `/`, `/animales/catalogo`, `/login`, `/css/**`, `/js/**`.
* **Protección por Rol:**
    * `/admin/**` → Requiere `ROLE_ADMIN`.
    * `/voluntarios/**` → Accesible para `ROLE_ADMIN` y `ROLE_VOLUNTARIO`.
    * `/adopciones/**` → Escritura permitida para `ROLE_ADMIN`, lectura para otros roles.

---

#### Seguridad de Infraestructura y Red

Siguiendo el principio de **Defensa en Profundidad**, la base de datos del refugio reside en una subred privada de Docker, aislada de accesos externos directos. Solo el backend de la aplicación tiene visibilidad sobre el contenedor `db`.

---

[Volver](/README.md)
