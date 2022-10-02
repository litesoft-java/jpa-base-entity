package org.litesoft.dtos.withbaseiso;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;

import org.litesoft.annotations.PackageFriendlyForTesting;
import org.litesoft.annotations.Significant;
import org.litesoft.fields.Accessor;
import org.litesoft.fields.Equivalance;
import org.litesoft.fields.FieldAccessors;
import org.litesoft.fields.FieldMappers;
import org.litesoft.fields.ToStringBuilder;
import org.litesoft.isos.withbaseentity.AbstractBaseISOwithBaseEntityFields;
import org.litesoft.isos.withbaseentity.BaseISOwithBaseEntityFields;
import org.litesoft.utils.Cast;
import org.litesoft.utils.TemplatedMessageException;
import org.litesoft.uuid.UuidMoniker;
import org.litesoft.uuid.UuidVersionCodec;
import org.litesoft.uuid.UuidVersionPair;

@SuppressWarnings({"com.haulmont.jpb.EqualsDoesntCheckParameterClass", "unused"})
public abstract class AbstractBaseDTOwithUpdateTokenAndISOentityFields<I extends AbstractBaseISOwithBaseEntityFields<?, I>, T extends AbstractBaseDTOwithUpdateTokenAndISOentityFields<I, T>>
        implements BaseDTOwithUpdateTokenAndBaseISOentityFields<T> {
    public static final String UPDATE_TOKEN_INVALID_TEMPLATE = "UpdateToken Invalid.||.";

    protected static <I extends AbstractBaseISOwithBaseEntityFields<?, I>, T extends AbstractBaseDTOwithUpdateTokenAndISOentityFields<I, T>>
    FieldAccessors<T> addCommonFieldsFAS( FieldAccessors<T> fas ) {
        return fas
                .optional( "updateToken", /* . */ BaseDTOwithUpdateTokenAndBaseISOentityFields::getUpdateToken, // for FAS should be first!
                        /* . . . . . . . . . . */ BaseDTOwithUpdateTokenAndBaseISOentityFields::setUpdateToken )
                /* . . . . . . . . . . . . . . */.withType( String.class ).addMetaData( "encoded" )
                .optional( "id", /*. . . . . . */ BaseDTOwithUpdateTokenAndBaseISOentityFields::getId,
                        /* . . . . . . . . . . */ BaseDTOwithUpdateTokenAndBaseISOentityFields::setId )
                /* . . . . . . . . . . . . . . */.withType( UUID.class )
                ;
    }

    protected static <I_SOURCE extends AbstractBaseISOwithBaseEntityFields<?, I_SOURCE>, T_TARGET extends AbstractBaseDTOwithUpdateTokenAndISOentityFields<I_SOURCE, T_TARGET>>
    FieldMappers<T_TARGET, I_SOURCE> addFromISO(
            FieldAccessors<T_TARGET> fasTarget, FieldAccessors<I_SOURCE> fasSource, FieldMappers<T_TARGET, I_SOURCE> mappers ) {
        return mappers
                .add( "id", fasTarget, fasSource ) // For Mappings: id should come before updateToken
                .add( "updateToken", fasTarget, AbstractBaseDTOwithUpdateTokenAndISOentityFields::generateUpdateToken ) // For Mappings: updateToken should come after id
                ;
    }

    protected static <I_TARGET extends AbstractBaseISOwithBaseEntityFields<?, I_TARGET>, T_SOURCE extends AbstractBaseDTOwithUpdateTokenAndISOentityFields<I_TARGET, T_SOURCE>>
    FieldMappers<I_TARGET, T_SOURCE> addToISO(
            FieldAccessors<I_TARGET> fasTarget, FieldAccessors<T_SOURCE> fasSource, FieldMappers<I_TARGET, T_SOURCE> mappers ) {
        return mappers.add( "id&updateToken", Cast.it( DTO_TO_ISO_ID_UPDATE_TOKEN_VERSION_MAPPER ) );
    }

    private final Map<String, String> errors = new LinkedHashMap<>();

    private String updateToken;
    private UUID id;

    private final Map<String, String> monikers = new LinkedHashMap<>();
    protected final Map<String, Object> relatedObjects = new LinkedHashMap<>();

    @Override
    public final Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public final String getUpdateToken() {
        return updateToken;
    }

    @Override
    public final void setUpdateToken( String updateToken ) {
        this.updateToken = updateToken;
    }

    @Override
    public final UUID getId() {
        return id;
    }

    @Override
    public final void setId( UUID id ) {
        this.id = id;
    }

    @Override
    public final Map<String, String> getMonikers() {
        return monikers;
    }

    @Override
    public final Map<String, Object> getRelatedObjects() {
        return relatedObjects;
    }

    @Override
    public final T populateMonikers() {
        for ( Accessor<T, ?> accessor : fas().getAll() ) {
            if ( accessor.getType() == UUID.class ) {
                UUID uuid = Cast.it( accessor.getValue( Cast.it( this ) ) );
                if ( uuid != null ) {
                    monikers.put( accessor.getName(), UuidMoniker.from( uuid ) );
                }
            }
        }
        for ( Object object : relatedObjects.values() ) {
            if ( object instanceof BaseDTOwithUpdateTokenAndBaseISOentityFields ) {
                ((BaseDTOwithUpdateTokenAndBaseISOentityFields<?>)object).populateMonikers();
            }
        }
        return us();
    }

    public final T from( I iso ) {
        if ( iso != null ) {
            fromISO().map( us(), iso );
            updateErrors( iso );
        }
        return us();
    }

    /**
     * Update ISO from this DTO.
     *
     * @param iso nullable, null -> not updated
     * @return passed in ISO
     * @throws IllegalStateException if there are Field Errors!
     */
    public final I update( I iso ) {
        if ( iso != null ) {
            toISO().map( iso, us() );
            if ( updateErrors( iso ) ) {
                throw new IllegalStateException( "Field Errors exist" );
            }
        }
        return iso;
    }

    private boolean updateErrors( I iso ) {
        boolean errorsExist = iso.validate();
        Map<String, String> errors = getErrors();
        errors.clear();
        if ( errorsExist ) {
            iso.getFieldErrors().values().forEach( fe -> errors.put( fe.getFieldName(), fe.errorMsg() ) );
        }
        return errorsExist;
    }

    @Override
    public final int hashCode() {
        return fas().hashCodeFrom( us() );
    }

    @Override
    public boolean equals( Object o ) {
        return fas().equalInstancesWithEqualTypes( us(), o );
    }

    @SuppressWarnings("unused")
    public final boolean isEqualLessUpdateToken( T them ) {
        return Equivalance.mostly( us(), them, fas(), 1 ); // Skip UpdateToken !
    }

    public final boolean isEquivalent( T them ) {
        return Equivalance.mostly( us(), them, fas(), 2 ); // Skip UpdateToken, & ID !
    }

    @Override
    public final String toString() {
        return new ToStringBuilder( this.getClass().getSimpleName() ).addAll( us(), fas() ).toString();
    }

    protected final T us() {
        return Cast.it( this );
    }

    abstract protected FieldAccessors<T> fas();

    abstract protected FieldMappers<T, I> fromISO();

    abstract protected FieldMappers<I, T> toISO();

    @PackageFriendlyForTesting
    static String generateUpdateToken( BaseISOwithBaseEntityFields iso ) {
        if ( iso != null ) {
            UUID id = iso.getId();
            Long version = iso.getVersion();
            if ( (id != null) && (version != null) ) {
                return codec.encode( id, version );
            }
        }
        return null;
    }

    @PackageFriendlyForTesting
    static AtomicReference<IntSupplier> randomSourceForCodec = new AtomicReference<>();

    private static final IntSupplier codecIntSupplier = new IntSupplier() {
        private final Random defaultIntSupplier = new SecureRandom();

        @Override
        public int getAsInt() {
            IntSupplier supplier = randomSourceForCodec.get();
            return (supplier != null) ? supplier.getAsInt() : defaultIntSupplier.nextInt();
        }
    };

    private static final UuidVersionCodec codec = new UuidVersionCodec( codecIntSupplier );

    @PackageFriendlyForTesting
    static final FieldMappers.Mapper<? extends BaseISOwithBaseEntityFields, ? extends BaseDTOwithUpdateTokenAndBaseISOentityFields<?>> DTO_TO_ISO_ID_UPDATE_TOKEN_VERSION_MAPPER =
            ( sourceDTO, targetISO ) -> {
                if ( (sourceDTO != null) && (targetISO != null) ) {
                    UUID id = sourceDTO.getId(); // presumably already transferred ID -- if null will
                    String token = Significant.ConstrainTo.valueOrNull( sourceDTO.getUpdateToken() );
                    if ( token != null ) {
                        UuidVersionPair pair;
                        try {
                            pair = codec.decode( token );
                        }
                        catch ( IllegalArgumentException e ) {
                            System.out.println( "AbstractBasePersistenceDTO UpdateToken decode: " + e.getMessage() );
                            throw new TemplatedMessageException( UPDATE_TOKEN_INVALID_TEMPLATE, "!" );
                        }
                        if ( pair == null ) {
                            throw new Error( "LIBRARY error: pair should only be null if token was null or empty!" );
                        }
                        UUID tokenUUID = pair.getUuid();
                        long tokenVersion = pair.getVersion();
                        if ( id == null ) {
                            id = tokenUUID;
                        } else if ( !id.equals( tokenUUID ) ) {
                            throw new TemplatedMessageException( UPDATE_TOKEN_INVALID_TEMPLATE, "¡" );
                        }
                        targetISO.setVersion( tokenVersion );
                    }
                    targetISO.setId( id );
                }
            };
}
