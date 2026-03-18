package es.refugio.refugio.infraestructure.db.repository.mock.animal;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;

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
                        .edad(3)
                        .tamano("Mediano")
                        .descripcion("Muy tranquila")
                        .foto("http://example.com/luna.jpg")
                        .fechaIngreso(LocalDateTime.now())
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
                        .edad(5)
                        .tamano("Grande")
                        .descripcion("Activo y juguetón")
                        .foto("http://example.com/max.jpg")
                        .fechaIngreso(LocalDateTime.now())
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
                        .edad(2)
                        .tamano("Pequeño")
                        .descripcion("Muy cariñosa")
                        .foto("http://example.com/misu.jpg")
                        .fechaIngreso(LocalDateTime.now())
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
                .edad(1)
                .tamano("Pequeño")
                .descripcion("Animal de prueba")
                .foto("http://example.com/nuevo.jpg")
                .fechaIngreso(LocalDateTime.now())
                .build();
    }
}