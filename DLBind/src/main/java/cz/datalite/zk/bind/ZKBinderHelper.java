package cz.datalite.zk.bind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

/**
 * Facade to helper utils to work with databinding. This internaly contains the
 * dispatched based on binding version to provide proper utils implemenation.
 *
 * @author Karel Cemus
 */
public abstract class ZKBinderHelper {

    private ZKBinderHelper() {
        // library class, facade
    }

    /** returns proper instance of binder helper based on binding version */
    public static BinderHelper helper( Component component ) {
        final Object binder = component.getAttribute( "binder", true );

        if ( binder == null )
            throw new IllegalArgumentException( "Component doesn't have assigned any binder." );

        if ( binder instanceof AnnotateDataBinder )
            return BinderHelperV1.INSTANCE;

        if ( binder instanceof Binder )
            return BinderHelperV2.INSTANCE;

        throw new UnsupportedOperationException( "Component has attached unsupported version of data binder." );
    }
}
