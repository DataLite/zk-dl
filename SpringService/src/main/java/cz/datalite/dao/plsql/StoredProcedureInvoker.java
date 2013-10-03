package cz.datalite.dao.plsql;

import org.springframework.jdbc.core.SqlParameter;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Definice volání PL/SQL
 */
public interface StoredProcedureInvoker
{
    /**
     * Název proměnné pro uložení výsledku funkce
     */
	public static final String RETURN_VALUE_NAME = "RETURN_VALUE" ;

    /**
     * @param name     jmeno databazoveho objektu, který se má spouštět
     */
    void setName(String name) ;

    /**
     * Nastaveni datoveho zdroje
     */
    void setDataSource(DataSource dataSource) ;

    /**
     * @return seznam parametru dotazu
     */
    List<SqlParameter> getDeclaredParameters() ;

	/**
	 * Definice navratove hodnoty
	 *
	 * @param type						Typ hodnoty (@link  java.sql.Types)
	 *
	 * @return vytvoreny parametr dotazu
	 */
	SqlParameter declareReturnParameter(int type) ;

    /**
     * Definice výstupního parametru typu DB record (Objektový typ)
     *
     * @param typeName      jméno DB typu
     * @return vytvorený parametr
     */
    SqlParameter declareReturnStructParameter(String typeName) ;

    /**
     * Definice výstupního parametru typu DB pole (Objektový typ)
     *
     * @param typeName      jméno DB typu
     * @return vytvorený parametr
     */
    SqlParameter declareReturnArrayParameter(String typeName) ;

    /**
     * Definice výstupního parametru typu PL/SQL record
     *
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       cílový typ
     *
     * @return vytvorený parametr
     */
    <T> SqlParameter declareReturnRecordParameter(String dbType, Class<T> entityClass) ;

    /**
     * Definice navratove hodnoty typu Boolean
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareReturnBooleanParameter() ;

    /**
     * Definice výstupního parametru typu PL/SQL table of PL/SQL record
     *
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       cílový typ
     *
     * @return vytvorený parametr
     */
    <T> SqlParameter declareReturnTableParameter(String dbType, Class<T> entityClass) ;

	/**
	 * Definice vystupniho parametru
	 *
	 * @param name						Jmeno parametru
	 * @param type			   			Typ hodnoty     (@link  java.sql.Types)
	 *
	 * @return vytvoreny parametr dotazu
	 */
	SqlParameter declareOutParameter(String name, int type) ;

