package cz.datalite.dao.plsql;

import cz.datalite.dao.plsql.annotations.SqlField;
import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.ReflectionHelper;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapovaní annotací
 */
public class FieldMaps
{

    /**
     * Synchronizační zámek
     */
    final static Object fieldsMapLocker = new Object() ;

    /**
     * Cache pro uložení mapování položek
     */
    static Map<Class<?>, Map<String, FieldInfo>> fieldsMap = new HashMap<>() ;

    /**
     * Příznak zda povolit
     */
    static boolean allowedHibernateAnnotations = true ;

    /**
     * Získání SQL pro převod vstupní struktury na PL/SQL strukturu
     *
     * @param entityClass          zdrojová entita
     *
     * @return SQL script
     */
    public static Map<String, FieldInfo> getFieldMaps( Class<?> entityClass  )
    {
        Map<String, FieldInfo> fm = fieldsMap.get( entityClass ) ;

        if ( ( fm == null ) || ( fm.isEmpty() ) )
        {
            synchronized ( fieldsMapLocker )
            {
                fm = generateFieldMaps(entityClass) ;

                fieldsMap.put( entityClass, fm ) ;
            }
        }

        return fm ;
    }

    /**
     * Generování mapování
     *
     * @param entityClass       třída entity
     * @return převodní mapa
     */
    public static Map<String, FieldInfo> generateFieldMaps( Class<?> entityClass )
    {
        Map<String, FieldInfo> result = new HashMap<>() ;

        if ( ObjectHelper.isBoolean(entityClass) )
        {
            result.put( StoredProcedureInvoker.FIELD_BOOLEAN, new FieldInfo( StoredProcedureInvoker.FIELD_BOOLEAN, Boolean.class ) ) ;
        }
        else if ( ObjectHelper.isNumeric( entityClass  ) )
        {
            result.put( StoredProcedureInvoker.FIELD_NUMERIC, new FieldInfo( StoredProcedureInvoker.FIELD_NUMERIC, Long.class ) ) ;
        }
        else if ( ObjectHelper.isString( entityClass  ) )
        {
            result.put( StoredProcedureInvoker.FIELD_STRING, new FieldInfo( StoredProcedureInvoker.FIELD_STRING, String.class ) ) ;
        }
        else if ( ObjectHelper.isDate( entityClass  ) )
        {
            result.put( StoredProcedureInvoker.FIELD_DATE, new FieldInfo( StoredProcedureInvoker.FIELD_DATE, Date.class ) ) ;
        }
        else
        {
            for( Field field : ReflectionHelper.getAllFields(entityClass) )
            {
                generateFieldMaps( result, field ) ;
            }
        }

        return result ;
    }


    /**
     * @param result            Výsledný seznam
     * @param field             Aktuální položka
     */
    public static void generateFieldMaps( Map<String, FieldInfo> result, Field field )
    {
        if ( field.isAnnotationPresent( SqlField.class ) )
        {
            SqlField sqlField = field.getAnnotation( SqlField.class ) ;
            Class<? extends Converter> converterClass = sqlField.converter();
            if (converterClass != null && !converterClass.isAssignableFrom(NoopConverter.class)) {
                try {
                    Converter converter = converterClass.newInstance();
                    result.put(sqlField.value(), new FieldInfo( field.getName(), field.getType(), converter) ) ;
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Unable to create converter for field %s: %s", field, e.getMessage()), e);
                }
            } else {
                result.put(sqlField.value(), new FieldInfo( field.getName(), field.getType() ) ) ;
            }


        }
        else if ( ( allowedHibernateAnnotations ) && ( field.isAnnotationPresent( Column.class ) ) )
        {
            Column sqlField = field.getAnnotation( Column.class ) ;

            result.put( sqlField.name(), new FieldInfo( field.getName(), field.getType() ) ) ;
        }
    }

    /**
     * Nastavení příznaku zda použít    annotace pro hibernate
     *
     * @param allowedHibernateAnnotations           hodnota příznaku
     */
    public static void setAllowedHibernateAnnotations(boolean allowedHibernateAnnotations)
    {
        FieldMaps.allowedHibernateAnnotations = allowedHibernateAnnotations;
    }
}
