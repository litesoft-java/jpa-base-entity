package org.litesoft.baseentity;

import java.util.UUID;

@SuppressWarnings("SameParameterValue")
public abstract class AbstractBaseEntityDataJDBC<T extends AbstractBaseEntityDataJDBC<T>> extends AbstractBaseEntity<T> {
    @org.springframework.data.annotation.Version
    private Long version; // default to null

    @org.springframework.data.annotation.Id
    private UUID id;

    protected AbstractBaseEntityDataJDBC( UUID givenId ) {
        id = (givenId != null) ? givenId : UUID.randomUUID();
    }

    @Override
    public final UUID getId() {
        return id;
    }

    @Override
    public final Long getVersion() {
        return version;
    }

    @Override
    final void setVersion( Long version ) {
        this.version = version;
    }
}
