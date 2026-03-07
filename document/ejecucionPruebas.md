### Ejecución de las Pruebas y Validación - Refugio de Animales
---

La fase de ejecución valida que los endpoints de la API del refugio cumplen con los procesos de negocio definidos, asegurando la integridad de los datos de animales y adoptantes.

#### Metodología de Ejecución
Se han realizado pruebas manuales sistemáticas utilizando **Postman** sobre el perfil de desarrollo (`dev`) con base de datos **H2**.

---

#### 1. Módulo: Animales
| ID Caso | Operación | Datos de Entrada | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **CP-ANI-01** | `POST create()` | JSON de nuevo animal. | `201 Created` + Nuevo ID. | ✔ PASS |
| **CP-ANI-02** | `GET findAll()` | - | Lista de animales en el refugio. | ✔ PASS |
| **CP-ANI-03** | `PUT update()` | ID existente, nuevo estado. | `200 OK` + Animal actualizado. | ✔ PASS |

#### 2. Módulo: Adopciones (Procesos de Negocio)
| ID Caso | Operación | Escenario | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **CP-ADO-01** | `POST request()` | Solicitud válida. | `201 Created` (Pnd. Validación). | ✔ PASS |
| **CP-ADO-02** | `POST request()` | Animal ya adoptado. | `409 Conflict`. | ✔ PASS |
| **CP-ADO-03** | `PUT validate()` | Aprobación por Admin. | `200 OK` + Estado "Finalizada". | ✔ PASS |

---

#### Resumen de Resultados
* **Total ejecutados:** 20 casos críticos.
* **Exitosos:** 20.
* **Cobertura:** Validación de los flujos de salud, voluntariado y contratos legales.

---

[Volver](/README.md)