    /**
     * Definice vystupniho parametru typu struktura
     *
     * @param name				Jmeno parametru
     * @param dbType            Název objektového typu
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareOutStructParameter(String name, String dbType) ;

    /**
     * Definice vystupniho parametru typu pole
     *
     * @param name				Jmeno parametru
     * @param dbType            Název objektového typu
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareOutArrayParameter(String name, String dbType) ;

    /**
     * Definice vystupniho parametru typu PL/SQL record
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareOutRecordParameter(String name, String dbType, Class<T> entityClass) ;

    /**
     * Definice vystupniho parametru (typu boolean)
     *
     * @param name	Jmeno parametru
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareOutBooleanParameter(String name) ;

    /**
     * Definice vystupniho parametru typu PL/SQL table of PL/SQL record
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareOutTableParameter(String name, String dbType, Class<T> entityClass) ;


    /**
     * Deklarace a nastaveni  vstupně výstupního parametru
     *
     * @param name				Jmeno parametru
     * @param type				Typ hodnoty (@link  java.sql.Types)
     * @param value				Hodnota vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareInOutParameter(String name, int type, T value) ;


    /**
     * Deklarace a nastaveni  vstupně výstupního parametru (struktura)
     *
     * @param name				Jmeno parametru
     * @param type				Název DB typu
     * @param value				Hodnota vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    <T extends StructConvertable> SqlParameter declareInOutStructParameter(String name, String type, T value) ;

    /**
     * Deklarace a nastaveni  vstupně výstupního parametru (pole)
     *
     * @param name				Jmeno parametru
     * @param type				Název DB typu
     * @param value				Hodnota vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareInOutArrayParameter(String name, String type, List<T> value) ;


    /**
     * Definice vystupniho parametru typu PL/SQL record
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     * @param value             hodnota
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareInOutRecordParameter(String name, String dbType, Class<T> entityClass, T value) ;

    /**
     * Deklarace a nastaveni  vstupně výstupního parametru (s hodnotou null )
     *
     * @param name				Jmeno parametru
     * @param type				Typ hodnoty (@link  java.sql.Types)
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareInOutNullParameter(String name, int type) ;

    /**
     * Deklarace a nastaveni  vstupně výstupního parametru (pole)
     *
     * @param name				Jmeno parametru
     * @param value             hodnota
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter declareInOutBooleanParameter(String name, Boolean value) ;

    /**
     * Definice vystupniho parametru typu PL/SQL table of PL/SQL record
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     * @param value             hodnota
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter declareInOutTableParameter(String name, String dbType, Class<T> entityClass, List<T> value) ;

    /**
     *
     * Nastaveni  vstupniho parametru
     *
     * @param name				Jmeno parametru
     * @param type				Typ hodnoty (@link  java.sql.Types)
     * @param value				Hodnota vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter setParameter(String name, int type, T value) ;

	/**
	 * Nastaveni vstupniho parametru (struktura)
	 *
	 * @param name				Jmeno parametru
	 * @param typeName		    Jmeno databazoveho typ
	 * @param value				Hodnota vstupniho parametru
	 *
	 * @return vytvoreny parametr dotazu
	 */
	<E extends StructConvertable> SqlParameter setStructParameter(String name, String typeName, E value) ;

    /**
     *
     * Nastaveni  vstupniho parametru (pole)
     *
     * @param name				Jmeno parametru
     * @param typeName		    Jmeno databazoveho typ
     * @param value				Seznam hodnot vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter setArrayParameter(String name, String typeName, List<T> value) ;

    /**
     * Nastavení vstupniho parametru typu PL/SQL record
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     * @param value             hodnota
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter setRecordParameter(String name, String dbType, Class<T> entityClass, T value) ;


    /**
	 *
	 * Nastaveni  vstupniho parametru ( s hodnotou null )
	 *
	 * @param name				Jmeno parametru
	 * @param type				Typ hodnoty (@link  java.sql.Types)
	 *
	 * @return vytvoreny parametr dotazu
	 */
	SqlParameter setNullParameter(String name, int type) ;


    /**
     *
     * Nastaveni  vstupniho parametru
     *
     * @param name				Jmeno parametru
     * @param value				Hodnota vstupniho parametru
     *
     * @return vytvoreny parametr dotazu
     */
    SqlParameter setBooleanParameter(String name, Boolean value) ;

    /**
     * Nastavení vstupniho parametru typu PL/SQL table
     *
     * @param name				Jmeno parametru
     * @param dbType            Cílový typ databázový typ
     * @param entityClass       Cílový typ
     * @param value             hodnota
     *
     * @return vytvoreny parametr dotazu
     */
    <T> SqlParameter setTableParameter(String name, String dbType, Class<T> entityClass, List<T> value) ;


    /**
     * Funkce pro spuštění a ziskani vysledku dotazu (primitivní typu)
     *
     * @param returnType	Typ vysledku
     * @return vysledek dotazu
     */
    <T> T getResult(Class<T> returnType) ;

    /**
     * Funkce pro spuštění a ziskani vysledku dotazu (db struktura)
     *
     * @param returnType	Typ vysledku
     * @return vysledek dotazu
     */
    <T extends StructConvertable> T getResultStruct(Class<T> returnType) ;

