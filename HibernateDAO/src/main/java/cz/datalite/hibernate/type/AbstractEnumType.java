package cz.datalite.hibernate.type;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.helpers.StringHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
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
@SuppressWarnings("WeakerAccess")
public abstract class AbstractEnumType implements UserType, Serializable, DynamicParameterizedType
{
    /**
     * Nazev parametru, ktery urcuje hodnotu enum v databazi
     */
    public final static String PARAMETER_DATABASE_FIELD = "DATABASE_FIELD" ;

    /**
     * Typ hodnoty
     */
    Class<? extends Enum> targetType ;

    /**
     * Nazev položky s hodnotu pro uloženi do DB
     */
    String databaseField ;


    @Override
    public int[] sqlTypes()
    {
        return new int[] { getSqlType() } ;
    }

    /**
     * @return typ DB objektu
     */
    protected abstract int getSqlType() ;



    @Override
    public void setParameterValues(Properties parameters)
    {
        targetType = null;

        if (parameters.containsKey(PARAMETER_DATABASE_FIELD))
        {
            databaseField = (String) parameters.get(PARAMETER_DATABASE_FIELD) ;
        }

        final ParameterType reader = (ParameterType) parameters.get(PARAMETER_TYPE);

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
    public int hashCode(Object _obj)
    {
        return _obj.hashCode();
    }

    @Override
    public Object assemble(Serializable _cached, Object _owner) throws HibernateException
    {
        //noinspection unchecked
        return typeToEnum( _cached ) ;
    }

    @Override
    public Serializable disassemble(Object _obj) throws HibernateException
    {
        //noinspection unchecked
        return enumToType((Enum) _obj) ;
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
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException
    {
        Serializable result = null;

        if ( getSqlType() == Types.NUMERIC )
        {
            Long value = rs.getLong(names[0]);

            if(!rs.wasNull())
            {
                result = typeToEnum(value);
            }

        }
        else if ( getSqlType() == Types.VARCHAR )
        {
            String value = rs.getString(names[0]);

            if(!rs.wasNull())
            {
                result = typeToEnum(value);
            }
        }
        else
        {
            Serializable value = (Serializable) rs.getObject(names[0]);

            if(!rs.wasNull())
            {
                result = typeToEnum(value);
            }
        }

        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException
    {
        //noinspection unchecked
        st.setObject(index, enumToType((Enum) value)) ;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException
    {
        //noinspection unchecked
        return typeToEnum(enumToType((Enum) value)) ;
    }

    @Override
    public boolean isMutable()
    {
        return true  ;
    }

    /**
     * @param targetType        typ enumu
     * @return seznam enum hodnot
     */
    protected  <T extends Enum<T>> T[] getEnumValues( Class<T> targetType )
    {
        return targetType.getEnumConstants() ;
    }


    /**
     * @param value     převáděný objekt
     * @return převedený objekt
     */
    protected <T extends Enum<T>, S extends Serializable> T typeToEnum(S value)
    {
        if ( value == null )
        {
            return null ;
        }

        for( Enum v : getEnumValues(targetType) )
        {
            //noinspection unchecked
            Object enumValue = enumToType( v ) ;

            if ( ( enumValue instanceof Integer ) && ( value instanceof Long ) )
            {
                enumValue = ((Integer)enumValue).longValue() ;
            }
            else if ( ( enumValue instanceof Long ) && ( value instanceof Integer ) )
            {
                //noinspection unchecked
                enumValue = ((Long)enumValue).intValue() ;
            }

            if ( EqualsHelper.isEquals( enumValue, value ) )
            {
                //noinspection unchecked
                return (T) v;
            }
        }

        throw new HibernateException( "Chybná hodnota '" + value + "'" ) ;
    }


    /**
     * @param value     převáděná hodnota
     * @return převedená hodnota
     */
    protected <T extends Enum<T>, S extends Serializable> S enumToType(T value)
    {
        if ( value == null )
        {
            return null ;
        }

        if ( StringHelper.isNull( databaseField ) )
        {
            //noinspection unchecked
            return (S) value.name();
        }

        try
        {
            try
            {
                Field field = value.getClass().getField( databaseField ) ;

                field.setAccessible( true ) ;

                //noinspection unchecked
                return (S) field.get( value );
            }
            catch (NoSuchFieldException e)
            {
                Method method  = value.getClass().getDeclaredMethod( "get" + databaseField.substring(0,1).toUpperCase() + databaseField.substring(1) ) ;

                //noinspection unchecked
                return (S) method.invoke( value );
            }
        }
        catch ( Exception e )
        {
            throw new HibernateException( "Chybná hodnota '" + value + "'" ) ;
        }
    }
}
