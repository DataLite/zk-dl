package cz.datalite.zk.converter;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BindContextImpl;
import org.zkoss.bind.impl.BindContextUtil;
import org.zkoss.zk.ui.Component;

/**
 * Adapter above {@link Converter}
 *
 * @author Karel Cemus
 */
public class ZkConverterAdapter implements ZkConverter {

    /** real converter implementation */
    private final Converter converter;

    private final Map<String, String> attributes;

    private final Binder binder;

    private final Component component;

    public ZkConverterAdapter( Converter converter, Binder binder, Component component, Map<String, String> attributes ) {
        this.converter = converter;
        this.attributes = new HashMap<String, String>( attributes );
        this.binder = binder;
        this.component = component;
    }

    public Object convertToView( Object value ) {

        // init contex
        final BindContext context = BindContextUtil.newBindContext( binder, null, false, "manual", component, null );
        context.setAttribute( BindContextImpl.CONVERTER_ARGS, new HashMap<String, String>( attributes ) );

        // perform conversion
        return converter.coerceToUi( value, component, context );
    }
}
