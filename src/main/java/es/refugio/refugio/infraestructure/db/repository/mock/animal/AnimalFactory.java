package es.refugio.refugio.infraestructure.db.repository.mock.animal;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;

public class AnimalFactory {

    public static final Map<AnimalId, Animal> getDemoData() {

        Map<AnimalId, Animal> datos = new LinkedHashMap<>();

        datos.put(new AnimalId(10),
                Animal.builder()
                        .id(new AnimalId(10))
                        .nombre("Luna")
                        .especie(Especie.PERRO)
                        .raza("Labrador")
                        .sexo(Sexo.HEMBRA)
                        .chipId("CHIP001")
                        .estado(EstadoAnimal.DISPONIBLE)
                        .edad(3)
                        .tamano(Tamano.MEDIANO)
                        .descripcion("Muy tranquila")
                        .foto("http://example.com/luna.jpg")
                        .fechaIngreso(LocalDateTime.now())
                        .build());

        datos.put(new AnimalId(11),
                Animal.builder()
                        .id(new AnimalId(11))
                        .nombre("Max")
                        .especie(Especie.PERRO)
                        .raza("Pastor Alemán")
                        .sexo(Sexo.MACHO)
                        .chipId("CHIP002")
                        .estado(EstadoAnimal.DISPONIBLE)
                        .edad(5)
                        .tamano(Tamano.GRANDE)
                        .descripcion("Activo y juguetón")
                        .foto("http://example.com/max.jpg")
                        .fechaIngreso(LocalDateTime.now())
                        .build());

        datos.put(new AnimalId(12),
                Animal.builder()
                        .id(new AnimalId(12))
                        .nombre("Misu")
                        .especie(Especie.GATO)
                        .raza("Europeo")
                        .sexo(Sexo.HEMBRA)
                        .chipId("CHIP003")
                        .estado(EstadoAnimal.ADOPTADO)
                        .edad(2)
                        .tamano(Tamano.PEQUEÑO)
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
                .especie(Especie.PERRO)
                .raza("Mestizo")
                .sexo(Sexo.MACHO)
                .chipId("CHIP999")
                .estado(EstadoAnimal.DISPONIBLE)
                .edad(1)
                .tamano(Tamano.PEQUEÑO)
                .descripcion("Animal de prueba")
                .foto("http://example.com/nuevo.jpg")
                .fechaIngreso(LocalDateTime.now())
                .build();
    }
}