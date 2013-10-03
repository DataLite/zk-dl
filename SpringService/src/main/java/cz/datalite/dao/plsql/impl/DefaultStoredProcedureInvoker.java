package cz.datalite.dao.plsql.impl;

import cz.datalite.dao.plsql.*;
import cz.datalite.dao.plsql.annotations.SqlField;
import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.helpers.StringHelper;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import javax.persistence.Column;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * User: karny
 * Date: 1/9/12
 * Time: 3:58 PM
 */
class DefaultStoredProcedureInvoker extends StoredProcedure   implements StoredProcedureInvoker
{
	Map<String, Object> inputs = new HashMap<String, Object>();

    // Oracle neumi az do verze 11g vybalit z DBCP pool native connection. Je potreba pomoci prostredku springu.
    NativeJdbcExtractor nativeJdbcExtractor = new CommonsDbcpNativeJdbcExtractor();

    /**
     * Faktory pro vytvoreni LOB hodnoty parametru
     */
    SqlLobValueFactory sqlLobValueFactory ;

    /**
     * Příznak zda je nutné provést změnu dotazu
     */
    boolean wrapNeed = false ;

    public static final String FIELD_BOOLEAN = "b" ;
    public static final String FIELD_NUMERIC = "n" ;
    public static final String FIELD_STRING = "s" ;
    public static final String FIELD_DATE = "d" ;


    static class FieldInfo
    {
        String fieldName ;
        Class<?> type ;

        FieldInfo(String fieldName, Class<?> type)
        {
            this.fieldName = fieldName;
            this.type = type;
        }

