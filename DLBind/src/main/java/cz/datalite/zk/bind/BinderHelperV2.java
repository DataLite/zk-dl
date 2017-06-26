package cz.datalite.zk.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;

import java.util.Collections;

/**
 * Binder helper implementation for databinding in version 2.0 which came with
 * ZK 6.
 *
 * @author Karel Cemus
 */
/* package */ class BinderHelperV2 implements BinderHelper {

    /* package */ static final BinderHelperV2 INSTANCE = new BinderHelperV2();

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger( BinderHelperV2.class );

    protected BinderHelperV2() {
        // inner library class, singleton
    }

    public void loadComponent( Component comp ) {
        if ( Binder.getBinder( comp ) == null ) {
            LOGGER.debug("Binding on the component '{}' cannot be loaded, the component is detached.", comp.getId());
        } else {
            Binder.getBinder(comp).loadComponent(comp);
        }
    }

    public void loadComponentAttribute( Component comp, String attribute ) {
        if ( Binder.getBinder( comp ) == null ) {
            LOGGER.debug("Binding on the attribute '{}' of the component '{}' cannot be loaded, the component is detached.", attribute, comp.getId());
        } else {
            Binder.getBinder(comp).loadComponentAttribute(comp, attribute);
        }
    }

    public boolean saveComponent( Component comp ) {
        if ( Binder.getBinder( comp ) == null ) {
            LOGGER.debug( "Binding on the component '{}' cannot be saved, the component is detached.", comp.getId() );
            return false;
        } else {
            return Binder.getBinder( comp ).saveComponent( comp );
        }
    }

    public boolean saveComponentAttribute( Component comp, String attribute ) {
        if ( Binder.getBinder( comp ) == null ) {
            LOGGER.debug( "Binding on the attribute '{}' of the component '{}' cannot be saved, the component is detached.", attribute, comp.getId() );
            return false;
        } else {
            return Binder.getBinder(comp).saveComponentAttribute(comp, attribute);
        }
    }

    public void loadComponentAttributes( Component comp, String[] attributes ) {
        for ( String attr : attributes ) {
            loadComponentAttribute( comp, attr );
        }
    }

    public void registerAnnotation( AbstractComponent component, String property, String annotName, String value ) {
        component.addAnnotation( property, annotName, Collections.singletonMap("value", new String[]{value}) );
    }

    public void notifyChange( Object bean, String model ) {
        BindUtils.postNotifyChange( null, null, bean, model );
    }

    public int version() {
        return 2;
    }

    public Object resolveConverter( String converter, Component component ) {
        return Binder.getBinder( component ).getConverter( converter );
    }
}
