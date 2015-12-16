package cz.datalite.dao.plsql.helpers;

import cz.datalite.helpers.StringHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: karny
 * Date: 9/13/12
 * Time: 11:23 AM
 */
public final class EnumHelper
{
    private EnumHelper()
    {
    }

    /**
     * @return seznam zobrazovanych hodnot
     */
    public static  Object[] getEnumValues( Class enumClass )
    {
        try
        {
            //noinspection unchecked
            Method m = enumClass.getDeclaredMethod( "values" ) ;

            //noinspection unchecked
            return (Object[])m.invoke( null ) ;
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
    }

    /**
     * @param value     prevadena hodnota
     * @return prevedena hodnota
     */
    @SuppressWarnings ("UnusedDeclaration")
    public static String getEnumValue( Object value )
    {
        if ( value == null )
        {
            return null ;
        }

        try
        {
            Method m = value.getClass().getDeclaredMethod( "getValue" ) ;

            //noinspection unchecked
            return (String)m.invoke( value ) ;
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
    }

    /**
     * @param value     prevadena hodnota
     * @return prevedena hodnota
     */
    public static <T> T getEnumValue( Class<T> enumType, String value )
    {
        try
        {
            for( Method m : enumType.getDeclaredMethods() )
            {
                if ( ( StringHelper.isEquals( m.getName(), "fromValue" ) ) || ( StringHelper.isEquals( m.getName(), "getByStringValue" ) ) )
                {
                    //noinspection unchecked
                    return (T)m.invoke( null, value ) ;
                }
            }

            if ( ! StringHelper.isNull(value) )
            {
                for( Object o : getEnumValues( enumType ) )
                {
                    if ( StringHelper.isEquals( getEnumName( o ), value ) )
                    {
                        //noinspection unchecked
                        return (T)o ;
                    }
                }

                throw new IllegalArgumentException( "Neexistuje method fromValue ve třídě " + enumType.getName() ) ;
            }

            return null ;
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException( e ) ;
        }
    }

    /**
     * @param value     prevadena hodnota
     * @return prevedena hodnota
     */
    public static String getEnumName( Object value )
    {
        if ( value == null )
        {
            return null ;
        }

        return ((Enum)value).name() ;
    }

}
