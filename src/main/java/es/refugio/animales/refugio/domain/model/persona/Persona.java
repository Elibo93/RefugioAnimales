package es.refugio.animales.refugio.domain.model.persona;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Persona {

	// Atributos
	private PersonaId id;
	private String dni;
	private String nombre;
	private String apellido;
	private String email;
	private String telefono;
	private String direccion;
	private String fechaNacimiento;
	private LocalDateTime createdAt;

}

















