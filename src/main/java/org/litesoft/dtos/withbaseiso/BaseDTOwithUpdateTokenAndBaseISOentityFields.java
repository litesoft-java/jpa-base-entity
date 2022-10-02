package org.litesoft.dtos.withbaseiso;

import java.util.Map;
import java.util.UUID;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.dtos.BaseDTO;

@SuppressWarnings("unused")
public interface BaseDTOwithUpdateTokenAndBaseISOentityFields<T extends BaseDTOwithUpdateTokenAndBaseISOentityFields<T>> extends BaseDTO {
    Map<String, String> getErrors();

    String getUpdateToken();

    void setUpdateToken( String updateToken );

    UUID getId();

    void setId( UUID id );

    Map<String, String> getMonikers();

    Map<String, Object> getRelatedObjects();

    default void addMoniker( String name, String moniker ) {
        getMonikers().put( Significant.AssertArgument.namedValue( "name", name ),
                           Significant.AssertArgument.namedValue( "moniker", moniker ) );
    }

    default void addRelatedObject( String name, Object relatedObject ) {
        getRelatedObjects().put( Significant.AssertArgument.namedValue( "name", name ),
                                 NotNull.AssertArgument.namedValue( "relatedObject", relatedObject ) );
    }

    /**
     * populateMonikers should be called AFTER any Related Objects are added!
     */
    @SuppressWarnings("UnusedReturnValue")
    T populateMonikers();
}
