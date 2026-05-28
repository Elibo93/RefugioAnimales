package es.refugio.common.infraestructure.web.error;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

//Clase que gestion una respuesta personalizada
@Data
@AllArgsConstructor
public class CustomResponse {

	private final LocalDateTime date;
	private final HttpStatus status;
	private final String message;
	private final Map<String, Object> details;

    public CustomResponse(LocalDateTime date, HttpStatus status, String message) {
        this.date = date;
        this.status = status;
        this.message = message;
        this.details = null;
    }
}




