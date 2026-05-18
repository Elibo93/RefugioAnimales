package es.refugio.common.domain.repository;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T, ID> {

    public T save(T t);

    public List<T> getAll();

    public default org.springframework.data.domain.Page<T> findAll(org.springframework.data.domain.Pageable pageable) {
        return org.springframework.data.domain.Page.empty();
    }

    public Optional<T> getById(ID id);

    public void deleteById(ID id);

}

















