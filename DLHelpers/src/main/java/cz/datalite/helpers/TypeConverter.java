package cz.datalite.helpers;

import java.text.ParseException;

/**
 * The class allows to convert the string representation of a well-known basic java data types. The API offers
 * couple methods with the same inner implementation but a bit different conversion on the output. 
 * Currently there are supported these conversions from String to:
 * <ul>
 *  <li>{@link Integer} or int</li>
 *  <li>{@link Long} or long</li>
 *  <li>{@link Double} or double</li>
 *  <li>{@link Boolean} or boolean</li>
 *  <li>{@link String}</li>
 *  <li>{@link java.lang.Byte}</li>
 *  <li>{@link java.util.Date}</li>
 * </ul>
 * @author Karel Cemus
 */
final public class TypeConverter {

    /** redefinable default date format. This format is used to parse the input during the conversion */
    public static String dateFormat = "dd.MM.yyyy";

    private TypeConverter() {
    }
    
     /**
     * <p>Tries to convert a string value to a data type given as a parameter. There are few cases, that can occure.</p>
     * 
     * <ol>
     * <li>Given value is null or an empty string. In such case the null is also returned.</li>
     * <li>Defined class is a java primitive like int, double etc. For these instances the object representation is returned. It means
     * that for int it returns Integer, for double Double etc.</li>
     * <li>Value is well-known object primitive, like Integer, Date etc. In these cases the string is parsed into given object and returned.</li>     
     * <li>Defined class is complex. In such cases the original string value is returned because there is not known converter.</li>
     * </ol>
     * 
     * <p> Note: When the given class is inherited from some java primitive then the returned object is that primitive type. For example
     * this can occure for {@link java.sql.Date} which inherits from {@link java.util.Date}</p>
     * 
     * @param <T> desired type to be converted to
     * @param value string representation of converted value
     * @param type target class of the value
     * @return converted value if the conversion is defined
     * @throws ParseException this occures when the date is in bad format. The format can be redefined in {@link  #dateFormat}.
     * @throws NumberFormatException conversion of string to number has failed. It occures when the input string is not a number but the target class is.
     */
    public static <T> Object tryToConvertTo( final String value, final Class<T> type ) throws ParseException, NumberFormatException {
        if ( value == null || "".equals(value) ) {
            return null;
        }
        if ( java.util.Date.class.isAssignableFrom( type ) ) {
            return (java.util.Date)( new java.text.SimpleDateFormat( dateFormat, java.util.Locale.getDefault() ).parse( value ) );
        } else if ( String.class.isAssignableFrom( type ) ) {
            return type.cast( value );
        } else if ( Integer.class.isAssignableFrom( type ) || Integer.TYPE.equals( type )) {
            return   Integer.valueOf( value );
        } else if ( Double.class.isAssignableFrom( type ) || Double.TYPE.equals( type ) ) {
            return  Double.valueOf( value );
        } else if ( Long.class.isAssignableFrom( type ) || Long.TYPE.equals( type ) ) {
            return   Long.valueOf( value );
        } else if ( Boolean.class.isAssignableFrom( type ) || Boolean.TYPE.equals( type ) ) {
            return   (BooleanHelper.isTrue(value) ? Boolean.TRUE : (Boolean.valueOf( value )));
        } else if ( Byte.class.isAssignableFrom( type ) || Byte.TYPE.equals( type ) ) {
            return   Byte.valueOf(value);
        } else if ( type.isEnum() ) {
            for ( T enumValue : type.getEnumConstants() ) {
                if ( enumValue.toString().equals( value ) ) {
                    return enumValue;
                }
            }
            return null;
        } else {
            return value;
        }
    }
    
    
    /**
     * Calls method {@link TypeConverter#tryToConvertTo(java.lang.String, java.lang.Class) } but suppresses all the exceptions.
     * @param <T> desired type to be converted to
     * @param value string representation of converted value
     * @param type target class of the value
     * @return converted value if the conversion is defined, otherwise it returns the original string value
     */
    public static <T> Object tryToConvertToSilent( final String value, final Class<T> type ) {
        try {
            return tryToConvertTo( value, type );
        } catch ( ParseException ex ) {
            return value;
        } catch ( ClassCastException ex ) {
            return value;
        } catch ( NumberFormatException ex ) {
            return value;
        }
    }
    
    
    /**
     * Calls method {@link TypeConverter#tryToConvertTo(java.lang.String, java.lang.Class) } but it 
     * also tries to convert returned value to the given class to be allow the code to be type-safe.
     * It can fail for example for classes inhereted from the well-known primitive like {@link java.util.Date}.
     * The desired class can be {@link java.sql.Date} but it returns {@link java.util.Date} which is not 
     * castable to {@link java.sql.Date}.
     * 
     * @param <T> desired type to be converted to
     * @param value string representation of converted value
     * @param type target class of the value
     * @return converted value if the conversion is defined
     * @throws ParseException this occures when the date is in bad format. The format can be redefined in {@link  #dateFormat}.
     * @throws ClassCastException class cast failed
     * @throws NumberFormatException conversion of string to number has failed. It occures when the input string is not a number but the target class is.
     */
    public static <T> T convertTo( final String value, final Class<T> type ) throws ParseException, ClassCastException, NumberFormatException {
       return (T) tryToConvertTo(value, type);
    }

    /**
     * Calls method {@link #convertTo(java.lang.String, java.lang.Class) } but suppresses all the exceptions.
     * @param <T> desired type to be converted to
     * @param value string representation of converted value
     * @param type target class of the value
     * @return converted value if the conversion is defined, otherwise it returns <b>null</b>
     */
    public static <T> T convertToSilent( final String value, final Class<T> type ) {
        try {
            return convertTo( value, type );
        } catch ( ParseException ex ) {
            return null;
        } catch ( ClassCastException ex ) {
            return null;
        } catch ( NumberFormatException ex ) {
            return null;
        }
    }
}
