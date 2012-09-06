package cz.datalite.zk.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.AnnotateBinder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BindingKey;
import org.zkoss.zk.ui.Component;

/**
 * DataLite's implementation of ZK Binder to add the support for annotations
 * brought by this part of ZK-DL library. To preserve the correct ZK command
 * life-cycle and to be as close as possible to the ZK framework's intended
 * usage there is needed to intercept the method with execution to attach
 * another logic.
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class Binder extends AnnotateBinder {

    /** instance of looger */
    protected static final Logger LOGGER = LoggerFactory.getLogger( Binder.class );

    public void saveComponent( Component comp ) {
        LOGGER.trace( "Saving the component '{}'.", comp.getClass() );
        saveAllBindings( comp );
    }

    public void loadComponent( Component comp ) {
        LOGGER.trace( "Loading the component '{}'.", comp.getClass() );
        super.loadComponent( comp, false );
    }

    @Deprecated
    public void loadComponentAttribute( Component comp, String attribute ) {
        LOGGER.trace( "Loading the attribute '{}' on the component '{}'.", attribute, comp.getClass() );
        loadBinding( comp, new BindingKey( comp, attribute ) );
    }

    @Deprecated
    public void saveComponentAttribute( Component comp, String attribute ) {
        LOGGER.trace( "Saving the attribute '{}' on the component '{}'.", attribute, comp.getClass() );
        saveBinding( comp, new BindingKey( comp, attribute ) );
    }

    public static Binder getBinder( Component comp ) {
        return ( Binder ) comp.getAttribute( "$BINDER$", true );
    }

    @Override
    public Converter getConverter( String name ) {
        try {
            return super.getConverter( name );
        } catch(org.zkoss.zk.ui.UiException ex) {
            // this allows to use method converters
            // syntax is then like 'ctl.coerceToValue'
            return new MethodTypeConverter( name, ex.getMessage() );
        }
    }
    
    
}
