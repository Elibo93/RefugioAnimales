package es.refugio.common.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CRUDRepository<T, ID> {

    public T save(T t);

    public List<T> getAll();

    public default Page<T> findAll(Pageable pageable) {
        return Page.empty();
    }

    public Optional<T> getById(ID id);

    public void deleteById(ID id);

}

















