package es.refugio.refugio.infraestructure.db.repository.mock.adoptante;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;

public class AdoptanteRepositoryMockImpl implements AdoptanteRepository {

    private final Map<AdoptanteId, Adoptante> adoptantes = AdoptanteFactory.getDemoData();

    @Override
    public Adoptante save(Adoptante i) {
        // Lógica de autoincremento para Mocks
        if (i.getId() == null) {
            i.setId(new AdoptanteId(obtenerSiguienteId()));
        }

        adoptantes.put(i.getId(), i);
        return i;
    }

    private int obtenerSiguienteId() {
        AdoptanteId ultimo = null;
        if (!adoptantes.isEmpty()) {
            Collection<Adoptante> lista = adoptantes.values();
            for (Adoptante p : lista) {
                ultimo = p.getId();
            }
        }
        return (ultimo != null ? ultimo.getValue() + 1 : 1);
    }

    @Override
    public List<Adoptante> getAll() {
        return new ArrayList<>(adoptantes.values());
    }

    @Override
    public Optional<Adoptante> getById(AdoptanteId id) {
        return Optional.ofNullable(adoptantes.get(id));
    }

    @Override
    public void deleteById(AdoptanteId id) {
        adoptantes.remove(id);
    }

    @Override
    public Optional<Adoptante> getByDni(String dni) {
        return adoptantes.values().stream()
                .filter(a -> a.getDni().equalsIgnoreCase(dni))
                .findFirst();
    }

    @Override
    public Optional<Adoptante> getByUsuarioId(UsuarioId usuarioId) {
        return adoptantes.values().stream()
                .filter(a -> a.getUsuarioId().equals(usuarioId))
                .findFirst();
    }

    // Métodos específicos útiles para el dominio de Adoptante
    
    public Optional<Adoptante> getByUsuarioId(Integer usuarioId) {
        return adoptantes.values().stream()
                .filter(a -> a.getUsuarioId().equals(usuarioId))
                .findFirst();
    }

    public List<Adoptante> getByEstado(String estado) {
        List<Adoptante> result = new ArrayList<>();
        for (Adoptante a : adoptantes.values()) {
            if (a.getEstadoValidacion() != null && a.getEstadoValidacion().name().equalsIgnoreCase(estado)) {
                result.add(a);
            }
        }
        return result;
    }
}