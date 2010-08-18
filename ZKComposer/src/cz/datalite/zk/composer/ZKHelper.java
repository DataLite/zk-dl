package cz.datalite.zk.composer;

import cz.datalite.helpers.TypeConverter;
import java.util.Map;

/**
 * Pomocne funkce pro ZK framework
 *
 * @author Jiri Bubnik
 */
public class ZKHelper
{

    /**
     * Checks, if parameter exists in arg map and returns its value (it might be null) or the default value.
     * Target class is required for type safety, result type is checked and if not matched, exception is thrown.
     *
     * @param <T> type of parameter
     * @param arg argument map
     * @param key name of parameter
     * @param targetClass requested type
     * @param defaultValue default value if the parameter is not set at all
     * @return value of the parameter or null
     */
    public static <T> T getOptionalParameter(Map arg, String key, Class<T> targetClass, T defaultValue) {
        if (!arg.containsKey(key))
            return defaultValue;

        Object val = arg.get(key);

        if (val == null)
            return null;

        // http param are automaticaly assigned to String[] - unwrap
        if (val.getClass().isArray() && !targetClass.isArray())
        {
            if (((Object[])val).length == 0)
                return null;
            else
                val = ((Object[])val)[0];
        }

        // same class
        if (targetClass.isInstance(val))
            return (T) val;

        try
        {
            return TypeConverter.convertTo(val.toString(), targetClass);
        }
        catch (Exception ex)
        {
            throw new java.lang.IllegalArgumentException("Parameter '" + key + "' is not of expected type '" + targetClass.getName() +
                    "'. Unable assign value '" + arg.get(key) + "' of type '" + arg.get(key).getClass() + "'.");
        }
    }

    /**
     * Checks, that parameter exists in arg map and returns its value (it might be null).
     * Target class is required for type safety, result type is checked and if not matched, exception is thrown.
     *
     * @param <T> type of parameter
     * @param arg argument map
     * @param key name of parameter
     * @param targetClass requested type
     * @return value of the parameter or null
     */
    public static <T> T getRequiredParameter(Map arg, String key, Class<T> targetClass) {
        if (!arg.containsKey(key))
            throw new java.lang.IllegalArgumentException("Mandatory parameter '" + key + "' is not set.");

        return getOptionalParameter(arg, key, targetClass, null);
    }
}
