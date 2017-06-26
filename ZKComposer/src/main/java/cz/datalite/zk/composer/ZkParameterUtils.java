package cz.datalite.zk.composer;

import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.helpers.StringHelper;
import cz.datalite.helpers.ZKHelper;
import cz.datalite.zk.annotation.ZkParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.zkoss.lang.SystemException;
import org.zkoss.zk.ui.Executions;

/**
 *
 * @author Karel Cemus
 */
public final class ZkParameterUtils {

    private ZkParameterUtils() {
    }

    /**
     * Traverse all fields and set up parameters according o @ZkParameters
     * annotation.
     */
    public static void setupZkParameters( Object instance ) {
        for ( Field field : ReflectionHelper.getAllFields( instance.getClass() ) ) {
            for ( Annotation annotation : field.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkParameter ) {
                    setupZkParameter((ZkParameter) annotation, getName((ZkParameter) annotation, field), field.getType(), field, null, instance);
                }
            }
        }

        for ( Method method : ReflectionHelper.getAllMethods( instance.getClass() ) ) {
            for ( Annotation annotation : method.getDeclaredAnnotations() ) {
                if ( annotation instanceof ZkParameter ) {
                    String paramName = ( ( ZkParameter ) annotation ).name();
                    if ( StringHelper.isNull( paramName ) ) {
                        if ( !method.getName().startsWith( "set" ) ) {
                            throw new InstantiationError("@ZkParameter must be on method in form of setXXX(ParamType p)  (e.g. setParamName). "
                                    + "Found: " + method.getName());
                        }
                        paramName = getMethodLowerCase( method.getName().substring( 3 ) );
                    }

                    if ( method.getParameterTypes().length != 1 ) {
                        throw new InstantiationError("@ZkParameter must be on method in form of setXXX(ParamType p), wrong number of parameters. "
                                + "Actual number of parameters: " + method.getParameterTypes().length);
                    }

                    setupZkParameter( ( ZkParameter ) annotation, paramName, method.getParameterTypes()[0], null, method, instance );
                }
            }
        }

    }

    /**
     * Setup parameter accordnig to ZkParameter annotation.
     *
     * @param <T> type of field.
     * @param annot the annotation with parameter definition
     * @param field the field to set
     * @param clazz class of the field and parameter
     */
    protected static <T> void setupZkParameter( ZkParameter annot, String paramName, Class<T> clazz, Field field, Method method, Object instance ) {
        T result = null;

        Map[] paramMaps = new Map[]{
            Executions.getCurrent().getArg(),
            Executions.getCurrent().getAttributes(),
            Executions.getCurrent().getParameterMap()
        };

        Map paramMap = paramMaps[0];
        for ( Map map : paramMaps ) {
            if ( map.containsKey( paramName ) ) {
                paramMap = map;
                break;
            }
        }

        try {
            if ( annot.required() ) {
                result = ZKHelper.getRequiredParameter(paramMap, paramName, clazz);
            } else if ( !paramMap.containsKey( paramName ) ) {
                return; // optional parameter doesn't do anything
            } else {
                result = ZKHelper.getOptionalParameter(paramMap, paramName, clazz, null);
            }
        } catch ( IllegalArgumentException ex ) {
            throw new IllegalArgumentException( "@ZkParameter(name='" + paramName + "', "
                    + "required=" + ( annot.required() ? "true" : "false" ) + ", "
                    + "createIfNull=" + ( annot.createIfNull() ? "true" : "false" )
                    + "): " + ex.getLocalizedMessage() );
        }

        if ( result == null && annot.createIfNull() ) {
            try {
                result = (T) clazz.newInstance();
            } catch (InstantiationException ex) {
                throw new InstantiationError("@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Parameter is null, unable to create new object with error: " + ex.getLocalizedMessage());
            } catch (IllegalAccessException ex) {
                throw new InstantiationError("@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Parameter is null, unable to create new object, no public default constructor: " + ex.getLocalizedMessage());
            }
        }

        if ( field != null ) {
            try {
                field.setAccessible(true);
                field.set(instance, result);
                field.setAccessible(false);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of field to '" + result + "': " + ex.getLocalizedMessage(), ex);
            } catch (IllegalAccessException ex) {
                throw SystemException.Aide.wrap(ex);
            }
        }
        if ( method != null ) {
            try {
                method.setAccessible(true);
                method.invoke(instance, new Object[]{result});
                method.setAccessible(false);
            } catch (IllegalAccessException ex) {
                throw SystemException.Aide.wrap(ex);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of method to '" + result + "': " + ex.getClass() + " - " + ex.getLocalizedMessage(), ex);
            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException("@ZkParameter(name='" + paramName + "', "
                        + "required=" + (annot.required() ? "true" : "false") + ", "
                        + "createIfNull=" + (annot.createIfNull() ? "true" : "false")
                        + ") - Unable to set new value of method to '" + result + "', error in method invocation: "
                        + ex.getClass() + " - " + (ex.getTargetException() == null ? ex.getLocalizedMessage() : ex.getTargetException().getLocalizedMessage()), ex);
            }
        }
    }

    private static String getName( final ZkParameter annotation, final Field field ) {
        return "".equals( annotation.name() ) ? field.getName() : annotation.name();
    }

    protected static String getMethodLowerCase( final String name ) {
        return name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
    }
}
