package org.litesoft.baseentity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.litesoft.fields.Accessor;
import org.litesoft.fields.FieldAccessors;
import org.litesoft.utils.Cast;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnnecessaryLocalVariable")
class AbstractBaseEntityTest {

    @Test
    void test_SimpleEntity_FAS() {
        String expected = String.join( "\n"
                , "id      UUID   (required & generated, if missing)"
                , "version Long   (auto)"
                , "value   String"
                , "" ); // force new line at end

        assertEquals( expected, SimpleEntity.FAS.toString() );
        assertEquals( expected, OtherEntity.FAS.toString() );
        // Both have exactly the same fields (including types and metadata)!
    }

    @Test
    void test_methods() {
        SimpleEntity se1 = new SimpleEntity( UUID.fromString( "4da10d9c-8640-458c-a660-6f641da5ca3b" ), "Fred" );
        SimpleEntity se2 = new SimpleEntity( UUID.randomUUID(), "Fred" );
        SimpleEntity se3 = new SimpleEntity( se1.getId(), "Fred" );
        se3.setVersion( 1L );
        OtherEntity oe1 = new OtherEntity( se1.getId(), "Fred" );
        OtherEntity oe2 = new OtherEntity( UUID.randomUUID(), "Wilma" );
        oe2.setVersion( 1L );

        assertNotEquals( se1, se2 );
        assertTrue( se1.isEquivalent( se2 ) ); // assert that IDs do NOT participate in Equivalence

        assertEquals( se1, se3 );
        assertTrue( se1.isEquivalent( se3 ) ); // assert that Versions do NOT participate in Equivalence
        assertEquals( se1.hashCode(), se3.hashCode() );

        assertNotEquals( se2, se3 );
        assertTrue( se2.isEquivalent( se3 ) );

        assertNotEquals( oe1, oe2 );
        assertFalse( oe1.isEquivalent( oe2 ) ); // assert that other fields (not ID or Version) DO participate in Equivalence

        AbstractBaseEntity<?> abe1 = se1;
        AbstractBaseEntity<?> abe2 = oe1;
        // assert that, all fields being the same, "type" difference still fails:
        assertNotEquals( abe1, abe2 );
        assertFalse( abe1.checkEquivalent( Cast.it( abe2 ), Cast.it( SimpleEntity.FAS ) ) );

        String se1Str = se1.toString();
        assertNotEquals( se1Str, se3.toString() ); // Version difference
        assertNotEquals( se1Str, oe1.toString() ); // Type label different

        assertEquals( se1.stringFrom( null, SimpleEntity.FAS ), // Same fields AND Type label not added
                      oe1.stringFrom( null, OtherEntity.FAS ) );

        assertEquals( String.join( "\n"
                , "SimpleEntity:"
                , "  id: 4da10d9c-8640-458c-a660-6f641da5ca3b"
                , "  version: null"
                , "  value: 'Fred'"
                                   // No trailing new line!
        ).replace( '\'', '"' ), se1Str );
    }

    @SuppressWarnings("unused")
    static class SimpleEntity extends AbstractBaseEntity<SimpleEntity> implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
        public static FieldAccessors<SimpleEntity> FAS = createWithCommon();
        public static Accessor<SimpleEntity, String> FA_Value = FAS.optional( "value", SimpleEntity::getValue ).withType( String.class );

        public SimpleEntity( UUID givenId ) {
            super( SimpleEntity.class, givenId );
        }

        protected SimpleEntity() {
            this( null );
        }

        private String value;

        public SimpleEntity( UUID givenId, String value ) {
            this( givenId );
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue( String value ) {
            this.value = value;
        }

        @Override
        public boolean isEquivalent( SimpleEntity them ) {
            return checkEquivalent( them, FAS );
        }

        @Override
        public String toString() {
            return stringFrom( this.getClass().getSimpleName(), FAS );
        }
    }

    @SuppressWarnings("unused")
    static class OtherEntity extends AbstractBaseEntity<OtherEntity> implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
        public static FieldAccessors<OtherEntity> FAS = createWithCommon();
        public static Accessor<OtherEntity, String> FA_Value = FAS.optional( "value", OtherEntity::getValue ).withType( String.class );

        public OtherEntity( UUID givenId ) {
            super( OtherEntity.class, givenId );
        }

        protected OtherEntity() {
            this( null );
        }

        private String value;

        public OtherEntity( UUID givenId, String value ) {
            this( givenId );
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue( String value ) {
            this.value = value;
        }

        @Override
        public boolean isEquivalent( OtherEntity them ) {
            return checkEquivalent( them, FAS );
        }

        @Override
        public String toString() {
            return stringFrom( this.getClass().getSimpleName(), FAS );
        }
    }
}