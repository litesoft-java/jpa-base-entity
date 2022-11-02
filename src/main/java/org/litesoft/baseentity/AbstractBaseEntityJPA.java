package org.litesoft.baseentity;

import java.util.UUID;

@javax.persistence.MappedSuperclass
@SuppressWarnings("SameParameterValue")
public abstract class AbstractBaseEntityJPA<T extends AbstractBaseEntityJPA<T>> extends AbstractBaseEntity<T> {
    @javax.persistence.Version
    private Long version; // default to null

    @javax.persistence.Id
    @javax.persistence.Column(unique = true, nullable = false)
    private UUID id;

    protected AbstractBaseEntityJPA( UUID givenId ) {
        id = (givenId != null) ? givenId : UUID.randomUUID();
    }

    @Override // Can't be final per Hibernate (JPA implementation)
    public UUID getId() {
        return id;
    }

    @Override // Can't be final per Hibernate (JPA implementation)
    public Long getVersion() {
        return version;
    }

    @Override
    void setVersion( Long version ) {
        this.version = version;
    }
}
