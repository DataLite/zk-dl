package cz.datalite.zk.converter;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.*;
import org.zkoss.bind.sys.Binding;
import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * Convenient converter, which is called directly from Binding.
 *
 * @author Jiri Bubnik
 */
public class MethodConverter implements TypeConverter, Converter {
    // Converter definition (like "ctl.myConverter")

    private String converter;
    // error description

    private String errorDesc;

    private String controller;

    private String method;

    private Method coerceMethod = null;

    private Object controllerObj = null;

    public MethodConverter( String converter, String errorDesc ) {
        this.errorDesc = errorDesc;
        this.converter = converter;

        // split the pattern
        String[] parts = converter.split( "\\." );

        if ( parts.length != 2 )
            throw new UiException( "Converter '" + converter + "' is not the class neither the controller method." );

        controller = parts[0];
        method = parts[1];
    }

    /**
     * Find the coerce method in composer
     *
     * @param comp component for the context
     * @param valueClass to which class it should be converted
     * @return method with converter
     */
    public Method getCoerceMethod( Component comp, Class valueClass ) {
        if ( coerceMethod == null ) {

            if ( comp == null )
                throw new IllegalStateException( "MethodConverter has not recognized controller instance. It is not set and the given component is NULL." );

            // controller bean must be in component variables or accessible by a variable resolver
            controllerObj = comp.getAttribute( controller, true );
            if (controllerObj == null) {
                controllerObj = comp.getPage().getXelVariable(controller);
            }

            if ( controllerObj == null )
                throw new UiException( errorDesc + " Unable to find bean '" + controller + "'." );


            // try to get converter object field - if found, than return value should be the converter object
            try {
                controllerObj = Fields.getByCompound(controllerObj, method);
                method = "coerceToUi";
            } catch ( Exception ex ) {
                // ok - the method itself should be coerceToUi method
            }

            // load method with only value param or component + value param
            try {
                coerceMethod = Classes.getCloseMethod( controllerObj.getClass(), method, new Class[]{ valueClass } );
            } catch ( Exception ex ) {
            }


            if ( coerceMethod == null )
                try {
                    coerceMethod = Classes.getCloseMethod( controllerObj.getClass(), method, new Class[]{ valueClass, comp == null ? Component.class : comp.getClass() } );
                } catch ( Exception ex ) {
                }

            if ( coerceMethod == null )
                try {
                    coerceMethod = Classes.getCloseMethod( controllerObj.getClass(), method, new Class[]{ valueClass, comp == null ? Component.class : comp.getClass(), BindContext.class } );
                } catch ( Exception ex ) {
                }

            if ( coerceMethod == null )
                throw new UiException(
                        String.format( "%s Method '%s' not found in class '%s'. Params: %s, %s (optional)",
                        errorDesc,
                        method,
                        controllerObj == null ? null : controllerObj.getClass(),
                        valueClass, comp == null ? null : comp.getClass() ) );
        }
        return coerceMethod;
    }

    public Object coerceToUi( Object val, Component comp ) {
        try {
            Method m = getCoerceMethod( comp, val == null ? null : val.getClass() );
            if ( m.getGenericParameterTypes().length == 3 )
                return m.invoke( controllerObj, val, comp, createDummyBindContext(comp));
            else if ( m.getGenericParameterTypes().length == 2 )
                return m.invoke( controllerObj, val, comp );
            else
            return m.invoke( controllerObj, val );
        } catch ( IllegalAccessException ex ) {
            throw new UiException( errorDesc + " Illegal access: " + ex.getLocalizedMessage(), ex );
        } catch ( IllegalArgumentException ex ) {
            throw new UiException( errorDesc + " Illegal argument: " + ex.getLocalizedMessage(), ex );
        } catch ( InvocationTargetException ex ) {
            throw new UiException( errorDesc + " Error while invoking method: " + ex.getLocalizedMessage()
                    + "\n" + getStackTrace( ex ), ex );
        } catch ( Throwable ex ) {
            throw new UiException( errorDesc + " Unexpected exception: " + ex.getLocalizedMessage()
                    + "\n" + getStackTrace( ex ), ex );
        }
    }

    // dummy context to allow client code to depend on binding context (instead of just passing null)
    private BindContext createDummyBindContext(Component comp) {
        Binder binder = BinderUtil.getBinder(comp);
        BindContext bindContext = BindContextUtil.newBindContext(binder, null, false, "", comp, null);

        return bindContext;
    }

    /**
     * Coerce to bean is not supported by this converter
     *
     * @param val the object to be corece to backend bean property type.
     * @param comp associated component
     * @return always the val (same as input)
     */
    public Object coerceToBean( Object val, Component comp ) {
        return val;
    }

    public String getStackTrace( Throwable throwable ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        throwable.printStackTrace( pw );
        return sw.getBuffer().toString();
    }

    public Object coerceToBean( Object compAttr, Component component, BindContext ctx ) {
        return coerceToBean( compAttr, component );
    }

    public Object coerceToUi( Object beanProp, Component component, BindContext ctx ) {
        return coerceToUi( beanProp, component );
    }
}
