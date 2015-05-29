package cz.datalite.zk.converter;

import cz.datalite.zk.bind.Binder;
import cz.datalite.zk.bind.ZKBinderHelper;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Map;

/**
 * Wraps instance of converter with adapter implementing {@link ZkConverter}
 *
 * @author Karel Cemus
 */
public class ConverterResolver {

    public static ZkConverter createAdapter( final Object converter, final Component component, final Object controller, final Object binder, final Map<String, String> attributes ) {

        if ( converter == null ) return null;

        if ( converter instanceof Converter && ZKBinderHelper.version( component ) == 2 )
            return new ZkConverterAdapter( ( Converter ) converter, ( Binder ) binder, component, attributes );

        throw new IllegalArgumentException( String.format("Unsupported class type in converter. "
                    + "'org.zkoss.zkplus.databind.TypeConverter' of "
                    + "'org.zkoss.bind.Converter' is required instead "
                    + "of given '%s'.", converter.getClass() ));
    }

    public static ZkConverter resolve( final String converter, final Component component, final Object controller, final Map<String, String> attributes ) {

        if ( converter == null || converter.length() == 0 )
            return null;

        // strip quotes
        String name = converter.replaceAll( "^'(.*)'$", "$1");

        // resolve converter name based on binding version
        Object instance = ZKBinderHelper.resolveConverter( name, component );

        // create adapter
        return createAdapter( instance, component, controller, component.getAttribute( "binder", true ), attributes );
    }
}
