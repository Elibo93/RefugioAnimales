package es.refugio.common.domain.error;

public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Integer entityId;

    public EntityNotFoundException(String entity) {
        super("%s no encontrado".formatted(entity));
        this.entityName = entity;
        this.entityId = null;
    }

    public EntityNotFoundException(String entity, int id) {
        super("%s con id %s no se encuentra".formatted(entity, id));
        this.entityName = entity;
        this.entityId = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public Integer getEntityId() {
        return entityId;
    }
}

















