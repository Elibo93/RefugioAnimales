package es.refugio.common.infraestructure.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO Genérico para respuestas paginadas compartida por los microservicios.
 * @param <T> Tipo de los elementos en la lista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> items;
    private long total;
    private long count; // Alias para total (compatibilidad frontend)
    private int page;
    private int pageSize;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    /**
     * Crea una respuesta paginada a partir de un objeto Page de Spring Data.
     */
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .items(page.getContent())
                .total(page.getTotalElements())
                .count(page.getTotalElements())
                .page(page.getNumber() + 1) // Spring es 0-indexed, la web suele ser 1-indexed
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Permite mapear los elementos de una página manteniendo los metadatos de paginación.
     */
    public static <T, R> PaginatedResponse<R> fromPage(Page<T> page, List<R> items) {
        return PaginatedResponse.<R>builder()
                .items(items)
                .total(page.getTotalElements())
                .count(page.getTotalElements())
                .page(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