	/**
	 * Funkce pro spustení a ziskani vysledku dotazu - seznam polozek (db pole)
	 *
	 * @param returnType	Typ polozky
	 *
	 * @return seznam polozek (vysledek dotazu)
	 */
	<T> List<T> getResultArray(Class<T> returnType) ;

    /**
     * Funkce pro spuštění a ziskani vysledku dotazu (pl/sql struktura)
     *
     * @param returnType	Typ vysledku
     * @return vysledek dotazu
     */
    <T> T getResultRecord(Class<T> returnType) ;

    /**
     * Funkce pro spuštění a ziskani vysledku dotazu (pl/sql struktura)
     *
     * @return vysledek dotazu
     */
    Boolean isResultBoolean() ;

    /**
     * Funkce pro spuštění a ziskani vysledku dotazu (jako pl/sql tabulka)
     *
     * @param returnType	Typ vysledku
     * @return vysledek dotazu
     */
    <T> List<T> getResultTable(Class<T> returnType) ;

	/**
	 * Funkce pro spuštění a získání mapy hodnot výstupních parametrů
	 *
	 * @return map hodnot výstupních parametrů
	 */
	Map<String, Object> execute() ;

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> T extract(Map<String, Object> resultMap, String name, Class<T> returnType) ;

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T extends StructConvertable> T extractStruct(Map<String, Object> resultMap, String name, Class<T> returnType) ;

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> List<T> extractArray(Map<String, Object> resultMap, String name, Class<T> returnType) ;

    /**
     * Vyzvednutí hodnot PL/SQL recordu
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> T extractRecord(Map<String, Object> resultMap, String name, Class<T> returnType) ;

    /**
     * Vyzvednutí hodnot PL/SQL recordu
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnValue         cílový objekt
     *
     * @return true pokud byla ve vysledku alespon jedna hodnota polozky ciloveho objektu
     */
    <T> boolean extractRecord(Map<String, Object> resultMap, String name, T returnValue) ;

    /**
     *
     * Vyzvednutí PL/SQL pole
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     *
     * @return seznam vyzvednutých záznamů
     */
    <T> List<T> extractTable(Map<String, Object> resultMap, String name, Class<T> returnType) ;

    /**
     *
     * Vyzvednutí PL/SQL pole
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     * @param returnType          cílový typ
     * @param target              cílový seznam
     * @param mergeType           způsob spojení seznamu z dba a cilového seznamu
     */
    <T> void extractTable(Map<String, Object> resultMap, String name, Class<T> returnType, List<T> target, MergeType mergeType) ;

    /**
     * Vyzvednutí hodnot parametru
     *
     * @param resultMap           seznam hodnot
     * @param name                jméno proměnné
     *
     * @return vyzvednuta hodnota
     */
    Boolean extractBoolean(Map<String, Object> resultMap, String name) ;

    /**
     * Vyzvednutí výsledku funkce
     *
     * @param resultMap           seznam hodnot
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> T extractResult(Map<String, Object> resultMap, Class<T> returnType) ;

    /**
     * Vyzvednutí výsledku funkce
     *
     * @param resultMap           seznam hodnot
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T extends StructConvertable> T extractResultStruct(Map<String, Object> resultMap, Class<T> returnType) ;

    /**
     * Vyzvednutí výsledku funkce (db pole)
     *
     * @param resultMap           seznam hodnot
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> List<T> extractResultArray(Map<String, Object> resultMap, Class<T> returnType) ;

    /**
     * Vyzvednutí výsledku funkce (PL/SQL table)
     *
     * @param resultMap           seznam hodnot
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> List<T> extractResultTable(Map<String, Object> resultMap, Class<T> returnType) ;

    /**
     * Vyzvednutí výsledku funkce (PL/SQL record)
     *
     * @param resultMap           seznam hodnot
     * @param returnType          cílový typ
     *
     * @return vyzvednuta hodnota
     */
    <T> T extractResultRecord(Map<String, Object> resultMap, Class<T> returnType) ;
}
