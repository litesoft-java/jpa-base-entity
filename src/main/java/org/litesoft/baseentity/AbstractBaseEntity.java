package org.litesoft.baseentity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.litesoft.fields.Equivalance;
import org.litesoft.fields.FieldAccessors;
import org.litesoft.fields.ToStringBuilder;
import org.litesoft.utils.Cast;

@MappedSuperclass
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractBaseEntity<T extends AbstractBaseEntity<T>> implements BaseEntity {
    protected static <T extends AbstractBaseEntity<T>> FieldAccessors<T> createWithCommon( FieldAccessors<T> fas ) {
        return fas
                .auto( "version", AbstractBaseEntity::getVersion ).withType( Long.class )
                .required( "id", AbstractBaseEntity::getId ).withType( UUID.class ).addMetaData( "generated, if missing" )
                ;
    }

    @Transient
    private final Class<T> type;

    @Version
    private Long version; // default to null

    @Id
    @Column(unique = true, nullable = false)
    private UUID id;

    protected AbstractBaseEntity( Class<T> type, UUID givenId ) {
        this.type = type;
        id = (givenId != null) ? givenId : UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    void setVersion( Long version ) {
        this.version = version;
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    @Override
    public final boolean equals( Object o ) {
        return (o == this) ||
               ((o instanceof AbstractBaseEntity) && equals( (AbstractBaseEntity<?>)o ));
    }

    public final boolean equals( AbstractBaseEntity<?> them ) {
        return (them == this) ||
               (sameTypes( this, them ) && (Objects.equals( this.id, them.id )));
    }

    @SuppressWarnings("unused")
    public final boolean isEqualLessVersion( T them ) {
        return Equivalance.mostly( sameTypes( this, them ), us(), them, fas(), 1 ); // Skip Version !
    }

    public final boolean isEquivalent( T them ) {
        return Equivalance.mostly( sameTypes( this, them ), us(), them, fas(), 2 ); // Skip Version, & ID !
    }

    @Override
    public final String toString() {
        return new ToStringBuilder( this.getClass().getSimpleName() ).addAll( us(), fas() ).toString();
    }

    abstract protected FieldAccessors<T> fas();

    protected T us() {
        return Cast.it( this );
    }

    private static boolean sameTypes( AbstractBaseEntity<?> abe1, AbstractBaseEntity<?> abe2 ) {
        if ( (abe1 == null) || (abe2 == null) ) { // Note: for this situation (null != null)!
            return false;
        }
        return (abe1.type == abe2.type); // Identity!
    }
}
