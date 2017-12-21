package cz.datalite.dao.plsql.impl;

import cz.datalite.dao.plsql.*;
import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.StringHelper;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * User: karny
 * Date: 1/9/12
 * Time: 3:58 PM
 */
@SuppressWarnings({"WeakerAccess", "unused"})
class AbstractStoredProcedureInvoker extends StoredProcedure   implements StoredProcedureInvoker
{
    private final static Logger PLSQL_LOGGER = LoggerFactory.getLogger(AbstractStoredProcedureInvoker.class + ".plsql");

    private Map<String, Object> inputs = new HashMap<String, Object>();


    /**
     * Faktory pro vytvoreni LOB hodnoty parametru
     */
    private SqlLobValueFactory sqlLobValueFactory ;

    /**
     * Database schema.
     * SQL object types like NUMBER_TABLE or VARCHAR_TABLE are resolved with this schema.
     */
    private String databaseSchema;

    /**
     * Příznak zda je nutné provést změnu dotazu
     */
    private boolean wrapNeed = false ;

    private EntityManager entityManager ;

    private boolean dbmsOutput = false ;
    private int sizeOutput = Integer.MAX_VALUE ;

    public AbstractStoredProcedureInvoker(DataSource dataSource, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
	{
		super( dataSource, "" ) ;

        this.entityManager = entityManager ;
        this.sqlLobValueFactory = sqlLobValueFactory ;
        this.databaseSchema = databaseSchema;
	}

    public AbstractStoredProcedureInvoker(DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, name, sqlLobValueFactory, databaseSchema, entityManager, null  ) ;
    }

    public AbstractStoredProcedureInvoker(DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager )
    {
        this( dataSource, name, resultType, sqlLobValueFactory, databaseSchema, entityManager, null ) ;
    }

    public AbstractStoredProcedureInvoker(DataSource dataSource, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        super( dataSource, "" ) ;

        this.entityManager = entityManager ;
        this.sqlLobValueFactory = sqlLobValueFactory ;
        this.databaseSchema = databaseSchema;

        getJdbcTemplate().setNativeJdbcExtractor( extractor ) ;
    }

    public AbstractStoredProcedureInvoker(DataSource dataSource, String name, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        this( dataSource, sqlLobValueFactory, databaseSchema, entityManager, extractor ) ;

        setName( name ) ;
    }

    public AbstractStoredProcedureInvoker(DataSource dataSource, String name, int resultType, SqlLobValueFactory sqlLobValueFactory, String databaseSchema, EntityManager entityManager, NativeJdbcExtractor extractor )
    {
        this( dataSource, name, sqlLobValueFactory, databaseSchema, entityManager, extractor ) ;
        declareReturnParameter( resultType ) ;
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
     * @throws SQLException chyba pri vybaleni
     */
    private Connection getNativeConnection() throws SQLException
    {
        Connection con = DataSourceUtils.getConnection(getJdbcTemplate().getDataSource());

        return ( getJdbcTemplate().getNativeJdbcExtractor() != null ) ? getJdbcTemplate().getNativeJdbcExtractor().getNativeConnection(con) : con ;
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

        return execute().extractResult( returnType ) ;
    }

    @Override
    public <T extends StructConvertable> T getResultStruct( Class<T> returnType )
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný DB objekt není funkce" ) ;
        }

