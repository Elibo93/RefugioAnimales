### Diagrama de Navegación - Flujo del Refugio
---

Este documento actúa como el **plano de la interfaz de usuario**. Explica cómo se organizan las pantallas, qué caminos puede seguir cada usuario (User Journey) y cómo se segregan las funcionalidades según los permisos (RBAC - Role Based Access Control).

---

#### Mapa de Navegación (Mermaid)

```mermaid
graph TD
    %% --- ZONA PÚBLICA (ANÓNIMA) ---
    subgraph "Navegación: VISITANTE"
        Start((Inicio)) --> Landing[Landing Page]
        Landing --> CatalogPub[Catálogo Público]
        CatalogPub --> AnimalDetPub[Ficha Animal (Lectura)]
        Landing --> Register[Formulario de Registro]
        Landing --> Login[Acceso Usuarios]
    end

    Login -- Auth OK --> Router{¿Perfil de Usuario?}

    %% --- COMPONENTES GLOBALES ---
    subgraph Global [Estructura Común]
        Header[Navbar / Campana Notificaciones]
        NotifCenter[Centro de Notificaciones]
        Header --> NotifCenter
    end

    %% --- CARRIL ADMINISTRADOR ---
    subgraph "Nivel: ADMINISTRADOR"
        Router -- ROLE_ADMIN --> DashAdmin[Dashboard Admin]
        DashAdmin --> NavAnimal[Gestión de Animales]
        NavAnimal --> ViewListAn[Listado y Filtros]
        ViewListAn --> ViewFormNew[Alta Animal]
        ViewListAn --> ViewFormEdit[Edición / Ficha Técnica]
        
        DashAdmin --> NavAdopc[Validación de Solicitudes]
        DashAdmin --> NavVolun[Gestión de Voluntarios]
        DashAdmin --> NavDonat[Control de Donaciones]
    end

    %% --- CARRIL VOLUNTARIO ---
    subgraph "Nivel: VOLUNTARIO"
        Router -- ROLE_VOLUNTARIO --> DashVolun[Dashboard Voluntario]
        DashVolun --> MyTasks[Gestión de Tareas]
        MyTasks --> ActionTask[Marcar Progreso / Finalizar]
        
        DashVolun --> AnimalCare[Cuidado Animal]
        AnimalCare --> HistMed[Consultar/Añadir Historial Médico]
    end

    %% --- CARRIL ADOPTANTE ---
    subgraph "Nivel: ADOPTANTE"
        Router -- ROLE_PUBLICO --> DashAdopt[Dashboard Adoptante]
        DashAdopt --> CatalogPriv[Catálogo Interactivo]
        CatalogPriv --> AnimalDetPriv[Ficha y Solicitud]
        AnimalDetPriv --> ActionRequest[Enviar Formulario Adopción]
        
        DashAdopt --> MyRequests[Mis Solicitudes y Trámites]
        DashAdopt --> DonatFlow[Módulo de Donaciones]
    end

    %% --- DUALIDAD VOLUNTARIO-ADOPTANTE ---
    subgraph "Estado: DUAL (Mix)"
        DashAdopt <-->|Cambiar a Panel Voluntario| DashVolun
        note[Usuario con ambos roles asignados]
    end

    %% --- SALIDA ---
    DashAdmin & DashVolun & DashAdopt --> Logout[Cerrar Sesión]
    Logout --> Landing

    %% Estilos
    style Global fill:#f8f9fa,stroke:#dee2e6,stroke-dasharray: 5 5
    style Router fill:#f9f,stroke:#333
    style Start fill:#e3f2fd,stroke:#2196f3
    style note fill:#fff,stroke:#333,stroke-dasharray: 2 2
```

---

#### 📋 Resumen de Navegación por Perfil (Versión Texto)

| Perfil | Acción Principal | Destino / Funcionalidad |
| :--- | :--- | :--- |
| **Visitante** | Explorar | Catálogo Público (sin necesidad de cuenta) |
| **Visitante** | Unirse | Registro para ser Adoptante o Voluntario |
| **Adoptante** | Adoptar | Catálogo Interactivo + Envío de Solicitudes |
| **Adoptante** | Colaborar | Pasarela de Donaciones |
| **Voluntario** | Operar | Gestión de Tareas Diarias y Salud Animal |
| **Admin** | Supervisar | Validación de Solicitudes y Gestión de Miembros |
| **Dual** | Alternar | Cambio de contexto entre gestión y adopción |
| **Cualquier Logueado** | Notificar | Centro de Alertas (Campana) |

---

#### Explicación de los Flujos Principales

1.  **Navegación Anónima (Visitante)**: Punto de entrada principal. El usuario puede explorar la **Landing Page** y el **Catálogo Público** (solo lectura). Desde aquí puede iniciar el proceso de **Registro**, donde deberá elegir su perfil inicial (Adoptante o solicitante de Voluntariado).
2.  **Flujo de Adopción (Adoptante)**: Una vez logueado, el usuario accede a un catálogo interactivo con filtros avanzados. Al seleccionar un animal, accede a su ficha técnica y puede enviar una **Solicitud de Adopción**. Desde su panel personal ("Mis Trámites"), realiza el seguimiento del estado (Pendiente, Aprobada, Rechazada) y puede acceder al **Módulo de Donaciones** para colaborar con el refugio.
3.  **Gestión de Voluntariado (Operativa)**: El voluntario visualiza su "Mochila de Tareas" asignada por el Admin. Puede marcar el progreso de las tareas diarias, registrar **Incidencias de Salud** de los animales y consultar/añadir entradas al **Historial Médico** de los mismos, asegurando la trazabilidad del cuidado animal.
4.  **Panel de Administración (Control Global)**: El administrador supervisa toda la operativa. Valida nuevos miembros, aprueba o rechaza solicitudes de adopción (lo que dispara notificaciones automáticas), gestiona el inventario de animales y asigna tareas específicas a los voluntarios según las necesidades del refugio.
5.  **Dualidad de Roles (Conmutación)**: El sistema permite que un usuario posea los roles de Voluntario y Adoptante simultáneamente. En el Header aparecerá un selector de rol que permite cambiar el Dashboard activo sin necesidad de cerrar sesión, manteniendo los trámites personales separados de las tareas de voluntariado.
6.  **Notificaciones Transversales**: Sistema de alertas en tiempo real presente en todos los perfiles logueados. Notifica sobre nuevas tareas, cambios en el estado de adopciones, avisos de urgencia animal o confirmación de donaciones recibidas.

---

[Volver](/README.md)
