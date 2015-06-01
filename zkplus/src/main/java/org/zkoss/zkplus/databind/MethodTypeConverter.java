package org.zkoss.zkplus.databind;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Convenient converter, which is called directly from Binding.
 *
 * @author Jiri Bubnik
 */
public class MethodTypeConverter implements TypeConverter
{
    // Converter definition (like "ctl.myConverter")
    private String converter;
    // databinder
    private DataBinder binder;
    // error description
    private String errorDesc;

    private String controller;
    private String method;

    private Method coerceMethod = null;
    private Object controllerObj = null;

    public MethodTypeConverter(String converter, DataBinder binder, String errorDesc)
    {
        this.binder = binder;
        this.errorDesc = errorDesc;
        this.converter = converter;

        controller = (String) DataBinder.parseExpression(this.converter, ".").get(0);
        method = (String) DataBinder.parseExpression(this.converter, ".").get(1);
    }


    /**
     * Find the coerce method in composer
     * @param comp component for the context
     * @param valueClass to which class it should be converted
     * @return
     */
    public Method getCoerceMethod(Component comp, Class valueClass)
    {
        if (coerceMethod == null) {
            // controller bean must be in variables
            controllerObj = binder.lookupBean(comp, controller);
            if (controllerObj == null) {
                throw new UiException(errorDesc + "Unable to find bean '" + controller + "'.");
            }

            // load method with only value param or component + value param
            try {
                coerceMethod = Classes.getCloseMethod(controllerObj.getClass(), method, new Class[]{valueClass});
            } catch (NoSuchMethodException ex) {
            }


            if (coerceMethod == null) {
                try {
                    coerceMethod = Classes.getCloseMethod(controllerObj.getClass(), method, new Class[]{valueClass, comp.getClass()});
                } catch (NoSuchMethodException ex) {
                }
            }

            if (coerceMethod == null) {
                throw new UiException(errorDesc
                        + " Method '" + method + "' not found in class '" + controllerObj.getClass() + "'. Params: "
                        + valueClass + ", " + comp.getClass() + " (optional)");
            }

        }
        return coerceMethod;
    }

    public Object coerceToUi(Object val, Component comp)
    {
        try {
            Method m = getCoerceMethod(comp, val == null ? null : val.getClass());
            if (m.getGenericParameterTypes().length == 1) {
                return m.invoke(controllerObj, val);
            } else {
                return m.invoke(controllerObj, val, comp);
            }
        } catch (IllegalAccessException ex) {
            throw new UiException(errorDesc + " Illegal access: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new UiException(errorDesc + " Illegal argument: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new UiException(errorDesc + " Error while invoking method: " + ex.getLocalizedMessage()
                    + "\n" + getStackTrace(ex), ex);
        } catch (Throwable ex) {
            throw new UiException(errorDesc + " Unexpected exception: " + ex.getLocalizedMessage()
                    + "\n" + getStackTrace(ex), ex);
        }
    }

    /**
     * Coerce to bean is not supported by this converter
     *
     * @param val the object to be corece to backend bean property type.
     * @param comp associated component
     * @return always the val (same as input)
     */
    public Object coerceToBean(Object val, Component comp)
    {
        return val;
    }

    public String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
