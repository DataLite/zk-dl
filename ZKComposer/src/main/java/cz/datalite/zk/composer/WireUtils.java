package cz.datalite.zk.composer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;

/**
 *
 * @author Karel Čemus <cemuskar@fel.cvut.cz>
 */
public class WireUtils {

    // unfortunettly, ZK makes everything private and doesn't allow to reuse if from outside.
    private static final Set IMPLICIT_NAMES = new HashSet();

    static {
        IMPLICIT_NAMES.add( "application" );
        IMPLICIT_NAMES.add( "applicationScope" );
        IMPLICIT_NAMES.add( "arg" );
        IMPLICIT_NAMES.add( "componentScope" );
        IMPLICIT_NAMES.add( "desktop" );
        IMPLICIT_NAMES.add( "desktopScope" );
        IMPLICIT_NAMES.add( "execution" );
        IMPLICIT_NAMES.add( "event" ); //since 3.6.1, #bug 2681819: normal page throws exception after installed zkspring
        IMPLICIT_NAMES.add( "self" );
        IMPLICIT_NAMES.add( "session" );
        IMPLICIT_NAMES.add( "sessionScope" );
        IMPLICIT_NAMES.add( "spaceOwner" );
        IMPLICIT_NAMES.add( "spaceScope" );
        IMPLICIT_NAMES.add( "page" );
        IMPLICIT_NAMES.add( "pageScope" );
        IMPLICIT_NAMES.add( "requestScope" );
        IMPLICIT_NAMES.add( "param" );
    }

    /**
     * We want to maintain implicit objects like GenericAutowireComposer does,
     * however it is not possible to reuse part of Components class while it is
     * private. This code uses Components.getImplicit() method.
     */
    public static void wireImplicit( final Object controller, final Component self ) {
        for ( final Iterator it = IMPLICIT_NAMES.iterator(); it.hasNext(); ) {
            final String fdname = ( String ) it.next();
            //we cannot inject event proxy because it is not an Interface
            if ( "event".equals( fdname ) )
                continue;
            final Object argVal = Components.getImplicit( self, fdname );
            try {
                final Field field = Classes.getAnyField( controller.getClass(), fdname );
                field.setAccessible( true );
                field.set( controller, argVal );
                field.setAccessible( false );
            } catch ( IllegalArgumentException ex ) {
                throw SystemException.Aide.wrap( ex );
            } catch ( IllegalAccessException ex ) {
                throw SystemException.Aide.wrap( ex );
            } catch ( NoSuchFieldException ex ) {
                throw SystemException.Aide.wrap( ex );
            }
        }
    }
}
