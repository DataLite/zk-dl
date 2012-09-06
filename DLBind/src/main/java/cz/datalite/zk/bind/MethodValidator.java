package cz.datalite.zk.bind;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * validator handler for syntax "@validate('ctl.validateSomething')"
 *
 * method has to correspond on of the following signature
 *
 * public void validateSomething( ValidationContext ctx ) throws WrongValueException;
 *
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class MethodValidator implements Validator {

    // Converter definition (like "ctl.myConverter")
    private String validator;
    // error description

    private String errorDesc;

    private String controller;

    private String method;

    private Method coerceMethod = null;

    private Object controllerObj = null;

    public MethodValidator( String validator, String errorDesc ) {
        this.validator = validator;
        this.errorDesc = errorDesc;

        // split the pattern
        String[] parts = validator.split( "\\." );

        if ( parts.length != 2 )
            throw new UiException( "Validator '" + validator + "' is not the class neither the controller method." );

        controller = parts[0];
        method = parts[1];
    }

    /**
     * Find the coerce method in composer
     *
     * @param comp component for the context
     * @return
     */
    public Method getValidationMethod( Component comp ) {
        if ( coerceMethod == null ) {
            // controller bean must be in variables
            controllerObj = comp.getAttribute( controller, true );
            if ( controllerObj == null )
                throw new UiException( errorDesc + " Unable to find bean '" + controller + "'." );

            // load method with only value param or component + value param
            try {
                coerceMethod = Classes.getCloseMethod( controllerObj.getClass(), method, new Class[]{ ValidationContext.class } );
            } catch ( NoSuchMethodException ex ) {
            }

            if ( coerceMethod == null )
                throw new UiException( errorDesc
                        + " Method '" + method + "' not found in class '" + controllerObj.getClass() + "'. Params: ValidationContext.class" );

        }
        return coerceMethod;
    }

    public void validate( ValidationContext ctx ) {
        try {
            Method m = getValidationMethod( ctx.getBindContext().getComponent() );
            if ( m.getGenericParameterTypes().length == 1 )
                m.invoke( controllerObj, ctx );
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
    
    public String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
