package es.refugio.animales.refugio.infraestructure.db.repository.mock.animal;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;

public class AnimalFactory {

    public static final Map<AnimalId, Animal> getDemoData() {

        Map<AnimalId, Animal> datos = new LinkedHashMap<>();

        datos.put(new AnimalId(10),
                Animal.builder()
                        .id(new AnimalId(10))
                        .nombre("Luna")
                        .especie("Perro")
                        .raza("Labrador")
                        .sexo("Hembra")
                        .chipId("CHIP001")
                        .estado("Disponible")
                        .notas("Muy tranquila")
                        .createdAt(LocalDateTime.now())
                        .build());

        datos.put(new AnimalId(11),
                Animal.builder()
                        .id(new AnimalId(11))
                        .nombre("Max")
                        .especie("Perro")
                        .raza("Pastor Alemán")
                        .sexo("Macho")
                        .chipId("CHIP002")
                        .estado("Disponible")
                        .notas("Activo y juguetón")
                        .createdAt(LocalDateTime.now())
                        .build());

        datos.put(new AnimalId(12),
                Animal.builder()
                        .id(new AnimalId(12))
                        .nombre("Misu")
                        .especie("Gato")
                        .raza("Europeo")
                        .sexo("Hembra")
                        .chipId("CHIP003")
                        .estado("Adoptado")
                        .notas("Muy cariñosa")
                        .createdAt(LocalDateTime.now())
                        .build());

        return datos;
    }

    public static Animal create() {

        return Animal.builder()
                .id(new AnimalId(99))
                .nombre("Nuevo")
                .especie("Perro")
                .raza("Mestizo")
                .sexo("Macho")
                .chipId("CHIP999")
                .estado("Disponible")
                .notas("Animal de prueba")
                .createdAt(LocalDateTime.now())
                .build();
    }
}