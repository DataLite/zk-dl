package cz.datalite.dao.plsql.helpers;

import cz.datalite.dao.plsql.Converter;
import cz.datalite.dao.plsql.FieldInfo;
import cz.datalite.dao.plsql.StructConvertable;
import cz.datalite.helpers.BooleanHelper;
import cz.datalite.helpers.DateHelper;
import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.helpers.StringHelper;
import oracle.sql.STRUCT;
import org.hibernate.proxy.HibernateProxyHelper;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;

/**
 * Date: 6/13/13
 * Time: 8:40 AM
 */
@SuppressWarnings({"TryWithIdenticalCatches", "Duplicates", "unused", "WeakerAccess"})
public final class ObjectHelper
{
    private ObjectHelper()
    {
    }


    /**
     * Vytvoření nové instance
     *
     * @param type typ vytvářeného objektu
     * @return vytvořená instance
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public static <T> T newInstance(Class<T> type)
    {
        try
        {
            return type.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new IllegalStateException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Ziskani getteru dane polozky
     *
     * @param aClass Trida aktualni instance
     * @param field  Polozka
     * @return getter
     */
    public static Method getFieldGetter(Class aClass, Field field)
    {
        if (aClass == null)
        {
            throw new IllegalArgumentException("Class aClass is null");
        }

        if (field == null)
        {
            throw new IllegalArgumentException("field argument is null");
        }

        String fieldName = field.getName();
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        try
        {
            //noinspection unchecked
            return aClass.getMethod( "get" + fieldName ) ;
        }
        catch ( NoSuchMethodException e )
        {
            try
            {
                //noinspection unchecked
                return aClass.getMethod("is" + fieldName);
            }
            catch (NoSuchMethodException e2)
            {
                try
                {
                    //noinspection unchecked
                    return aClass.getMethod("get" + methodName);
                }
                catch (NoSuchMethodException e1)
                {
                    try
                    {
                        //noinspection unchecked
                        return aClass.getMethod("is" + methodName);
                    }
                    catch (NoSuchMethodException e3)
                    {
                        if (aClass.getSuperclass() != Object.class)
                        {
                            return getFieldGetter(aClass.getSuperclass(), field);
                        }
                    }
                }
                catch (NullPointerException e4)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Ziskani getteru dane polozky
     *
     * @param aClass Trida aktualni instance
     * @param field  Polozka
     * @return getter
     */
    public static Method getFieldGetter(Class aClass, String field)
    {
        if (aClass == null)
        {
            throw new IllegalArgumentException("Class aClass is null");
        }

        if (field == null)
        {
            throw new IllegalArgumentException("field argument is null");
        }

        String methodName = field.substring(0, 1).toUpperCase() + field.substring(1);

        try
        {
            //noinspection unchecked
            return aClass.getMethod( "get" + field) ;
        }
        catch ( NoSuchMethodException e )
        {
            try
            {
                //noinspection unchecked
                return aClass.getMethod("is" + field);
            }
            catch (NoSuchMethodException e2)
            {
                try
                {
                    //noinspection unchecked
                    return aClass.getMethod("get" + methodName);
                }
                catch (NoSuchMethodException e1)
                {
                    try
                    {
                        //noinspection unchecked
                        return aClass.getMethod("is" + methodName);
                    }
                    catch (NoSuchMethodException e3)
                    {
                        if (aClass.getSuperclass() != Object.class)
                        {
                            return getFieldGetter(aClass.getSuperclass(), field);
                        }
                    }
                }
                catch (NullPointerException e4)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    /**
     * Ziskani getteru dane polozky
     *
     * @param aClass Trida aktualni instance
     * @param field  Polozka
     * @return getter
     */
    public static Method getFieldSetter(Class aClass, Field field)
    {
        if (aClass == null)
        {
            throw new IllegalArgumentException("Class aClass is null");
        }

        if (field == null)
        {
            throw new IllegalArgumentException("field argument is null");
        }

        String fieldName = field.getName();
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        try
        {
            //noinspection unchecked
            return aClass.getMethod( "set" + fieldName, field.getType() ) ;
        }
        catch (NoSuchMethodException e)
        {
            try
            {
                //noinspection unchecked
                return aClass.getMethod( "set" + methodName, field.getType() ) ;
            }
            catch (NoSuchMethodException e1)
            {
                if (aClass.getSuperclass() != Object.class)
                {
                    return getFieldSetter(aClass.getSuperclass(), field);
                }
            }
            catch (NullPointerException e2)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Object getValue(String fieldName, Object obj)
    {
        if (obj == null)
        {
            return null;
        }

        int dot = fieldName.indexOf( "." ) ;

        if ( dot > 0 )
        {
            Object value = getValue( fieldName.substring( 0, dot ), obj ) ;

            return  ( value == null ) ? null : getValue( fieldName.substring( dot + 1 ), value ) ;
        }

        Class clazz = null ;

        try
        {
            clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(obj) ;
            Method m = getFieldGetter( clazz, fieldName ) ;
                
            if ( m != null )
            {
                 return m.invoke( obj ) ;
            }

            Field field = getDeclaredField( clazz, fieldName);
            boolean f = field.isAccessible() ;

            try
            {
                field.setAccessible( true ) ;

                return field.get(obj);
            }
            finally
            {
                //noinspection ThrowFromFinallyBlock
                field.setAccessible( f ) ;
            }
        }
        catch (NoSuchFieldException e)
        {
            throw new IllegalStateException("Missing field '" + fieldName + "' in '" + clazz + "'" ) ;
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException("Can`t access field '" + fieldName + "' in '" + clazz + "'" ) ;
        }
    }

    public static <T> T getValue(String fieldName, Object obj, Class<T> returnType)
    {
        return extractFromObject(getValue(fieldName, obj), returnType);
    }

    /**
     * @param clazz     prohledávaná třída
     * @param name      hledaná položka
     * @return nalezená položka
     */
    private static Field getDeclaredField( Class clazz, String name  ) throws NoSuchFieldException
    {
        for( Field field : ReflectionHelper.getAllFields( clazz ) )
        {
            if ( field.getName().equals( name ) )
            {
                return field ;
            }
        }

        throw new NoSuchFieldException( name ) ;
    }

    public static void setValue(String fieldName, Object obj, Object value)
    {
        if ( obj == null )
        {
            return ;
        }

        try
        {
            Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(obj) ;



            int dot = fieldName.indexOf( "." ) ;

            if ( dot > 0 )
            {
                setValue( fieldName.substring( dot + 1 ), getValue( fieldName.substring( 0, dot ), obj ), value ) ;
                return  ;
            }

            Field field = getDeclaredField( clazz, fieldName ) ;
            Method m = getFieldSetter( field.getDeclaringClass(), field ) ;


            if ( ( value != null ) && ( ! field.getType().isAssignableFrom( value.getClass() ) ) )
            {
                value = ObjectHelper.extractFromObject( value, field.getType() ) ;
            }

            if ( m != null )
            {
                m.invoke( obj, value ) ;
            }

            boolean f = field.isAccessible() ;

            try
            {
                field.setAccessible( true ) ;
                field.set( obj, value ) ;
            }
            finally
            {
                //noinspection ThrowFromFinallyBlock
                field.setAccessible( f ) ;
            }
        }
        catch (NoSuchFieldException e)
        {
            throw new IllegalStateException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }


    public static <T> T extractFromObject(Object value, Class<T> returnType) {
        return extractFromObject(value, returnType, null);
    }

    /**
     * Extrahovaní hodnoty
     *
     * @param value      Hodnota
     * @param returnType Navratový typ pozadovane hodnoty
     * @return převedená hodnota
     */
    public static <T> T extractFromObject(Object value, Class<T> returnType, Converter<Object, T> converter)
    {
        if (converter != null) {
            return converter.fromDb(value);
        } else if ( value instanceof STRUCT) {
            try
            {
                StructConvertable sc = (StructConvertable) returnType.newInstance() ;

                //noinspection unchecked
                return (T)extractFromStructure( value, sc ) ;
            }
            catch (InstantiationException e)
            {
                throw new IllegalStateException( e ) ;
            }
            catch (IllegalAccessException e)
            {
                throw new IllegalStateException( e ) ;
            }
        }
        else if ((returnType == Integer.class) || (returnType == int.class))
        {
            //noinspection unchecked
            return (T) extractInteger(value);
        }
        else if ((returnType == Long.class) || (returnType == long.class))
        {
            //noinspection unchecked
            return (T) extractLong(value);
        }
        else if ((returnType == Double.class) || (returnType == double.class))
        {
            //noinspection unchecked
            return (T) extractDouble(value);
        }
        else if (returnType == BigDecimal.class)
        {
            //noinspection unchecked
            return (T) extractBigDecimal(value);
        }
        else if ((returnType == Boolean.class) || (returnType == boolean.class))
        {
            //noinspection unchecked
            return (T) extractBoolean(value);
        }
        else if (returnType == String.class)
        {
            //noinspection unchecked
            return (T) extractString(value);
        }
        else if (returnType == BigInteger.class)
        {
            //noinspection unchecked
            return (T) extractBigInteger(value);
        }
        else if (returnType == Date.class)
        {
            //noinspection unchecked
            return (T) extractDate(value);
        }
        else if ( returnType.isEnum() )
        {
            return EnumHelper.getEnumValue( returnType, extractString( value ) ) ;
        }

        //noinspection unchecked
        return (T) value;
    }


    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static String extractString(Object value)
    {
        if (value instanceof Boolean)
        {
            return ((Boolean) value) ? "A" : "N";
        }
        else if ( value instanceof Date )
        {
            return DateHelper.dateToString((Date) value, "MM/dd/yyyy HH:mm:ss" ) ;
        }

        return (value != null) ? String.valueOf(value) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static Long extractLong(Object value)
    {
        if (value instanceof Long)
        {
            return (Long) value;
        }
        else if (value instanceof Integer)
        {
            return (long) (Integer) value;
        }
        else if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).longValue();
        }
        else if ( value instanceof String )
        {
            return ( ! StringHelper.isNull((String)value)) ? Long.parseLong( removeDot( (String)value ) ) : null ;
        }
        else if ( value instanceof Double )
        {
            return ((Double)(Double.parseDouble(String.valueOf(value)))).longValue() ;
        }

        return (value != null) ? Long.parseLong(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static Double extractDouble(Object value)
    {
        if (value instanceof Double)
        {
            return (Double) value;
        }
        else if (value instanceof Integer)
        {
            return (double) (Integer) value;
        }
        else if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).doubleValue();
        }
        else if ( value instanceof String )
        {
            return ( ! StringHelper.isNull((String)value)) ?  Double.parseDouble((String) value) : null ;
        }

        return (value != null) ? Double.parseDouble(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    @SuppressWarnings("WeakerAccess")
    public static Integer extractInteger(Object value)
    {
        if (value instanceof Long)
        {
            return ((Long) value).intValue();
        }
        else if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).intValue();
        }
        else if (value instanceof Integer)
        {
            return ((Integer) value);
        }
        else if ( value instanceof String )
        {
            return ( ! StringHelper.isNull((String)value)) ?  Integer.parseInt( removeDot( (String)value ) ): null ;
        }
        else if ( value instanceof Double )
        {
            return ((Double)(Double.parseDouble(String.valueOf(value)))).intValue() ;
        }

        return (value != null) ? Integer.parseInt(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static BigDecimal extractBigDecimal(Object value)
    {
        if (value instanceof Long)
        {
            return new BigDecimal((Long) value);
        }
        else if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value);
        }
        else if (value instanceof Integer)
        {
            return new BigDecimal((Integer) value);
        }
        else if ( value instanceof String )
        {
            return ( ! StringHelper.isNull((String)value)) ?  new BigDecimal((String) value) : null ;
        }
        else if ( value instanceof Double )
        {
            return new BigDecimal((Double) value) ;
        }

        return (value != null) ? new BigDecimal(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static BigInteger extractBigInteger(Object value)
    {
        if (value instanceof Long)
        {
            return BigInteger.valueOf( (Long)value ) ;
        }
        else if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).toBigInteger() ;
        }
        else if (value instanceof Integer)
        {
            return BigInteger.valueOf(((Integer) value)) ;
        }
        else if ( value instanceof String )
        {
            return ( ! StringHelper.isNull((String)value)) ?  new BigInteger((String) value) : null ;
        }
        else if ( value instanceof Double )
        {
            //noinspection ConstantConditions
            return new BigInteger( extractString( value ) ) ;
        }

        return (value != null) ? new BigInteger(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static Boolean extractBoolean(Object value)
    {
        if (value instanceof Long)
        {
            return (Long) value != 0;
        }
        else if (value instanceof BigDecimal)
        {
            return !BigDecimal.ZERO.equals(value);
        }
        else if (value instanceof Integer)
        {
            return (Integer) value != 0;
        }
        else if ( value instanceof String )
        {
            return BooleanHelper.isTrue((String) value) ;
        }

        return (value != null) ? BooleanHelper.isTrue(String.valueOf(value)) : null;
    }

    /**
     * @param value převáděná hodnota
     * @return převedená hodnota
     */
    public static Date extractDate(Object value)
    {
        if (value instanceof Long)
        {
            return new Date( (long)value ) ;
        }
        else if (value instanceof BigDecimal)
        {
            return new Date( ((BigDecimal)value).longValue() ) ;
        }
        else if (value instanceof Integer)
        {
            return new Date( ((Integer)value).longValue() ) ;
        }
        else if ( value instanceof String )
        {
            return DateHelper.fromString( (String)value ) ;
        }
        else if ( value instanceof XMLGregorianCalendar )
        {
            return ((XMLGregorianCalendar) value).toGregorianCalendar().getTime() ;
        }
        else if ( value instanceof Date )
        {
            return (Date)value ;
        }

        return null ;
    }

    /**
     * @param type typ
     * @return true jedna se logickou hodnotu
     */
    public static boolean isBoolean( Class<?> type )
    {
        return ( ( type == Boolean.class ) || ( type == boolean.class ) ) ;
    }

    /**
     * @param fieldInfo     položka
     * @return true položka je číslo
     */
    public static boolean isNumeric( FieldInfo fieldInfo )
    {
        return isNumeric( fieldInfo.getType() ) ;
    }

    /**
     * @param type typ
     * @return true položka je číslo
     */
    public static boolean isNumeric( Class<?> type )
    {
        return ( ( type == Integer.class ) || ( type == Long.class ) || ( type == BigDecimal.class )  || ( type == int.class ) || ( type == long.class ) ) ;
    }

    /**
     * @param fieldInfo     položka
     * @return true položka je číslo
     */
    public static boolean isDate( FieldInfo fieldInfo )
    {
        return isDate(fieldInfo.getType()) ;
    }

    /**
     * @param type typ
     * @return true položka je číslo
     */
    public static boolean isDate( Class<?> type )
    {
        return ( type == Date.class )  ;
    }

    /**
     * @param type typ
     * @return true položka jsou znaky
     */
    public static boolean isString( Class<?> type )
    {
        return ( type == String.class )  ;
    }

    /**
     * Převod objektu na cílový typ
     *
     * @param source             Převáděný objekt
     * @param target             Cílový objekt
     * @return cílový objekt
     */
    public static <E extends StructConvertable> E extractFromStructure( Object source, E target )
    {
        if ( target == null )
        {
            throw new IllegalArgumentException( "Není určen cílový objekt" ) ;
        }

        if ( source instanceof STRUCT )
        {
            try
            {
                target.setStructureAttributes( ((STRUCT) source).getAttributes() ) ;

                return target ;
            }
            catch (SQLException e)
            {
                throw new IllegalStateException( e ) ;
            }
        }

        throw new IllegalStateException( "Zdrojový objekt není typu STRUCT") ;
    }

    /**
     * Převod na pole
     *
     * @param values        převáděné hodnoty
     * @return převedené hodnoty
     */
    @SuppressWarnings({"unchecked", "UnusedDeclaration"})
    public static <T> T[] asArray( T ... values )
    {
        return values ;
    }


    private static String removeDot( String original )
    {
        return ( ( original != null ) && ( original.endsWith( ".0" ) ) ) ? original.substring( 0, original.indexOf( "." )  ) : original ;
    }
}
