package org.litesoft.isos.withbaseentity;

import java.util.Map;
import java.util.UUID;

import org.litesoft.annotations.NotNull;
import org.litesoft.fields.FieldError;
import org.litesoft.isos.BaseISO;
import org.litesoft.utils.TemplatedMessage;

@SuppressWarnings("unused")
public interface BaseISOwithBaseEntityFields extends BaseISO {
    @NotNull
    Map<String, FieldError> getFieldErrors();

    default void addFieldError( FieldError fieldError ) {
        if ( fieldError != null ) {
            getFieldErrors().put( fieldError.getFieldName(), fieldError );
        }
    }

    default void addFieldError( String fieldName, TemplatedMessage templatedMessage ) {
        addFieldError( new FieldError( fieldName, templatedMessage ) );
    }

    default void addFieldError( String fieldName, String fmtString, String... indexedFmtData ) {
        addFieldError( new FieldError( fieldName, fmtString, indexedFmtData ) );
    }

    default boolean hasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }

    /**
     * @return true if there are any FieldErrors
     */
    boolean validate();

    UUID getId();

    void setId( UUID id );

    Long getVersion();

    void setVersion( Long version );
}
