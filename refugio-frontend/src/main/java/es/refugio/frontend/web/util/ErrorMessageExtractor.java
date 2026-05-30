package es.refugio.frontend.web.util;

import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;

public class ErrorMessageExtractor {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extract(Exception e) {
        String body = null;
        if (e instanceof RestClientResponseException) {
            RestClientResponseException rce = (RestClientResponseException) e;
            body = rce.getResponseBodyAsString();
        } else if (e instanceof FeignException) {
            FeignException fe = (FeignException) e;
            body = fe.contentUTF8();
        }

        if (body != null && !body.isEmpty()) {
            try {
                JsonNode root = mapper.readTree(body);

                if (root.isArray()) {
                    List<String> arrayErrors = new ArrayList<>();
                    for (JsonNode node : root) {
                        if (node.isObject()) {
                            node.fields().forEachRemaining(entry -> {
                                arrayErrors.add(entry.getValue().asText());
                            });
                        } else {
                            arrayErrors.add(node.asText());
                        }
                    }
                    if (!arrayErrors.isEmpty()) {
                        return String.join(", ", arrayErrors);
                    }
                } else if (root.isObject()) {
                    StringBuilder sb = new StringBuilder();
                    if (root.has("message") && !root.get("message").isNull()) {
                        sb.append(root.get("message").asText());
                    }
                    
                    if (root.has("details") && !root.get("details").isNull() && root.get("details").isObject()) {
                        List<String> fieldErrors = new ArrayList<>();
                        root.get("details").fields().forEachRemaining(entry -> {
                            fieldErrors.add(entry.getValue().asText());
                        });
                        if (!fieldErrors.isEmpty()) {
                            if (sb.length() > 0) sb.append(": ");
                            sb.append(String.join(", ", fieldErrors));
                        }
                    }

                    if (sb.length() == 0) {
                        List<String> anyErrors = new ArrayList<>();
                        root.fields().forEachRemaining(entry -> {
                            if (!entry.getValue().isObject() && !entry.getValue().isArray()) {
                                anyErrors.add(entry.getValue().asText());
                            }
                        });
                        if (!anyErrors.isEmpty()) {
                            sb.append(String.join(", ", anyErrors));
                        }
                    }
                    
                    if (sb.length() > 0) {
                        return sb.toString();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return e != null ? e.getMessage() : "Error desconocido";
    }
}
