package cz.datalite.dao.plsql;

import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.ReflectionHelper;
import oracle.sql.ARRAY;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

/**
 * Definice výsledku uložené procedur
 */
@SuppressWarnings("Duplicates")
public class StoredProcedureResult extends HashMap<String, Object>
{
    private Map<String, String> long2shortName ;

    public StoredProcedureResult( Map<String, Object > original, Map<String, String> long2shortName )
    {
        super( original ) ;

        this.long2shortName = long2shortName ;
    }

    /**
     * @param original  originalni nazev
     * @return zmenseny nazev
     */
    private String compressName( String original )
    {
        if ( long2shortName.containsKey( original ) )
        {
            return long2shortName.get( original ) ;
        }

        String shortName = "S" + long2shortName.size() ;

        long2shortName.put( original, shortName ) ;

        return shortName ;
    }

    /**
     * Převod DB typu
     *
     * @param array             Převáděné pole
     * @param returnType        typu položky převedeněného pole
     * @return převedené pole
     */
    private <T> List<T> extractFromArray( Object[] array, Class<T> returnType )
    {
        if ( array == null )
        {
            return null ;
        }

        List<T> result = new ArrayList<T>() ;

        for( Object v : array )
        {
            result.add( ObjectHelper.extractFromObject(v, returnType) ) ;
        }

        return result ;
    }



    /**
     * Převod objektu na cílový typ
     *
     * @param obj             Převáděný objekt
     * @param itemType        Typ položky seznamu
     * @return cílový objekt
     */
    private <T> List<T> extractFromArray( Object obj, Class<T> itemType )
    {
        if ( obj instanceof List )
        {
            //noinspection unchecked
            return (List<T>) obj;
        }
        else if ( obj instanceof ARRAY )
        {
            try
            {
                return extractFromArray( (Object[])((ARRAY) obj).getArray(), itemType );
            }
            catch (SQLException e)
            {
                throw new IllegalStateException( "Chyba při převodu DB pole na JAVA pole", e )  ;
            }
        }
        else if ( obj != null )
        {
            throw new IllegalStateException( "Návratová hodnota má špatný typ:" + obj.getClass() )  ;
        }

        return null ;
    }


    /**
     * Vyzvednutí hodnot parametru
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> T extract( String name, Class<T> returnType)
    {
        Object obj = get(name) ;

        return ObjectHelper.extractFromObject(obj, returnType) ;
    }

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T extends StructConvertable> T extractStruct( String name, Class<T> returnType)
    {
        Object obj = get(name) ;

        return ObjectHelper.extractFromObject(obj, returnType) ;
    }

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> List<T> extractArray(String name, Class<T> returnType)
    {
        Object obj = get( name ) ;

        return extractFromArray( obj, returnType ) ;
    }



    /**
     * Vyzvednutí hodnot PL/SQL recordu
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> T extractRecord(String name, Class<T> returnType)
    {
        if ( ObjectHelper.isBoolean(returnType) )
        {
            //noinspection unchecked
            return (T) ObjectHelper.extractBoolean(get("o" + name + "." + StoredProcedureInvoker.FIELD_BOOLEAN)) ;
        }
        else if ( ObjectHelper.isNumeric(returnType) )
        {
            //noinspection unchecked
            return ObjectHelper.extractFromObject(get("o" + name + "." + StoredProcedureInvoker.FIELD_NUMERIC), returnType) ;
        }
        else if ( ObjectHelper.isDate(returnType) )
        {
            //noinspection unchecked
            return ObjectHelper.extractFromObject(get("o" + name + "." + StoredProcedureInvoker.FIELD_DATE), returnType) ;
        }
        else if ( ObjectHelper.isString(returnType) )
        {
            //noinspection unchecked
            return ObjectHelper.extractFromObject(get("o" + name + "." + StoredProcedureInvoker.FIELD_STRING), returnType) ;
        }
        else
        {
            T result = ObjectHelper.newInstance( returnType ) ;

            boolean found = extractRecord(name, result) ;

            return ( found ) ? result : null ;
        }
    }

    /**
     * Vyzvednutí hodnot PL/SQL recordu
     *
     * @param name                jméno proměnné
     * @param returnValue         cílový objekt
     *
     * @return true pokud byla ve vysledku alespon jedna hodnota polozky ciloveho objektu
     */
    public <T> boolean extractRecord(String name, T returnValue)
    {
        boolean found = false ;

        for( Map.Entry<String, FieldInfo> field : FieldMaps.getFieldMaps(returnValue.getClass()).entrySet() )
        {
            String parameterName = "o" + name + "." + field.getKey() ;
            Object value = null ;

            if ( containsKey(parameterName) )
            {
                value = get(parameterName) ;
                found = true ;
            }

            ObjectHelper.setValue( field.getValue().getFieldName(), returnValue, ObjectHelper.extractFromObject(value, field.getValue().getType()) ) ;
        }

        return found ;     }


