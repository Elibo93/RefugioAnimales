package es.refugio.frontend.web.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> items,
        int totalPages,
        long total,
        int page,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {}
