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
                .required( "id", AbstractBaseEntity::getId ).withType( UUID.class ).addMetaData( "generated, if missing" )
                .auto( "version", AbstractBaseEntity::getVersion ).withType( Long.class );
    }

    @Transient
    private final Class<T> type;

    @Id
    @Column(length = 16, unique = true, nullable = false)
    private UUID id;

    @Version
    private Long version; // default to null

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

    public abstract boolean isEquivalent( T them );

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

    protected final String stringFrom( String prefix, FieldAccessors<T> fas ) {
        return new ToStringBuilder( prefix )
                .addAll( Cast.it( this ), fas )
                .toString();
    }

    protected final boolean checkEquivalent( T them, FieldAccessors<T> fas ) {
        return Equivalance.mostly( sameTypes( this, them ), Cast.it( this ), them, fas, 2 );
    }

    private static boolean sameTypes( AbstractBaseEntity<?> abe1, AbstractBaseEntity<?> abe2 ) {
        if ( (abe1 == null) || (abe2 == null) ) { // Note: for this situation (null != null)!
            return false;
        }
        return (abe1.type == abe2.type); // Identity!
    }
}
