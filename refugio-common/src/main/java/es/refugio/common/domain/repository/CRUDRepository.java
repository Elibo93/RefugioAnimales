package es.refugio.common.domain.repository;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T, ID> {

    public T save(T t);

    public List<T> getAll();

    public Optional<T> getById(ID id);

    public void deleteById(ID id);

}

















