### Organización de Paquetes y Clases - Refugio
---

La estructura del código sigue los principios de **Clean Architecture**, asegurando que la lógica de bienestar animal sea el centro del sistema, independiente de la tecnología web o de base de datos elegida.

#### 1. Estructura de Paquetes
```text
es.refugio.animales
├── common                      # Utilidades transversales e interfaces base.
├── refugio                     # Núcleo del Sistema (Bounded Context).
│   ├── application             # Casos de Uso (ej: SolicitarAdopcion).
│   ├── domain                  # Entidades Puras (Animal, Persona, Adopcion).
│   └── infraestructure         # Adaptadores (REST, JPA Refugio, Config).
└── vista                       # Adaptador para Interfaz Web THYMELEAF.
```

#### 2. Reutilización Multicanal
Gracias a esta organización, el mismo servicio de aplicación (`GestionadorAdopciones`) alimenta:
* **Controladores REST:** Para integraciones JSON.
* **Controladores MVC:** Para la administración web del refugio mediante Thymeleaf.

#### 3. Diagrama de Componentes
El dominio rige el sistema. Los adaptadores (Web y DB) se conectan mediante **Puertos e Interfaces**, permitiendo cambiar el motor de base de datos o la interfaz de usuario sin poner en riesgo la lógica de negocio del refugio.

---

[Volver](/README.md)
