package es.refugio.frontend.web.util;

import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorMessageExtractor {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extract(Exception e) {
        if (e instanceof RestClientResponseException) {
            RestClientResponseException rce = (RestClientResponseException) e;
            try {
                String body = rce.getResponseBodyAsString();
                JsonNode root = mapper.readTree(body);
                
                StringBuilder sb = new StringBuilder();
                if (root.has("message") && !root.get("message").isNull()) {
                    sb.append(root.get("message").asText());
                }
                
                if (root.has("details") && !root.get("details").isNull() && root.get("details").isObject()) {
                    java.util.List<String> fieldErrors = new java.util.ArrayList<>();
                    root.get("details").fields().forEachRemaining(entry -> {
                        fieldErrors.add(entry.getValue().asText());
                    });
                    if (!fieldErrors.isEmpty()) {
                        if (sb.length() > 0) sb.append(": ");
                        sb.append(String.join(", ", fieldErrors));
                    }
                }
                
                if (sb.length() > 0) {
                    return sb.toString();
                }
            } catch (Exception ignored) {
            }
        }
        return e != null ? e.getMessage() : "Error desconocido";
    }
}
