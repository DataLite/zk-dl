package cz.datalite.hibernate.type;

import cz.datalite.helpers.StringHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Typ pro konverze textové DB reprezentace na enum
 */
public class EnumCharType implements UserType, Serializable, DynamicParameterizedType
{
    /**
     * Nazev parametru, ktery urcuje hodnotu enum v databazi
     */
    public final static String PARAMETER_DATABASE_FIELD = "DATABASE_FIELD" ;

    /**
     * DB typ
     */
    private static final int[] SQL_TYPES = {Types.VARCHAR } ;

    /**
     * Typ hodnoty
     */
    Class<? extends Enum> targetType ;

    /**
     * Nazev položky s hodnotu pro uloženi do DB
     */
    String databaseField ;


    @Override
    public void setParameterValues(Properties parameters)
    {
        targetType = null;

        if (parameters.containsKey(PARAMETER_DATABASE_FIELD))
        {
            databaseField = (String) parameters.get(PARAMETER_DATABASE_FIELD) ;
        }

        final DynamicParameterizedType.ParameterType reader = (DynamicParameterizedType.ParameterType) parameters.get(PARAMETER_TYPE);

         Class targetTypeClass = ( reader != null ) ? reader.getReturnedClass()  : null ;

         if ( ( targetTypeClass == null ) || ( ! targetTypeClass.isEnum() ) )
         {
              throw new IllegalStateException( "Typ Enum lze použít pouze pro položky typu '" + Enum.class.getName() + "'" ) ;
         }

        //noinspection unchecked
        targetType = targetTypeClass ;
    }

    @Override
    public Class returnedClass()
    {
        return targetType;
    }

    @Override
    public int[] sqlTypes()
    {
        return SQL_TYPES;
    }

    @Override
    public int hashCode(Object _obj)
    {
        return _obj.hashCode();
    }

    @Override
    public Object assemble(Serializable _cached, Object _owner) throws HibernateException
    {
        return stringToEnum((String) _cached) ;
    }

    @Override
    public Serializable disassemble(Object _obj) throws HibernateException
    {
        return enumToString((Enum) _obj) ;
    }

    @Override
    public Object replace(Object _orig, Object _tar, Object _owner)
    {
        return deepCopy( _owner ) ;
    }

    @Override
    public boolean equals(Object arg0, Object arg1) throws HibernateException
    {
        return (arg0 == null) && (arg1 == null) || ((arg0 != null) && (arg0.equals(arg1)));
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException
    {
        return stringToEnum(rs.getString(names[0]) ) ;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException
    {
        st.setObject(index, enumToString((Enum) value)) ;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException
    {
        return stringToEnum(enumToString((Enum) value)) ;
    }

    @Override
    public boolean isMutable()
    {
        return true  ;
    }

    /**
     * @param value     převáděný objekt
     * @return převedený objekt
     */
    private Object stringToEnum( String value )
    {
        if ( value == null )
        {
            return null ;
        }

        for( Enum v : getEnumValues(targetType) )
        {
            String enumValue = enumToString( v ) ;

            if ( StringHelper.isEquals(enumValue, value) )
            {
                return v ;
            }
        }

        throw new HibernateException( "Chybná hodnota '" + value + "'" ) ;
    }

    /**
     * @param value převáděný objekt
     * @return převedená hodnota
     */
    private <T extends Enum<T>> String enumToString( T value )
    {
        if ( value == null )
        {
            return null ;
        }

        if ( StringHelper.isNull( databaseField ) )
        {
            return value.name() ;
        }

        try
        {
            try
            {
                Field field = value.getClass().getField( databaseField ) ;

                field.setAccessible( true ) ;

                return (String)field.get( value ) ;
            }
            catch (NoSuchFieldException e)
            {
                Method method  = value.getClass().getDeclaredMethod( "get" + databaseField.substring(0,1).toUpperCase() + databaseField.substring(1) ) ;

                return (String)method.invoke( value ) ;
            }
        }
        catch ( Exception e )
        {
            throw new HibernateException( "Chybná hodnota '" + value + "'" ) ;
        }
    }


    /**
     * @param targetType        typ enumu
     * @return seznam enum hodnot
     */
    private <T extends Enum<T>> T[] getEnumValues( Class<T> targetType )
    {
        return targetType.getEnumConstants() ;
    }
}
