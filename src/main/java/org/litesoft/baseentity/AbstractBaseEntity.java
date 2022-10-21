package org.litesoft.baseentity;

import java.util.Objects;
import java.util.UUID;

import org.litesoft.fields.Equivalance;
import org.litesoft.fields.FieldAccessors;
import org.litesoft.fields.ToStringBuilder;
import org.litesoft.utils.Cast;

@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractBaseEntity<T extends AbstractBaseEntity<T>> implements BaseEntity<T> {
    protected static <T extends BaseEntity<T>> FieldAccessors<T> createWithCommon( FieldAccessors<T> fas ) {
        return fas
                .auto( "version", BaseEntity::getVersion ).withType( Long.class )
                .required( "id", BaseEntity::getId ).withType( UUID.class ).addMetaData( "generated, if missing" )
                ;
    }

    abstract void setVersion( Long version );

    @Override
    public final int hashCode() {
        return getId().hashCode();
    }

    @Override
    public final boolean equals( Object o ) {
        return (o == this) ||
               ((o instanceof AbstractBaseEntity) && equals( (AbstractBaseEntity<?>)o ));
    }

    public final boolean equals( AbstractBaseEntity<?> them ) {
        return (them == this) ||
               (sameTypes( this, them ) && (Objects.equals( this.getId(), them.getId() )));
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

    private static boolean sameTypes( BaseEntity<?> abe1, BaseEntity<?> abe2 ) {
        if ( (abe1 == null) || (abe2 == null) ) { // Note: for this situation (null != null)!
            return false;
        }
        return (abe1.type() == abe2.type()); // Identity!
    }
}