    /**
     *
     * Převod jednoho řádku tabulky
     *
     * @param source            zdrojová data
     * @param index             index řáadku
     * @param target            cilový objket
     *
     */
    private <T> void extractFromTable( Map<FieldInfo, List<?>> source, int index, T target )
    {
        for( FieldInfo fieldName : source.keySet() )
        {
            List<?> fieldData = source.get( fieldName ) ;

            ObjectHelper.setValue( fieldName.getFieldName(), target, ( fieldData == null ) ? null : ObjectHelper.extractFromObject(fieldData.get(index), fieldName.getType()) ) ;
        }
    }

    /**
     * Generování mapy zdrojových dat
     *
     * @param name          jmeno proměnné
     * @param returnType    návratový typ (typ polozky)
     * @param target        vygenerovaný seznam zdrojových dat
     */
    private <T> void extractFromTable(  String name, Class<T> returnType, Map<FieldInfo, List<?>> target )
    {
        for( Map.Entry<String, FieldInfo> field : FieldMaps.getFieldMaps(returnType).entrySet() )
        {
            String parameterName = compressName( name + "_" + field.getKey() ) ;

            target.put( field.getValue(), extractFromArray(get(parameterName), field.getValue().getType()) ) ;
        }
    }


    /**
     *
     * Vyzvednutí PL/SQL pole
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return seznam vyzvednutých záznamů
     */
    public <T> List<T> extractTable( String name, Class<T> returnType )
    {
        List<T> result = new ArrayList<T>() ;

        Long count = ObjectHelper.extractLong(get(name + "_COUNT")) ;

        if ( count == null )
        {
            count = 0L ;
        }

        Map<FieldInfo, List<?>> dataFromDB = new HashMap<FieldInfo, List<?>>() ;

        extractFromTable( "o" + name, returnType, dataFromDB  ) ;

        if ( dataFromDB.size() == 1 )
        {
            FieldInfo fi = (FieldInfo) dataFromDB.keySet().toArray() [ 0 ] ;

            if ( fi.isPrimitive() )
            {
                for ( Object value : dataFromDB.get( fi ) )
                {
                    //noinspection unchecked
                    result.add((T) ObjectHelper.extractFromObject(value, fi.getType()) ) ;
                }

                return result ;
            }
        }

        for( int i=0; i<count; i++ )
        {
            T record = ObjectHelper.newInstance( returnType ) ;

            extractFromTable( dataFromDB, i, record ) ;

            result.add( record ) ;
        }

        return result ;
    }