        return execute().extractResult( returnType ) ;
    }

    @Override
    public <T> List<T> getResultArray( Class<T> returnType )
	{
		if ( ! isFunction() )
		{
			throw new IllegalStateException( "Volaný objekt není funkce" ) ;
		}

        return execute().extractResultArray( returnType ) ;
	}

    @Override
    public <T> T getResultRecord( Class<T> returnType )
    {
        if ( ! isFunction() )
        {
            throw new IllegalStateException( "Volaný objekt není funkce" ) ;
        }

        //noinspection deprecation
        return execute().extractRecord( RETURN_VALUE_NAME, returnType) ;
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

        //noinspection deprecation
        return execute().extractTable( RETURN_VALUE_NAME, returnType) ;
    }

    @Override
    @Deprecated
    public <T> T extractResult( StoredProcedureResult resultMap, Class<T> returnType )
    {
        return resultMap.extractResult(returnType) ;
    }

    @Override
    @Deprecated
    public <T> List<T> extractResultArray( StoredProcedureResult resultMap, Class<T> returnType )
    {
        return resultMap.extractResultArray( returnType  ) ;
    }

    @Override
    @Deprecated
    public <T> List<T> extractResultTable(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return resultMap.extractResultTable(returnType) ;
    }

    @Override
    @Deprecated
    public StoredProcedureResult execute()
    {
        return executeIndex() ;
    }

    /**
     * Generování wraperu pro získání PL/SQL recordu
     * @param named     příznak, zda zasilat parametry s jménem (true) nebo podle pořadí (false)
     */
    private void generateWrappedQuery( boolean named )
    {
        StringBuilder sb = new StringBuilder() ;

        sb.append( "declare \n" ) ;
        sb.append( " ob char ;\n" ) ;  //Proměná pro uložení konvertované boolean hodnoty
        sb.append( " l_line varchar2(255); " ) ;
        sb.append( " l_done number; " ) ;
        sb.append( " l_buffer long; " ) ;

        generateDeclarePlSqlVariables(sb) ;

        List<SqlParameter> newInputParameter = new ArrayList<>() ;
        List<SqlParameter> newOutputParameter = new ArrayList<>() ;


        sb.append( "begin\n" ) ;

        if ( dbmsOutput )
        {
            sb.append( "dbms_output.enable( " ).append( sizeOutput ).append( ") ;\n" ) ;
        }

        generateUsingVariables(sb, newInputParameter, true) ;

        generateCall( sb, named ) ;

        generateUsingVariables( sb, newOutputParameter, false ) ;

        if ( dbmsOutput )
        {
            sb.append( "\n" ) ;
            sb.append( "loop\n" ) ;
            sb.append(   "exit when length(l_buffer)+255 > " ).append( sizeOutput ).append(" OR l_done = 1 ;\n" ) ;
            sb.append(   "dbms_output.get_line( l_line, l_done ); \n" ) ;
            sb.append(   "l_buffer := l_buffer || l_line || chr(10); \n" ) ;
            sb.append( "end loop; \n" ) ;
            sb.append( "? := l_buffer;\n" ) ;
            sb.append( "dbms_output.disable() ;\n" ) ;

            newOutputParameter.add( new SqlOutParameter( "dbms_output", Types.LONGVARCHAR ) ) ;
        }

        sb.append( "end ;\n" ) ;

        generateNewParameterList( newInputParameter, newOutputParameter, new ArrayList<>( getDeclaredParameters() ) ) ;

        setName(sb.toString()) ;
        setSqlReadyForUse( true ) ;

        if ( dbmsOutput )
        {
            System.out.println( getSql() ) ;
        }
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
     * @param named     příznak, zda zasilat parametry s jménem (true) nebo podle pořadí (false)
     */
    private void generateCall( StringBuilder query, boolean named )
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
                        if ( named )
                        {
                            query.append( sqlParameter.getName() ).append( "=>" ) ;
                        }

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
                    if ( named )
                    {
                        query.append( sqlParameter.getName() ).append( "=>" ) ;
                    }

                    query.append( ((RecordParameter) sqlParameter).getVariableName() ).append( ", " ) ;
                }
                else
                {
                    if ( named )
                    {
                        query.append( sqlParameter.getName() ).append( "=>" ) ;
                    }

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

    private Set<Map.Entry<String, FieldInfo>> getFieldEntrySets( Class entityClass )
    {
        //noinspection unchecked
        return FieldMaps.getFieldMaps(entityClass).entrySet() ;
    }

    /**
     * Generování PL/SQL scriptu - deklarování proměnných pro uložení načteného pole
     *
     * @param query             aktuální generovaný dotaz
     * @param parameter         aktuální generovaný parametr
     */
    private void generateDeclareArrayVariables( StringBuilder query, RecordParameter parameter )
    {
        for( Map.Entry<String, FieldInfo>  field : getFieldEntrySets(parameter.getTargetEntity()) )
        {
            String variableName = compressName( parameter.getVariableName() + "_" + field.getKey() ) ;

            query.append( variableName ).append( " " ) ;

            if ( ObjectHelper.isNumeric(field.getValue()) )
            {
               query.append( " " ).append( getDatabaseSchema() ).append(  ".NUMBER_TABLE := " ).append( getDatabaseSchema() ).append( ".NUMBER_TABLE() " ) ;
            }
            else if ( ObjectHelper.isDate(field.getValue()) )
            {
                query.append(" ").append(getDatabaseSchema()).append(".DATE_TABLE := ").append(getDatabaseSchema()).append(".DATE_TABLE() ");
            }
            else
            {
                query.append(" ").append(getDatabaseSchema()).append(".VARCHAR_TABLE := ").append(getDatabaseSchema()).append(".VARCHAR_TABLE() ");
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

        for( Map.Entry<String, FieldInfo> field : getFieldEntrySets(parameter.getTargetEntity()) )
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
                if ( ( ObjectHelper.isBoolean(field.getValue().getType()) ) && ( field.getValue().isPrimitive() ) )
                {
                    query.append( variableName ).append( " := ( ? = 'A' ) ;\n" ) ;
                }
                else
                {
                    query.append( variableName ).append( " := ? ;\n" ) ;
                }

                newParameterList.add( createInputParameter( new SqlParameter( parameterName, convertToSqlType( field.getValue() ) ), getFieldValue(field.getValue().getFieldName(), sourceValue, field.getValue().getType()) ) ) ;
            }
            else
            {
                parameterName = "o" + parameterName ;

                if ( ( ObjectHelper.isBoolean(field.getValue().getType()) ) && ( field.getValue().isPrimitive() ) )
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
                    for( Map.Entry<String, FieldInfo> field : getFieldEntrySets(parameter.getTargetEntity()) )
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

                        if ( ( ObjectHelper.isBoolean(field.getValue().getType()) )  && ( field.getValue().isPrimitive() ) )
                        {
                            query.append( variableName ).append( " := ( ? = 'A' ) ;\n" ) ;
                        }
                        else
                        {
                            query.append( variableName ).append( " := ? ;\n" ) ;
                        }

                        newParameterList.add( createInputParameter( new SqlParameter( parameterName, convertToSqlType( field.getValue() ) ), getFieldValue(field.getValue().getFieldName(), sourceList.get( i ), field.getValue().getType()) ) ) ;
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

            for( Map.Entry<String, FieldInfo> field : getFieldEntrySets(parameter.getTargetEntity()) )
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

            for( Map.Entry<String, FieldInfo> field : getFieldEntrySets(parameter.getTargetEntity()) )
            {
                parameterName = compressName( "o" + parameter.getVariableName() + "_" + field.getKey() ) ;
                String variableName = compressName( parameter.getVariableName() + "_" + field.getKey() ) ;

                query.append( " ? := " ).append( variableName ).append("; \n ") ;

                if ( ObjectHelper.isNumeric(field.getValue()) )
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, getDatabaseSchema() + ".NUMBER_TABLE" ) ) ;
                }
                else if ( ObjectHelper.isDate(field.getValue()) )
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, getDatabaseSchema() + ".DATE_TABLE" ) ) ;
                }
                else
                {
                    newParameterList.add( new SqlOutParameter( parameterName, Types.ARRAY, getDatabaseSchema() + ".VARCHAR_TABLE" ) ) ;
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
            return ObjectHelper.extractFromObject( sourceValue, returnType ) ;
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
        if ( ObjectHelper.isNumeric(fieldInfo) )
        {
            return Types.NUMERIC ;
        }
        else if ( ObjectHelper.isDate(fieldInfo) )
        {
            return Types.TIMESTAMP ;
        }

        return Types.VARCHAR ;
    }

    @Override
    @Deprecated
    public <T> List<T> extractTable(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        return resultMap.extractTable( name, returnType ) ;
    }

    @Override
    @Deprecated
    public <T> void extractTable(StoredProcedureResult resultMap, String name, Class<T> returnType, List<T> target, MergeType mergeType)
    {
        resultMap.extractTable( name, returnType, target, mergeType ) ;
    }

    @Override
    @Deprecated
    public <T> T extractRecord(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        return resultMap.extractRecord( name, returnType ) ;
    }

    @Override
    @Deprecated
    public <T> boolean extractRecord(StoredProcedureResult resultMap, String name, T returnValue)
    {
        return resultMap.extractRecord( name, returnValue ) ;
    }

    @Override
    @Deprecated
    public <T> T extract(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        return resultMap.extract(name, returnType) ;
    }

    @Override
    @Deprecated
    public <T extends StructConvertable> T extractStruct(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        return resultMap.extractStruct(name, returnType) ;
    }

    @Override
    @Deprecated
    public <T> List<T> extractArray(StoredProcedureResult resultMap, String name, Class<T> returnType)
    {
        return resultMap.extractArray(name, returnType) ;
    }

    @Override
    @Deprecated
    public Boolean extractBoolean(StoredProcedureResult resultMap, String name)
    {
        return resultMap.extractBoolean( name ) ;
    }

    @Override
    @Deprecated
    public <T extends StructConvertable> T extractResultStruct(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return resultMap.extractResultStruct(returnType) ;
    }

    @Override
    public <T> T extractResultRecord(StoredProcedureResult resultMap, Class<T> returnType)
    {
        return resultMap.extractResultRecord(returnType) ;
    }

    public String getDatabaseSchema() 
    {
       return databaseSchema;
    }


    @Override
    public StoredProcedureResult executeIndex()
    {
       return execute( false ) ;
    }

    @Override
    public StoredProcedureResult executeName()
    {
        return execute( true ) ;
    }


    /**
     *
     * Spuštění PL/SQL kodu
     *
     * @param named     příznak, zda zasilat parametry s jménem (true) nebo podle pořadí (false)
     *
     * @return výsledk spuštění
     */
    private StoredProcedureResult execute( boolean named )
    {
        String originalSql = getSql();

        if ( ( wrapNeed ) || ( named ) || ( dbmsOutput ) )
        {
            generateWrappedQuery(named) ;
        }


        if ( ( entityManager != null ) && ( entityManager.getDelegate() != null ) )
        {
            ((Session)entityManager.getDelegate()).flush() ;
        }

        long startTime = System.currentTimeMillis();
        try
        {
            return new StoredProcedureResult( execute( inputs ), long2shortName ) ;
        }
        catch ( DataAccessException e )
        {
            if ( e.getCause() instanceof SQLException )
            {
                SQLException sqlException = (SQLException) e.getCause() ;

                if ( sqlException.getErrorCode() == 4068 ) //Balík byl změněn
                {
                    return new StoredProcedureResult( execute( inputs ), long2shortName ) ;
                }
            }

            throw e ;
        }
        finally {
            // analýza trvání volání PLSQL, určeno pro logování do samostatného souboru a export do excelu.
            if (PLSQL_LOGGER.isTraceEnabled()) {
                long duration = System.currentTimeMillis() - startTime;
                String sql = getSql();

                StringBuilder params = new StringBuilder();
                for (SqlParameter sqlParameter : getDeclaredParameters()) {
                    if (sqlParameter.isInputValueProvided()) {
                        params.append(sqlParameter.getName());
                        params.append('=');
                        if (inputs.containsKey(sqlParameter.getName())) {
                            params.append(inputs.get(sqlParameter.getName()));
                        }
                        params.append(';');
                    }
                }

                String callstack = ExceptionUtils.getFullStackTrace(new Throwable());

                PLSQL_LOGGER.trace("EXEC PLSQL;" + duration + ";\"" + originalSql + "\";\"" + sql + "\";\"" + params + "\";\"" + callstack + "\"");
            }
        }
    }


    @Override
    public void enableOutput(int size)
    {
        this.dbmsOutput = true ;
        this.sizeOutput = size ;
    }

    @Override
    public void disableOutput()
    {
        this.dbmsOutput = false ;
    }
}