        public boolean isPrimitive()
        {
          return (      ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_BOOLEAN ) )
                    || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_DATE ) )
                    || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_STRING ) )
                    || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_NUMERIC ) ) ) ;
        }

    }

    /**
     * Synchronizační zámek
     */
    final static Object fieldsMapLocker = new Object() ;

    /**
     * Cache pro uložení mapování položek
     */
    static Map<Class<?>, Map<String, FieldInfo>> fieldsMap = new HashMap<Class<?>, Map<String, FieldInfo>>() ;

    /**
     * Získání SQL pro převod vstupní struktury na PL/SQL strukturu
     *
     * @param entityClass          zdrojová entita
     *
     * @return SQL script
     */
    private static Map<String, FieldInfo> getFieldMaps( Class<?> entityClass  )
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
    private static Map<String, FieldInfo> generateFieldMaps( Class<?> entityClass )
    {
        Map<String, FieldInfo> result = new HashMap<String, FieldInfo>() ;

        if ( isBoolean( entityClass ) )
        {
             result.put( FIELD_BOOLEAN, new FieldInfo( FIELD_BOOLEAN, Boolean.class ) ) ;
        }
        else if ( isNumeric( entityClass  ) )
        {
            result.put( FIELD_NUMERIC, new FieldInfo( FIELD_NUMERIC, Long.class ) ) ;
        }
        else if ( isString( entityClass  ) )
        {
            result.put( FIELD_STRING, new FieldInfo( FIELD_STRING, String.class ) ) ;
        }
        else if ( isDate( entityClass  ) )
        {
            result.put( FIELD_DATE, new FieldInfo( FIELD_DATE, Date.class ) ) ;
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
    private static void generateFieldMaps( Map<String, FieldInfo> result, Field field )
    {
        if ( field.isAnnotationPresent( SqlField.class ) )
        {
            SqlField sqlField = field.getAnnotation( SqlField.class ) ;

            result.put(sqlField.value(), new FieldInfo( field.getName(), field.getType() ) ) ;
        }
        else if ( field.isAnnotationPresent( Column.class ) )
        {
            Column sqlField = field.getAnnotation( Column.class ) ;

            result.put( sqlField.name(), new FieldInfo( field.getName(), field.getType() ) ) ;
        }
    }


    public DefaultStoredProcedureInvoker( DataSource dataSource, SqlLobValueFactory sqlLobValueFactory )
	{
		super( dataSource, "" ) ;

        getJdbcTemplate().setNativeJdbcExtractor(nativeJdbcExtractor);

        this.sqlLobValueFactory = sqlLobValueFactory ;
	}

    public DefaultStoredProcedureInvoker( DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory )
    {
        this( dataSource, sqlLobValueFactory ) ;

        setName( name ) ;
    }

    public DefaultStoredProcedureInvoker( DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory )
    {
        this( dataSource, name, sqlLobValueFactory ) ;
        declareReturnParameter( resultType ) ;
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
            result.add( extractFromObject( v, returnType ) ) ;
        }

        return result ;
    }

    /**
     * @param name		Jmeno parametru
     * @return funkce pro zjisteni zda jiz existuje definice parametru
     */
    private boolean isParameterFounded( String name )
    {
        for( SqlParameter parameter : getDeclaredParameters() )
        {
            if (name.equalsIgnoreCase(parameter.getName()) )
            {
                return true ;
            }
        }

        return false ;
    }

    /**
     * Deklarovani vystupniho parametru
     *
     * @param param		Vystupni parametr
     * @return vystupni parametr
     */
    private SqlParameter declareOutParameter( SqlParameter param )
    {
        final String name = param.getName() ;

        if ( isParameterFounded( name ) )
        {
            throw new IllegalStateException( "Výstupní parameter '" + name + "' je již definován" ) ;
        }

        super.declareParameter( param ) ;

        return param ;
    }

    /**
     * Vybaleni connection z pool az na native connection.
     *
     * @return native connection
     * @throws java.sql.SQLException chyba pri vybaleni
     */
    private Connection getNativeConnection() throws SQLException
    {
        Connection con = DataSourceUtils.getConnection(getJdbcTemplate().getDataSource());

        return nativeJdbcExtractor.getNativeConnection(con);
    }

    /**
     * Nastaveni vlastnosti vstupniho parametru
     *
     * @param sqlParameter		Vstupni parametr
     * @param value 			Hodnota parametru
     * @return vstupni parametr
     *
     */
    private SqlParameter createInputParameter( SqlParameter sqlParameter, Object value )
    {
        Object newValue = value ;
        final int type = sqlParameter.getSqlType() ;
        final String name = sqlParameter.getName() ;

        if ( isParameterFounded( name ) )
        {
            throw new IllegalStateException( "Vstupní parameter '" + name + "' je již definován" ) ;
        }

        if ( value != null )
        {
            if ( ( value instanceof Boolean ) && ( type == Types.VARCHAR ) )
            {
                newValue = ( (Boolean)value ) ? "A" : "N" ;
            }
            else if ( value instanceof Enum )
            {
                newValue = ((Enum) value).name() ;
            }
            else
            {
                newValue = value ;
            }
        }

        inputs.put( name, newValue ) ;

        return sqlParameter;
    }

    /**
     * Nastaveni vlastnosti vstupniho parametru
     *
     * @param sqlParameter		Vstupni parametr
     * @param value 			Hodnota parametru
     * @return vstupni parametr
     *
     */
    private SqlParameter declareParameter( SqlParameter sqlParameter, Object value )
    {
        declareParameter( createInputParameter( sqlParameter, value ) ) ;

        return sqlParameter;
    }

    @Override
    public List<SqlParameter> getDeclaredParameters()
    {
        return super.getDeclaredParameters() ;
    }


    @Override
    public void setName(String name)
    {
        setSql( name ) ;
    }


    @Override
 	public SqlParameter declareReturnParameter( int type )
	{
		if (  isParameterFounded( RETURN_VALUE_NAME ) )
		{
			throw new IllegalStateException( "Návratový parametr je již definován" ) ;
		}

		setFunction(true);

		return declareOutParameter( RETURN_VALUE_NAME, type ) ;
	}

    @Override
    public SqlParameter declareReturnStructParameter( String typeName )
    {
        if (  isParameterFounded( RETURN_VALUE_NAME ) )
        {
            throw new IllegalStateException( "Návratový parametr je již definován" ) ;
        }

        setFunction(true);

        return declareOutParameter( new SqlOutParameter( RETURN_VALUE_NAME,  Types.STRUCT, typeName ) ) ;
    }

    @Override
    public SqlParameter declareReturnArrayParameter( String typeName )
    {
        if (  isParameterFounded( RETURN_VALUE_NAME ) )
        {
            throw new IllegalStateException( "Návratový parametr je již definován" ) ;
        }

        setFunction(true);

        return declareOutParameter( new SqlOutParameter( RETURN_VALUE_NAME,  Types.ARRAY, typeName ) ) ;
    }


    @Override
    public <T> SqlParameter declareReturnRecordParameter( String dbType, Class<T> entityClass )
    {
        if (  isParameterFounded( RETURN_VALUE_NAME ) )
        {
            throw new IllegalStateException( "Návratový parametr je již definován" ) ;
        }

        setFunction(true);

        return declareOutRecordParameter(RETURN_VALUE_NAME, dbType, entityClass) ;
    }

    @Override
    public <T> SqlParameter declareReturnTableParameter( String dbType, Class<T> entityClass )
    {
        if (  isParameterFounded( RETURN_VALUE_NAME ) )
        {
            throw new IllegalStateException( "Návratový parametr je již definován" ) ;
        }

        setFunction(true);

        return declareOutTableParameter(RETURN_VALUE_NAME, dbType, entityClass) ;
    }


    @Override
    public SqlParameter declareReturnBooleanParameter()
    {
        return declareReturnRecordParameter( "boolean", Boolean.class ) ;
    }

    @Override
    public SqlParameter declareOutParameter(String name, int type)
	{
		return declareOutParameter( new SqlOutParameter( name, type ) ) ;
	}

    @Override
    public SqlParameter declareOutStructParameter(String name, String dbType )
    {
        return declareOutParameter( new SqlOutParameter( name, Types.STRUCT, dbType ) ) ;
    }

    @Override
    public SqlParameter declareOutArrayParameter(String name, String dbType )
    {
        return declareOutParameter( new SqlOutParameter( name, Types.ARRAY, dbType ) ) ;
    }

    @Override
    public <T> SqlParameter declareOutRecordParameter( String name, String dbType, Class<T> entityClass )
    {
        wrapNeed = true ;

        return declareOutParameter( new RecordOutParameter<T>( name, entityClass, name, dbType ) ) ;
    }

    @Override
    public SqlParameter declareOutBooleanParameter( String name )
    {
        return declareOutRecordParameter( name, "boolean", Boolean.class) ;
    }

    @Override
    public <T> SqlParameter declareOutTableParameter(String name, String dbType, Class<T> entityClass)
    {
        wrapNeed = true ;

        return declareOutParameter( new RecordOutParameter<T>( name, entityClass, name, dbType, true ) ) ;
    }

    @Override
    public SqlParameter declareInOutParameter( String name, int type, Object value )
    {
        Object v = value ;

        if ( ( type == Types.CLOB ) || ( type == Types.BLOB ) )
        {
            v = sqlLobValueFactory.createLobValue( value ) ;
        }
        else if ( type == Types.BOOLEAN )
        {
            type = Types.VARCHAR ;
        }
        else if ( ( type == Types.ARRAY ) || ( type == Types.STRUCT ) )
        {
            throw new UnsupportedOperationException() ;
        }

        return declareParameter(new SqlInOutParameter( name, type ), v ) ;
    }

    @Override
    public <E extends StructConvertable> SqlParameter declareInOutStructParameter( String name, String type, E value )
    {
        try
        {
            STRUCT struct = null ;

            if ( value != null )
            {
                StructDescriptor descriptor = StructDescriptor.createDescriptor( type, getNativeConnection() ) ;

                struct = new STRUCT( descriptor, getNativeConnection(), value.getStructureAttributes() ) ;
            }

            return declareParameter( new SqlInOutParameter( name, Types.STRUCT, type ), struct ) ;
        }
        catch (SQLException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }


    @Override
    public <T> SqlParameter declareInOutArrayParameter(String name, String type, List<T> value )
    {
        try
        {
            ARRAY array = null ;

            if ( value != null )
            {
                ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor( type, getNativeConnection() ) ;

                array = new ARRAY( descriptor, getNativeConnection(), value.toArray() ) ;
            }

            return declareParameter( new SqlInOutParameter( name, Types.ARRAY, type ), array ) ;
        }
        catch (SQLException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }

    @Override
    public <T> SqlParameter declareInOutRecordParameter( String name, String dbType, Class<T> entityClass, T value )
    {
        wrapNeed = true ;

        return declareParameter(new RecordInOutParameter<T>(name, entityClass, name, dbType), value) ;
    }

    @Override
    public SqlParameter declareInOutNullParameter( String name, int type )
    {
        return declareInOutParameter( name, type, null ) ;
    }

    @Override
    public SqlParameter declareInOutBooleanParameter(String name, Boolean value)
    {
        return declareInOutRecordParameter(name, "boolean", Boolean.class, value) ;
    }

    @Override
    public <T> SqlParameter declareInOutTableParameter(String name, String dbType, Class<T> entityClass, List<T> value)
    {
        wrapNeed = true ;

        return declareParameter(new RecordInOutParameter<T>(name, entityClass, name, dbType, true ), value) ;
    }

    @Override
    public SqlParameter setParameter( String name, int type, Object value )
    {
        Object v = value ;

        if ( ( type == Types.STRUCT ) || ( type == Types.ARRAY ) )
        {
            throw new UnsupportedOperationException() ;
        }
        else if ( ( type == Types.CLOB ) || ( type == Types.BLOB ) )
        {
            v = sqlLobValueFactory.createLobValue( value ) ;
        }
        else if ( type == Types.BOOLEAN )
        {
            type = Types.VARCHAR ;
        }

        return declareParameter(new SqlParameter(name, type), v) ;
    }


    @Override
    public <E extends StructConvertable> SqlParameter setStructParameter( String name, String typeName, E value )
    {
        try
        {
            STRUCT struct = null ;

            if ( value != null )
            {
                StructDescriptor descriptor = StructDescriptor.createDescriptor( typeName, getNativeConnection() ) ;

                struct = new STRUCT( descriptor, getNativeConnection(), value.getStructureAttributes() ) ;
            }

            return declareParameter( new SqlParameter( name, Types.STRUCT, typeName ), struct ) ;
        }
        catch (SQLException e)
        {
            throw new IllegalStateException( e ) ;
        }
    }


    @Override
    public <E> SqlParameter setArrayParameter( String name, String typeName, List<E> value )
	{
        try
        {
            ARRAY array = null ;

            if ( value != null )
            {
                ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor( typeName, getNativeConnection() ) ;

                array = new ARRAY( descriptor, getNativeConnection(), value.toArray() ) ;
            }

            return declareParameter( new SqlParameter( name, Types.ARRAY, typeName ), array ) ;
        }
        catch (SQLException e)
        {
            throw new IllegalStateException( e ) ;
        }
	}

    @Override
    public <T> SqlParameter setRecordParameter( String name, String dbType, Class<T> entityClass, T value )
    {
        wrapNeed = true ;

        return declareParameter( new RecordInParameter<T>( name, entityClass, name, dbType ), value ) ;
    }

    @Override
    public SqlParameter setNullParameter(String name, int type)
    {
        return setParameter( name, type, null );
    }


    @Override
    public SqlParameter setBooleanParameter(String name, Boolean value)
    {
        return setRecordParameter( name, "boolean", Boolean.class, value ) ;
    }

    public <T> SqlParameter setTableParameter( String name, String dbType, Class<T> entityClass, List<T> value )
    {
        wrapNeed = true ;

        return declareParameter( new RecordInParameter<T>( name, entityClass, name, dbType, true ), value ) ;
    }

    @Override
    public <T> T getResult( Class<T> returnType )
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný DB objekt není funkce" ) ;
        }

        return extractResult(execute(), returnType) ;
    }

    @Override
    public <T extends StructConvertable> T getResultStruct( Class<T> returnType )
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný DB objekt není funkce" ) ;
        }

        return extractResult( execute(), returnType ) ;
    }

    @Override
    public <T> List<T> getResultArray( Class<T> returnType )
	{
		if ( ! isFunction() )
		{
			throw new IllegalStateException( "Volaný objekt není funkce" ) ;
		}

        return extractResultArray(execute(), returnType) ;
	}

    @Override
    public <T> T getResultRecord( Class<T> returnType )
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný objekt není funkce" ) ;
        }

        return extractRecord(execute(), RETURN_VALUE_NAME, returnType) ;
    }

    @Override
    public Boolean isResultBoolean()
    {
        return getResultRecord( Boolean.class ) ;
    }

    @Override
    public <T> List<T> getResultTable(Class<T> returnType)
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný objekt není funkce" ) ;
        }

        return extractTable(execute(), RETURN_VALUE_NAME, returnType) ;
    }

    @Override
    public <T> T extractResult( StoredProcedureResult resultMap, Class<T> returnType )
    {
        Object obj = resultMap.get(RETURN_VALUE_NAME) ;

        return extractFromObject( obj, returnType) ;
    }

    @Override
    public <T> List<T> extractResultArray( StoredProcedureResult resultMap, Class<T> returnType )
    {
        Object obj = resultMap.get(RETURN_VALUE_NAME) ;

        if ( obj instanceof List )
        {
            //noinspection unchecked
            return (List<T>) obj;
        }
        else if ( obj instanceof ARRAY )
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

    @Override
    public <T> List<T> extractResultTable(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return extractTable( resultMap, RETURN_VALUE_NAME, returnType ) ;
    }

    @Override
    public StoredProcedureResult execute()
    {
        if ( wrapNeed )
        {
            generateWrappedQuery() ;
        }

        try
        {
            return new StoredProcedureResult( execute( inputs ) ) ;
        }
        catch ( DataAccessException e )
        {
            if ( e.getCause() instanceof SQLException )
            {
                SQLException sqlException = (SQLException) e.getCause() ;

                if ( sqlException.getErrorCode() == 4068 ) //Balík byl změněn
                {
                    return new StoredProcedureResult( execute( inputs ) ) ;
                }
            }

            throw e ;
        }
    }


    /**
     * Generování wraperu pro získání PL/SQL recordu
     */
    private void generateWrappedQuery()
    {
        StringBuilder sb = new StringBuilder() ;

        sb.append( "declare \n" ) ;

        sb.append( " ob char ;\n" ) ;  //Proměná pro uložení konvertované boolean hodnoty

        generateDeclarePlSqlVariables(sb) ;

        List<SqlParameter> newInputParameter = new ArrayList<SqlParameter>() ;
        List<SqlParameter> newOutputParameter = new ArrayList<SqlParameter>() ;


        sb.append( "begin\n" ) ;

        generateUsingVariables(sb, newInputParameter, true) ;

        generateCall( sb ) ;

        generateUsingVariables( sb, newOutputParameter, false ) ;

        sb.append( "end ;\n" ) ;

        generateNewParameterList( newInputParameter, newOutputParameter, new ArrayList<SqlParameter>( getDeclaredParameters() ) ) ;

        setName(sb.toString()) ;
        setSqlReadyForUse( true ) ;
    }

    /**
     * Generování nového seznamu parametrů
     *
     * @param newInputParameter     Nové vstupní parametr
     * @param newOutputParameter    Nový výstupní parametr
     * @param olds                  původní parametry
     */
    private void generateNewParameterList( List<SqlParameter> newInputParameter, List<SqlParameter> newOutputParameter, List<SqlParameter> olds )
    {
        getDeclaredParameters().clear(); //Odstranění všech dosud nagenerovaných parametrů

        if ( ( newInputParameter != null ) && ( ! newInputParameter.isEmpty() ) )
        {
            getDeclaredParameters().addAll( newInputParameter ) ;
        }

        //Překopírování původních parametrů, které nejsou wrapované
        ListIterator<SqlParameter> oldsIt = olds.listIterator() ;

        while (oldsIt.hasNext())
        {
            SqlParameter next = oldsIt.next();

            if ( next instanceof RecordParameter )
            {
                continue ;
            }

            getDeclaredParameters().add( next ) ;
            oldsIt.remove() ;
        }

        if ( ( newOutputParameter != null ) && ( ! newOutputParameter.isEmpty() ) )
        {
            getDeclaredParameters().addAll( newOutputParameter ) ;
        }


        //Odstranění hodnot vstupních parametrů, které již neexistuji
        for( SqlParameter sqlParameter : olds )
        {
            if ( ( sqlParameter instanceof RecordParameter ) && ( ((RecordParameter)sqlParameter).isInput() ) )
            {
                inputs.remove( sqlParameter.getName() ) ;
            }
        }
    }

    /**
     * Generování PL/SQL scriptu - volání procedury
     *
     * @param query     aktuální generovaný dotaz
     */
    private void generateCall( StringBuilder query )
    {
        if ( ! StringHelper.isNull(getSql()) )
        {
            boolean first = true ;

            for( SqlParameter sqlParameter : getDeclaredParameters() )
            {
                if ( first )
                {
                    if ( sqlParameter instanceof RecordParameter )
                    {
                        if ( isFunction() )
                        {
                            query.append( ((RecordParameter) sqlParameter).getVariableName() ).append( " := " ) ;
                        }
                    }
                    else if ( isFunction() )
                    {
                        query.append( " ? := " ) ;
                    }

                    query.append( getSql() ) ;


                    if ( ( ! isFunction() ) || ( getDeclaredParameters().size() > 1 ) )
                    {
                        query.append("(") ;
                    }

                    if ( ! isFunction() )
                    {
                        if ( sqlParameter instanceof RecordParameter )
                        {
                            query.append( ((RecordParameter) sqlParameter).getVariableName() ).append( ", " ) ;
                        }
                        else
                        {
                            query.append( "?, " ) ;
                        }
                    }

                    first = false ;
                }
                else if ( sqlParameter instanceof RecordParameter )
                {
                    query.append( ((RecordParameter) sqlParameter).getVariableName() ).append( ", " ) ;
                }
                else
                {
                    query.append( "?, " ) ;
                }
            }

            if ( first )
            {
                query.append( getSql() ) ;
            }
            else
            {
                if ( ", ".equals( query.substring( query.length() - 2 ) ) )
                {
                    query.delete( query.length() - 2, query.length() ) ;
                }

                if ( ( ! isFunction() ) || ( getDeclaredParameters().size() > 1 ) )
                {
                    query.append(")") ;
                }
            }

            query.append( ";\n" ) ;
        }
    }


    /**
     * Generování PL/SQL scriptu - deklarování proměnných
     *
     * @param query     aktuální generovaný dotaz
     */
    private void generateDeclarePlSqlVariables(StringBuilder query)
    {
        for( SqlParameter sqlParameter : getDeclaredParameters() )
        {
            if ( sqlParameter instanceof RecordParameter )
            {
                RecordParameter parameter = (RecordParameter) sqlParameter ;

                query.append( parameter.getVariableName() ).append( " " ).append( parameter.getDatabaseType() ).append( " ;\n" ) ;

                if ( ( parameter.isArray() ) && ( parameter.isOutput() ) ) //Pokud se jedná o vystupní pole musí se nagenerovat pro jednotlivé položky strukty nové proměnné typu DB pole
                {
                    generateDeclareArrayVariables( query, parameter ) ;
                }
            }
        }
    }

    private Map<String, String> long2shortName = new HashMap<String, String>() ;

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
     * Generování PL/SQL scriptu - deklarování proměnných pro uložení načteného pole
     *
     * @param query             aktuální generovaný dotaz
     * @param parameter         aktuální generovaný parametr
     */
    private void generateDeclareArrayVariables( StringBuilder query, RecordParameter parameter )
    {
        for( Map.Entry<String, FieldInfo> field : getFieldMaps( parameter.getTargetEntity() ).entrySet() )
        {
            String variableName = compressName( parameter.getVariableName() + "_" + field.getKey() ) ;

            query.append( variableName ).append( " " ) ;

            if ( isNumeric( field.getValue() ) )
            {
               query.append( " NXT.NUMBER_TABLE := NXT.NUMBER_TABLE() " ) ;
            }
            else if ( isDate(field.getValue()) )
            {
                query.append( " NXT.DATE_TABLE := NXT.DATE_TABLE() " ) ;
            }
            else
            {
                query.append( " NXT.VARCHAR_TABLE := NXT.VARCHAR_TABLE() " ) ;
            }

            query.append( " ;\n" ) ;
        }
    }


    /**
     * Generování PL/SQL scriptu - použití proměnných
     *
     * @param query     aktuální generovaný dotaz
     * @param load      příznak, zda se provádí načtení z javy (true) nebo ukládání do javy (false)
     */
    private void generateUsingVariables(StringBuilder query, List<SqlParameter> newParameters, boolean load )
    {
        for( SqlParameter next : getDeclaredParameters() )
        {
            if ( next instanceof RecordParameter )
            {
                RecordParameter parameter = (RecordParameter) next ;

                if ( ( ( parameter.isInput() ) && ( load ) ) || ( ( parameter.isOutput() ) && ( ! load ) ) )
                {
                    generateUsingVariables( query, parameter, newParameters, load ) ;
                }
            }
        }
    }

    /**
     * Generování PL/SQL scriptu - deklarování proměnných
     *
     * @param query             aktuální generovaný dotaz
     * @param parameter         aktuální generovaný parametr
     * @param newParameterList  seznam nově přidaných parametrů
     * @param load              příznak, zda se provádí načtení z javy (true) nebo ukládání do javy (false)
     */
    private void generateUsingVariables( StringBuilder query, RecordParameter parameter, List<SqlParameter> newParameterList, boolean load )
    {
        if ( parameter.isArray() )
        {
            generateUsingArrayVariables( query, parameter, newParameterList, load ) ;

            return ;
        }

        Object sourceValue = ( load ) ? inputs.get( parameter.getVariableName() ) : null ;

        for( Map.Entry<String, FieldInfo> field : getFieldMaps( parameter.getTargetEntity() ).entrySet() )
        {
            String parameterName ;
            String variableName ;

            if ( field.getValue().isPrimitive() )
            {
                parameterName = parameter.getName() + "." + field.getKey() ;
                variableName = parameter.getVariableName() ;
            }
            else
            {
                parameterName = parameter.getName() + "." + field.getKey() ;
                variableName = parameter.getVariableName() + "." + field.getKey() ;
            }

            if ( load )
            {
                if ( ( isBoolean( field.getValue().type ) ) && ( field.getValue().isPrimitive() ) )
                {
                    query.append( variableName ).append( " := ( ? = 'A' ) ;\n" ) ;
                }
                else
                {
                    query.append( variableName ).append( " := ? ;\n" ) ;
                }

                newParameterList.add( createInputParameter( new SqlParameter( parameterName, convertToSqlType( field.getValue() ) ), getFieldValue(field.getValue().fieldName, sourceValue, field.getValue().type) ) ) ;
            }
            else
            {
                parameterName = "o" + parameterName ;

                if ( ( isBoolean( field.getValue().type ) ) && ( field.getValue().isPrimitive() ) )
                {
                    query.append( " if " ).append( variableName ).append( " then " ).append( "\n" )
                                  .append( "  ob := 'A' ;\n")
                         .append( " else" ).append("\n" )
                                  .append( "  ob := 'N' ;\n")

                         .append( " end if ;\n" )
                    .append( " ? := ob ;\n" ) ;
                }
                else
                {
                    query.append( " ? := " ).append(variableName).append(" ;\n") ;
                }

                newParameterList.add( new SqlOutParameter( parameterName, convertToSqlType( field.getValue() ) ) ) ;
            }
        }
    }

    /**
     * Generování PL/SQL scriptu - deklarování proměnných
     *
     * @param query             aktuální generovaný dotaz
     * @param parameter         aktuální generovaný parametr
     * @param newParameterList  seznam nově přidaných parametrů
     * @param load              příznak, zda se provádí načtení z javy (true) nebo ukládání do javy (false)
     */
    private void generateUsingArrayVariables( StringBuilder query, RecordParameter parameter, List<SqlParameter> newParameterList, boolean load )
    {
        List<?> sourceList = ( load ) ? (List<?>) inputs.get(parameter.getVariableName()) : null;

        if ( load )
        {
            if ( sourceList != null )
            {
                for( int i=0; i<sourceList.size(); i++ )
                {
                    for( Map.Entry<String, FieldInfo> field : getFieldMaps( parameter.getTargetEntity() ).entrySet() )
                    {
                        String parameterName ;
                        String variableName ;

                        if ( field.getValue().isPrimitive() )
                        {
                            parameterName = parameter.getName() + "(" + i + ")." + field.getKey() ;
                            variableName = parameter.getVariableName() + "(" + i + ")" ;
                        }
                        else
                        {
                            parameterName = parameter.getName() + "(" + i + ")." + field.getKey() ;
                            variableName = parameter.getVariableName() + "(" + i + ")." + field.getKey() ;
                        }

                        if ( ( isBoolean( field.getValue().type ) )  && ( field.getValue().isPrimitive() ) )
                        {
                            query.append( variableName ).append( " := ( ? = 'A' ) ;\n" ) ;
                        }
                        else
                        {
                            query.append( variableName ).append( " := ? ;\n" ) ;
                        }

                        newParameterList.add( createInputParameter( new SqlParameter( parameterName, convertToSqlType( field.getValue() ) ), getFieldValue(field.getValue().fieldName, sourceList.get( i ), field.getValue().type) ) ) ;
                    }
                }
            }
        }
        else
        {
            String parameterName = parameter.getName() + "_COUNT" ;

            query.append( " ? := " ).append( parameter.getVariableName() ).append(".count ;\n") ;

            newParameterList.add( new SqlOutParameter( parameterName, Types.NUMERIC ) ) ;

            query.append( "if " ).append( parameter.getVariableName() ).append(".count > 0 then \n" ) ;
            query.append( "for i in  " ).append( parameter.getVariableName() ).append(".first .. ").append( parameter.getVariableName() ).append(".last \n" ) ;
            query.append("loop\n") ;
            query.append( "if " ).append( parameter.getVariableName() ).append(".exists( i ) then \n" ) ;

            for( Map.Entry<String, FieldInfo> field : getFieldMaps( parameter.getTargetEntity() ).entrySet() )
            {
                String  variableName = compressName( parameter.getVariableName() + "_" + field.getKey() ) ;

                query.append( variableName ).append( ".extend( 1 ) ;\n" ) ;
                query.append( variableName ).append( "( " ).append(variableName).append(".count ) := ").append( parameter.getVariableName() ).append( "( i )" ) ;

                if ( ! field.getValue().isPrimitive() )
                {
                    query.append( "." ).append( field.getKey() ) ;
                }

                query.append( " ;\n") ;
            }

            query.append("end if ;\n") ;
            query.append( "end loop ;\n") ;
            query.append("end if ;\n") ;

            for( Map.Entry<String, FieldInfo> field : getFieldMaps( parameter.getTargetEntity() ).entrySet() )
            {
                parameterName = compressName( "o" + parameter.getVariableName() + "_" + field.getKey() ) ;
                String variableName = compressName( parameter.getVariableName() + "_" + field.getKey() ) ;

                query.append( " ? := " ).append( variableName ).append("; \n ") ;

                if ( isNumeric( field.getValue() ) )
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, "NXT.NUMBER_TABLE" ) ) ;
                }
                else if ( isDate(field.getValue()) )
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, "NXT.DATE_TABLE" ) ) ;
                }
                else
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, "NXT.VARCHAR_TABLE" ) ) ;
                }
            }
        }
    }

    /**
     *
     * Funkce pro získání hodnoty
     *
     * @param fieldName         jméno položky
     * @param sourceValue       zdrojový objekt
     * @param returnType        návratový typ
     *
     * @return hodnota položky
     */
    private <T> T getFieldValue( String fieldName, Object sourceValue, Class<T> returnType )
    {
        if ( sourceValue == null )
        {
            return null ;
        }

        if( ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_BOOLEAN ) )
           || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_DATE ) )
           || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_STRING ) )
           || ( StringHelper.isEqualsIgnoreCase( fieldName, FIELD_NUMERIC ) )
        )
        {
            return extractFromObject( sourceValue, returnType ) ;
        }

        return ObjectHelper.getValue( fieldName, sourceValue, returnType ) ;
    }

    /**
     *
     * Převod java typu na SQL typ
     *
     * @param fieldInfo     popis položky
     * @return typ parametru
     */
    private int convertToSqlType( FieldInfo fieldInfo )
    {
        if ( isNumeric( fieldInfo ) )
        {
            return Types.NUMERIC ;
        }
        else if ( isDate( fieldInfo ) )
        {
            return Types.TIMESTAMP ;
        }

        return Types.VARCHAR ;
    }

    /**
     * @param type typ
     * @return true jedna se logickou hodnotu
     */
    private static boolean isBoolean( Class<?> type )
    {
        return ( ( type == Boolean.class ) || ( type == boolean.class ) ) ;
    }

    /**
     * @param fieldInfo     položka
     * @return true položka je číslo
     */
    private static boolean isNumeric( FieldInfo fieldInfo )
    {
        return isNumeric( fieldInfo.type ) ;
    }

    /**
     * @param type typ
     * @return true položka je číslo
     */
    private static boolean isNumeric( Class<?> type )
    {
        return ( ( type == Integer.class ) || ( type == Long.class ) || ( type == BigDecimal.class )  || ( type == int.class ) || ( type == long.class ) ) ;
    }

    /**
     * @param fieldInfo     položka
     * @return true položka je číslo
     */
    private static boolean isDate( FieldInfo fieldInfo )
    {
        return isDate(fieldInfo.type) ;
    }

    /**
     * @param type typ
     * @return true položka je číslo
     */
    private static boolean isDate( Class<?> type )
    {
        return ( type == Date.class )  ;
    }

    /**
     * @param type typ
     * @return true položka jsou znaky
     */
    private static boolean isString( Class<?> type )
    {
        return ( type == String.class )  ;
    }


    public <T> List<T> extractTable(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        List<T> result = new ArrayList<T>() ;

        long count = ObjectHelper.extractLong( resultMap.get( name + "_COUNT" ) ) ;

        Map<FieldInfo, List<?>> dataFromDB = new HashMap<FieldInfo, List<?>>() ;

        extractFromTable( resultMap, "o" + name, returnType, dataFromDB  ) ;

        if ( dataFromDB.size() == 1 )
        {
            FieldInfo fi = (FieldInfo) dataFromDB.keySet().toArray() [ 0 ] ;

            if ( fi.isPrimitive() )
            {
                for ( Object value : dataFromDB.get( fi ) )
                {
                    //noinspection unchecked
                    result.add((T) extractFromObject( value, fi.type ) ) ;
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

    @Override
    public <T> void extractTable(StoredProcedureResult resultMap, String name, Class<T> returnType, List<T> target, MergeType mergeType)
    {
        List<T> novySeznam = extractTable(resultMap, name, returnType) ;

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
            else
            {
                for( T item : novySeznam )
                {
                    if ( ! target.contains( item ) )
                    {
                        target.add( item ) ;
                    }
                }
            }
        }
    }



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

            ObjectHelper.setValue( fieldName.fieldName, target, ( fieldData == null ) ? null : extractFromObject( fieldData.get( index ), fieldName.type ) ) ;
        }
    }

    /**
     * Generování mapy zdrojových dat
     *
     * @param resultMap     Data z databáze
     * @param name          jmeno proměnné
     * @param returnType    návratový typ (typ polozky)
     * @param target        vygenerovaný seznam zdrojových dat
     */
    private <T> void extractFromTable( StoredProcedureResult resultMap, String name, Class<T> returnType, Map<FieldInfo, List<?>> target )
    {
        for( Map.Entry<String, FieldInfo> field : getFieldMaps( returnType ).entrySet() )
        {
            String parameterName = compressName( name + "_" + field.getKey() ) ;

            target.put( field.getValue(), extractFromArray( resultMap.get( parameterName ), field.getValue().type ) ) ;
        }
    }


    @Override
    public <T> T extractRecord(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        if ( isBoolean(returnType) )
        {
            //noinspection unchecked
            return (T)ObjectHelper.extractBoolean(resultMap.get("o" + name + "." + FIELD_BOOLEAN)) ;
        }
        else if ( isNumeric(returnType) )
        {
            //noinspection unchecked
            return extractFromObject(resultMap.get("o" + name + "." + FIELD_NUMERIC), returnType) ;
        }
        else if ( isDate(returnType) )
        {
            //noinspection unchecked
            return extractFromObject( resultMap.get( "o" + name + "." + FIELD_DATE ), returnType ) ;
        }
        else if ( isString( returnType ) )
        {
            //noinspection unchecked
            return extractFromObject( resultMap.get( "o" + name + "." + FIELD_STRING ), returnType ) ;
        }
        else
        {
            T result = ObjectHelper.newInstance( returnType ) ;

            boolean found = extractRecord(resultMap, name, result) ;

            return ( found ) ? result : null ;
        }
    }

    public <T> boolean extractRecord(StoredProcedureResult resultMap, String name, T returnValue)
    {
        boolean found = false ;

        for( Map.Entry<String, FieldInfo> field : getFieldMaps( returnValue.getClass() ).entrySet() )
        {
            String parameterName = "o" + name + "." + field.getKey() ;
            Object value = null ;

            if ( resultMap.containsKey( parameterName ) )
            {
                value = resultMap.get( parameterName ) ;
                found = true ;
            }

            ObjectHelper.setValue( field.getValue().fieldName, returnValue, extractFromObject( value, field.getValue().type ) ) ;
        }

        return found ;
    }

    /**
     * Převod objektu na cílový typ
     *
     * @param value             Převáděný objekt
     * @param returnType        požadovaný typ
     * @return převedená hodnota
     */
    private <T> T extractFromObject( Object value, Class<T> returnType )
    {
        if ( value instanceof STRUCT )
        {
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
        else
        {
            return ObjectHelper.extractFromObject( value, returnType ) ;
        }
    }

    /**
     * Převod objektu na cílový typ
     *
     * @param source             Převáděný objekt
     * @param target             Cílový objekt
     * @return cílový objekt
     */
    private <E extends StructConvertable> E extractFromStructure( Object source, E target )
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

    @Override
    public <T> T extract(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        Object obj = resultMap.get( name ) ;

        return extractFromObject( obj, returnType ) ;
    }

    @Override
    public <T extends StructConvertable> T extractStruct(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        Object obj = resultMap.get( name ) ;

        return extractFromObject( obj, returnType ) ;
    }

    @Override
    public <T> List<T> extractArray(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        Object obj = resultMap.get( name ) ;

        return extractFromArray( obj, returnType ) ;
    }

    @Override
    public Boolean extractBoolean(StoredProcedureResult resultMap, String name)
    {
        return extractRecord( resultMap, name, Boolean.class ) ;
    }

    @Override
    public <T extends StructConvertable> T extractResultStruct(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return extractStruct( resultMap, RETURN_VALUE_NAME, returnType ) ;
    }

    @Override
    public <T> T extractResultRecord(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return extractRecord( resultMap, RETURN_VALUE_NAME, returnType ) ;
    }
}