    /**
     *
     * Vyzvednutí PL/SQL pole
     *
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     * @param target              cílový seznam
     * @param mergeType           způsob spojení seznamu z dba a cilového seznamu
     */
    public <T> void extractTable( String name, Class<T> returnType, List<T> target, MergeType mergeType)
    {
        List<T> novySeznam = extractTable( name, returnType ) ;

        if ( mergeType == MergeType.REPLACE )
        {
            target.clear() ;
        }

        if ( ( novySeznam != null ) && ( ! novySeznam.isEmpty() ) )
        {
            if ( mergeType == MergeType.ALL )
            {
                target.addAll( novySeznam ) ;
            }
            else if ( mergeType == MergeType.NEW )
            {
                for( T item : novySeznam )
                {
                    if (!target.contains(item))
                    {
                        target.add(item);
                    }
                }
            }
            else
            {
                List<T> newTarget = new ArrayList<>( target ) ;

                target.clear() ;

                for( T item : novySeznam )
                {
                    if ( ! newTarget.contains( item ) )
                    {
                        target.add( item ) ;
                    }
                    else if ( ( mergeType == MergeType.MERGE ) || ( mergeType == MergeType.SYNCHRONIZE ) )
                    {
                        int index = newTarget.indexOf( item ) ;
                        T old = newTarget.get( index ) ;

                        newTarget.remove( index ) ;
                        target.add( old ) ;

                        for( Field field : ReflectionHelper.getAllFields( returnType ) )
                        {
                            if ( ! Modifier.isStatic( field.getModifiers() ) )
                            {
                                ObjectHelper.setValue( field.getName(), old, ObjectHelper.getValue( field.getName(), item ) ) ;
                            }
                        }
                    }
                }

                if ( ( mergeType == MergeType.MERGE ) && ( !newTarget.isEmpty() ) )
                {
                     target.addAll( newTarget ) ;
                }
            }
        }
        else if ( mergeType == MergeType.SYNCHRONIZE  )
        {
            target.clear() ;
        }
    }

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param name                jméno proměnné
     *
     * @return vyzvednuta hodnota
     */
    public Boolean extractBoolean( String name )
    {
        return extractRecord( name, Boolean.class ) ;
    }

    /**
     * Vyzvednutí výsledku funkce
     *
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> T extractResult(Class<T> returnType)
    {
        Object obj = get(StoredProcedureInvoker.RETURN_VALUE_NAME) ;

        return ObjectHelper.extractFromObject(obj, returnType) ;
    }

    /**
     * Vyzvednutí výsledku funkce
     *
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T extends StructConvertable> T extractResultStruct(Class<T> returnType)
    {
        return extractStruct( StoredProcedureInvoker.RETURN_VALUE_NAME, returnType ) ;
    }

    /**
     * Vyzvednutí výsledku funkce (db pole)
     *
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> List<T> extractResultArray( Class<T> returnType )
    {
        Object obj = get(StoredProcedureInvoker.RETURN_VALUE_NAME) ;

        if ( obj instanceof List )
        {
            //noinspection unchecked
            return (List<T>) obj;
        }
        else if ( obj instanceof ARRAY)
        {
            try
            {
                return extractFromArray( (Object[])((ARRAY) obj).getArray(), returnType ) ;
            }
            catch (SQLException e)
            {
                throw new IllegalStateException( "Chyba při převodu DB pole na JAVA pole", e )  ;
            }
        }
        else if ( obj != null )
        {
            throw new IllegalStateException( "Návratová hodnota má špatný typ:" + obj.getClass() )  ;
        }

        return null ;
    }

    /**
     * Vyzvednutí výsledku funkce (PL/SQL table)
     *
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> List<T> extractResultTable( Class<T> returnType )
    {
        return extractTable( StoredProcedureInvoker.RETURN_VALUE_NAME, returnType ) ;
    }

    /**
     * Vyzvednutí výsledku funkce (PL/SQL record)
     *
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    public <T> T extractResultRecord(Class<T> returnType)
    {
        return extractRecord( StoredProcedureInvoker.RETURN_VALUE_NAME, returnType ) ;
    }


    public Date getDate( String name )
    {
        return extract( name, Date.class ) ;
    }

    public String getString( String name )
    {
        return extract( name, String.class ) ;
    }

    public Long getLong( String name )
    {
        return extract( name, Long.class ) ;
    }

    public <T> T getResultValue( Class<T> type )
    {
        return extract( StoredProcedureInvoker.RETURN_VALUE_NAME, type ) ;
    }

    public BigInteger getBigInteger( String name )
    {
        return extract( name, BigInteger.class ) ;
    }

    public Integer getInteger( String name )
    {
        return extract( name, Integer.class ) ;
    }
}
