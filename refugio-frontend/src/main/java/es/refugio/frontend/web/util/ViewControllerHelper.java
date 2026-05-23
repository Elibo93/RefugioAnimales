package es.refugio.frontend.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.refugio.frontend.web.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

@Component
@RequiredArgsConstructor
public class ViewControllerHelper {

    private static final Logger logger = LoggerFactory.getLogger(ViewControllerHelper.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    /**
     * Hace un fetch de una lista de objetos y los mapea fuertemente a una clase/record.
     */
    public <T> List<T> fetchList(String url, Class<T> type) {
        try {
            Object response = restTemplate.getForObject(url, Object.class);
            if (response instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) response;
                if (map.containsKey("items")) {
                    List<?> items = (List<?>) map.get("items");
                    return items.stream()
                            .map(item -> objectMapper.convertValue(item, type))
                            .toList();
                }
            } else if (response instanceof List) {
                List<?> list = (List<?>) response;
                return list.stream()
                        .map(item -> objectMapper.convertValue(item, type))
                        .toList();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Hace un fetch paginado y lo mapea al DTO PaginatedResponse.
     */
    @SuppressWarnings("unchecked")
    public <T> PaginatedResponse<T> fetchPaginated(String baseUrl, int page, int size, Class<T> type) {
        try {
            String delimiter = baseUrl.contains("?") ? "&" : "?";
            String finalUrl = baseUrl + delimiter + "page=" + (page - 1) + "&size=" + size;
            Map<String, Object> response = restTemplate.getForObject(finalUrl, Map.class);
            if (response == null) {
                return new PaginatedResponse<>(Collections.emptyList(), 0, 0, page, size, false, false);
            }

            List<?> itemsRaw = (List<?>) response.getOrDefault("items", response.getOrDefault("content", Collections.emptyList()));
            List<T> items = itemsRaw.stream()
                    .map(item -> objectMapper.convertValue(item, type))
                    .toList();

            Object totalRaw = response.getOrDefault("total", response.getOrDefault("totalElements", response.getOrDefault("totalItems", 0)));
            long total = (totalRaw instanceof Number) ? ((Number) totalRaw).longValue() : 0L;

            Object totalPagesRaw = response.getOrDefault("totalPages", 0);
            int totalPages = (totalPagesRaw instanceof Number) ? ((Number) totalPagesRaw).intValue() : 0;

            boolean hasNext = response.containsKey("hasNext") ? Boolean.TRUE.equals(response.get("hasNext")) : !Boolean.TRUE.equals(response.get("last"));
            boolean hasPrevious = response.containsKey("hasPrevious") ? Boolean.TRUE.equals(response.get("hasPrevious")) : !Boolean.TRUE.equals(response.get("first"));

            return new PaginatedResponse<>(items, totalPages, total, page, size, hasNext, hasPrevious);
        } catch (Exception e) {
            logger.error("[fetchPaginated] Error fetching {}: {}", baseUrl, e.getMessage(), e);
            return new PaginatedResponse<>(Collections.emptyList(), 0, 0, page, size, false, false);
        }
    }

    /**
     * Deserializa un objeto individual a una clase/record específica.
     */
    public <T> T fetchObject(String url, Class<T> type) {
        try {
            return restTemplate.getForObject(url, type);
        } catch (Exception e) {
            return null;
        }
    }
}
