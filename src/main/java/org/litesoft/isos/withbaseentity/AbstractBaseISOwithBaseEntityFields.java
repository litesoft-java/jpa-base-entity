package org.litesoft.isos.withbaseentity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.litesoft.baseentity.AbstractBaseEntity;
import org.litesoft.fields.Equivalance;
import org.litesoft.fields.FieldAccessors;
import org.litesoft.fields.FieldError;
import org.litesoft.fields.FieldMappers;
import org.litesoft.fields.ToStringBuilder;
import org.litesoft.utils.Cast;

@SuppressWarnings("unused")
public abstract class AbstractBaseISOwithBaseEntityFields<E extends AbstractBaseEntity<E>, T extends AbstractBaseISOwithBaseEntityFields<E, T>>
        implements BaseISOwithBaseEntityFields {
    protected static <E extends AbstractBaseEntity<E>, T extends AbstractBaseISOwithBaseEntityFields<E, T>>
    FieldAccessors<T> addCommonFieldsFAS( FieldAccessors<T> fas ) {
        return fas
                .optional( "version", /* . . */ BaseISOwithBaseEntityFields::getVersion, // for FAS should be first!
                        /* . . . . . . . . . */ BaseISOwithBaseEntityFields::setVersion )
                /* . . . . . . . . . . . . . */.withType( Long.class )
                .required( "id", /*. . */ BaseISOwithBaseEntityFields::getId,
                        /* . . . . . . . . . */ BaseISOwithBaseEntityFields::setId )
                /* . . . . . . . . . . . . . */.withType( UUID.class )
                ;
    }

    protected static <E_SOURCE extends AbstractBaseEntity<E_SOURCE>, T_TARGET extends AbstractBaseISOwithBaseEntityFields<E_SOURCE, T_TARGET>>
    FieldMappers<T_TARGET, E_SOURCE> addFromEntity(
            FieldAccessors<T_TARGET> fasTarget, FieldAccessors<E_SOURCE> fasSource, FieldMappers<T_TARGET, E_SOURCE> mappers ) {
        return mappers
                .add( "version", fasTarget, fasSource )
                .add( "id", fasTarget, fasSource )
                ;
    }

    protected final Map<String, FieldError> fieldErrors = new LinkedHashMap<>();

    @Override
    public final Map<String, FieldError> getFieldErrors() {
        return fieldErrors;
    }

    private Long version;
    private UUID id;

    @Override
    public final UUID getId() {
        return id;
    }

    @Override
    public final void setId( UUID id ) {
        this.id = id;
    }

    @Override
    public final Long getVersion() {
        return version;
    }

    @Override
    public final void setVersion( Long version ) {
        this.version = version;
    }

    public final T from( E entity ) {
        if ( entity != null ) {
            fromEntity().map( us(), entity, getFieldErrors() );
            this.validate();
        }
        return us();
    }

    /**
     * Update entity from this ISO.
     *
     * @param entity nullable, null -> not updated
     * @return passed in entity
     * @throws IllegalStateException if there are Field Errors!
     */
    public final E update( E entity ) {
        if ( entity != null ) {
            if ( !validate() ) {
                toEntity().map( entity, us(), getFieldErrors() );
            }
            if ( hasFieldErrors() ) {
                throw new IllegalStateException( "Field Errors exist" );
            }
        }
        return entity;
    }

    @Override
    public boolean validate() {
        List<FieldError> errors = fas().validate( us() );
        if ( (errors != null) && !errors.isEmpty() ) {
            errors.forEach( this::addFieldError );
        }
        return hasFieldErrors();
    }

    @Override
    public final int hashCode() {
        return fas().hashCodeFrom( us() );
    }

    @SuppressWarnings("com.haulmont.jpb.EqualsDoesntCheckParameterClass")
    @Override
    public boolean equals( Object o ) {
        return fas().equalInstancesWithEqualTypes( us(), o );
    }

    @SuppressWarnings("unused")
    public boolean isEqualLessVersion( T them ) {
        return Equivalance.mostly( us(), them, fas(), 1 ); // Skip Version !
    }

    public boolean isEquivalent( T them ) {
        return Equivalance.mostly( us(), them, fas(), 2 ); // Skip Version, & ID !
    }

    @Override
    public final String toString() {
        return new ToStringBuilder( this.getClass().getSimpleName() ).addAll( us(), fas() ).toString();
    }

    protected final T us() {
        return Cast.it( this );
    }

    abstract protected FieldAccessors<T> fas();

    abstract protected FieldMappers<T, E> fromEntity();

    abstract protected FieldMappers<E, T> toEntity();
}
