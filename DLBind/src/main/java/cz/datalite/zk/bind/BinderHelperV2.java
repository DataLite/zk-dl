package cz.datalite.zk.bind;

import java.util.Collections;
import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;

/**
 * Binder helper implementation for databinding in version 2.0 which came with
 * ZK 6.
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
/* package */ class BinderHelperV2 implements BinderHelper {

    /* package */ static final BinderHelperV2 INSTANCE = new BinderHelperV2();

    protected BinderHelperV2() {
        // inner library class, singleton
    }

    public void loadComponent( Component comp ) {
        Binder.getBinder( comp ).loadComponent( comp );
    }

    public void loadComponentAttribute( Component comp, String attribute ) {
        Binder.getBinder( comp ).loadComponentAttribute( comp, attribute );
    }

    public void saveComponent( Component comp ) {
        Binder.getBinder( comp ).saveComponent( comp );
    }

    public void saveComponentAttribute( Component comp, String attribute ) {
        Binder.getBinder( comp ).saveComponentAttribute( comp, attribute );
    }

    public void loadComponentAttributes( Component comp, String[] attributes ) {
        for ( String attr : attributes ) {
            loadComponentAttribute( comp, attr );
        }
    }

    public void registerAnnotation( AbstractComponent component, String property, String annotName, String value ) {
        component.addAnnotation( property, annotName, Collections.singletonMap( "value", new String[]{ value } ) );
    }

    public void notifyChange( Object bean, String model ) {
        BindUtils.postNotifyChange( null, null, bean, model );
    }

    public int version() {
        return 2;
    }
}
