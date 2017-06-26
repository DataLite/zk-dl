package cz.datalite.zk.bind;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.Binding;
import org.zkoss.zkplus.databind.DataBinder;

/**
 * Implementation of Binding utils for databinding in version 1.0 what came with
 * ZK in the project beginning.
 *
 * @author Karel Cemus
 */
/* package */ class BinderHelperV1 implements BinderHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger( BinderHelper.class );
    
    /* package */ static final BinderHelperV1 INSTANCE = new BinderHelperV1();

    protected BinderHelperV1() {
        // inner library class, singleton
    }

    public void loadComponent( Component comp ) {
        getBinder( comp ).loadComponent( comp );
    }

    public void loadComponentAttribute( Component comp, String attribute ) {
        Binding bind = getBinder( comp ).getBinding( comp, attribute );
        if ( bind != null ) {
            bind.loadAttribute(comp);
        }
    }

    public boolean saveComponent( Component comp ) {
        getBinder( comp ).saveComponent( comp );
        return true;
    }

    public boolean saveComponentAttribute( Component comp, String attribute ) {
        if ( getBinder( comp ) != null ) {
            Binding bind = getBinder( comp ).getBinding( comp, attribute );
            if ( bind != null ) {
                bind.saveAttribute( comp );
                return true;
            }
        }
        return false;
    }

    public void loadComponentAttributes( Component comp, String[] attributes ) {
        for ( String attr : attributes ) {
            loadComponentAttribute( comp, attr );
        }
    }

    public void registerAnnotation( AbstractComponent component, String property, String annotName, String value ) {
        component.addAnnotation( property, "default", Collections.singletonMap( annotName, new String[]{ value } ) );
    }

    public void notifyChange( Object bean, String model ) {
        throw new UnsupportedOperationException( "Not supported in this version of databinding. Please use binding 2.0 or later." );
    }

    public int version() {
        return 1;
    }

    protected DataBinder getBinder( Component comp ) {
        return ( DataBinder ) comp.getAttributeOrFellow( "binder", true );
    }

    public Object resolveConverter( String converter, Component component ) {
        try {
            // create instance of binding, constructor is package-friendly
            Constructor<Binding> bindingConstructor = Binding.class.getDeclaredConstructor( DataBinder.class, Component.class, String.class, String.class, LinkedHashSet.class, LinkedHashSet.class, String.class, String.class );
            bindingConstructor.setAccessible( true );
            Binding binding = bindingConstructor.newInstance( component.getAttribute( "binder", true ), component, null, null, null, null, null, converter );

            // return resolved constructor
            return binding.getConverter();

        } catch ( Exception ex ) {
            LOGGER.warn( "Converter '{}' wasn't resolved.", converter, ex);
            return null;
        }
    }
}
