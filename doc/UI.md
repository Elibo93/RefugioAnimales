### Vista (UI) - Refugio de Animales
---

La interfaz de usuario sigue el patrón **MVC**, utilizando **Thymeleaf** para el renderizado en servidor. Se integra en la arquitectura como un **Web Adapter**, asegurando un diseño limpio y responsive.

---

#### 1. Tecnologías y Enfoque
* **Motor de Plantillas:** Thymeleaf en Spring Boot.
* **Estilo:** CSS vainilla (`estilos.css`) con variables para una identidad visual propia del refugio. Tipografía **Plus Jakarta Sans** e iconos **Lucide**.
* **Responsive:** Diseño adaptable a tablets (uso en patio/jaulas) y equipos de oficina.

---

#### 2. Mapa de Navegación y Vistas

- ##### Módulo: Gestión de Animales
    Centraliza la trazabilidad de cada residente del refugio.

    | ID Vista | Nombre de la Vista | Descripción Funcional |
    | :--- | :--- | :--- |
    | **V-ANI-01** | **Listado de Residentes** | Tabla con animales, especie, estado (Disponible/Acogida) y acciones rápidas. |
    | **V-ANI-02** | **Alta de Animal** | Formulario de ingreso (Chip, nombre, salud inicial, fecha). |
    | **V-ANI-03** | **Ficha Técnica (Edición)** | Modal para actualizar salud, peso o estado de adopción. |
    | **V-ANI-04** | **Certificado de Ingreso** | Exportación PDF con los datos base del animal. |

- ##### Módulo: Gestión de Personal (Voluntarios)
    Administración de los cuidadores y colaboradores.

    | ID Vista | Nombre de la Vista | Descripción Funcional |
    | :--- | :--- | :--- |
    | **V-VOL-01** | **Lista de Voluntarios** | Gestión de contacto y disponibilidad. |
    | **V-VOL-02** | **Registro de Voluntario** | Formulario para nuevas incorporaciones. |

- ##### Módulo: Procesos de Adopción
    Gestión del flujo desde la solicitud hasta el contrato final.

    | ID Vista | Nombre de la Vista | Descripción Funcional |
    | :--- | :--- | :--- |
    | **V-ADO-01** | **Panel de Solicitudes** | Listado de trámites pendientes de revisión. |
    | **V-ADO-02** | **Formulario de Solicitud** | Captura de datos del adoptante y animal de interés. |
    | **V-ADO-03** | **Contrato de Adopción** | Generación de PDF legal tras la validación administrativa. |

---

#### 3. Integración con el Backend
Las vistas se alimentan de objetos `Model` proporcionados por los controladores, que a su vez se comunican con los **Casos de Uso** (ej. `RegistrarEntradaAnimal`). Las validaciones se muestran al usuario de forma integrada en el formulario.

---

[Volver](/README.md)
