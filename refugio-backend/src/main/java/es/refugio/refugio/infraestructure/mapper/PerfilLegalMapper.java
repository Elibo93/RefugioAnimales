package es.refugio.refugio.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegalId;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalRequest;
import es.refugio.refugio.infraestructure.web.dto.perfil_legal.PerfilLegalResponse;

@Mapper(componentModel = "spring")
public interface PerfilLegalMapper {

    @Mapping(target = "id", ignore = true)
    PerfilLegal toDomain(PerfilLegalRequest request);

    @Mapping(target = "id", source = "id", qualifiedByName = "integerToPerfilLegalId")
    PerfilLegal toDomain(PerfilLegalEntity entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "perfilLegalIdToInteger")
    PerfilLegalEntity toEntity(PerfilLegal domain);

    PerfilLegalResponse toResponse(PerfilLegal perfil);

    @Named("perfilLegalIdToInteger")
    default Integer perfilLegalIdToInteger(PerfilLegalId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("integerToPerfilLegalId")
    default PerfilLegalId integerToPerfilLegalId(Integer id) {
        return id != null ? new PerfilLegalId(id) : null;
    }
}
