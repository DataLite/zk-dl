package cz.datalite.helpers.excel.parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.zkoss.util.media.Media;

/**
 *
 * @param <T>
 * @author Karel Cemus
 */
@SuppressWarnings( "unchecked" )
public class ExcelImporter<T extends ExcelImportStructure> {

    /** třída, která se bude plnit */
    protected Class<T> _clazz;
    /** objekt media, ze kterého se čte stream*/
    protected Media media;
    /** stream, ze kterého se čte sešit*/
    protected InputStream _is;
    /** sešit, ze kterého se čte list */
    protected Workbook _workbook;
    /** list, se kterým se prování operace */
    protected Sheet _sheet;
    /** index prvního řádku pro export. defaultně 1, očekává se hlavčka na 0 řádku. */
    protected int _firstRow = 1;
    /** index posledního řádku, který se ještě bude načítat */
    protected int _lastRow = Integer.MAX_VALUE;
    /** id řídícího sloupce, který musí být spojitý */
    protected int _mainColumn = -1;
    /** název řídícího sloupce, který musí být spojitý */
    protected String _mainColumnName;
    /** mapa, kde je definován název atributu třídy a id sloupce v souboru */
    protected Map<String, Integer> _mapIndex;
    /** mapa, kde je definován název atributu třídy a název sloupce v souboru */
    protected Map<String, String> _mapName;

    /**
     * <p>Vytvoří excelový importer, který načte data ze objektu typu media.
     * Jako parametry přijímá zdrojový objekt a třídu, která se má naplnit.
     * Bude se pracovat s prvním listem.</p>
     * @param media zdrojový objekt
     * @param clazz třída k naplnění
     * @throws ExcelImportException
     */
    public ExcelImporter( final Media media, final Class<T> clazz ) throws ExcelImportException {
        this.media = media;
        this._clazz = clazz;
        init();
    }

    /**
     * <p>Vytvoří excelový importer, který načte data ze objektu typu stream.
     * Jako parametry přijímá zdrojový objekt a třídu, která se má naplnit.
     * Bude se pracovat s prvním listem.</p>
     * @param is zdrojový proud
     * @param clazz třída k naplnění
     * @throws ExcelImportException
     */
    public ExcelImporter( final InputStream is, final Class<T> clazz ) throws ExcelImportException {
        this._is = is;
        this._clazz = clazz;
        init();
    }

    /**
     * <p>Vytvoří excelový importer, který načte data ze objektu typu sešit.
     * Jako parametry přijímá zdrojový objekt a třídu, která se má naplnit.
     * Bude se pracovat s prvním listem.</p>
     * @param workbook sešit, se kterým se má pracovat
     * @param clazz třída k naplnění
     * @throws ExcelImportException
     */
    public ExcelImporter( final Workbook workbook, final Class<T> clazz ) throws ExcelImportException {
        this._workbook = workbook;
        this._clazz = clazz;
        init();
    }

    /**
     * <p>Vytvoří excelový importer, který načte data ze objektu typu list.
     * Jako parametry přijímá zdrojový objekt a třídu, která se má naplnit.</p>
     * @param sheet zdrojový list
     * @param clazz třída k naplnění
     * @throws ExcelImportException
     */
    public ExcelImporter( final Sheet sheet, final Class<T> clazz ) throws ExcelImportException {
        this._sheet = sheet;
        this._clazz = clazz;
        init();
    }

    /**
     * <p>Provede dotažení potřebných zdrojů v závislosti na vloženém zdroji.</p>
     * @throws ExcelImportException
     */
    protected void init() throws ExcelImportException {
        try {
            if (_sheet == null)
            {
                if (_workbook == null)
                {
                    if (_is == null)
                    {
                        if (media == null)
                        {
                            throw new ExcelImportException( "Invalid inicialization, no input is specified.");
                        }
                        _is = media.getStreamData();
                    }
                    _workbook = Workbook.getWorkbook( _is );
                }
                _sheet = _workbook.getSheet(0);
            }
        }
        catch ( IOException ex ) {
            throw new ExcelImportException( "Workbook couldn't be loaded.", ex );
        }
        catch ( BiffException ex ) {
            throw new ExcelImportException( "Workbook couldn't be loaded.", ex );
        }
    }

    /**
     * <p>Nastaví id řádku, na kterém se bude začínat. Čísluje se od 0.</p>
     * @param firstRow id řádku od 0
     * @return instance importeru
     */
    public ExcelImporter<T> firstRow( final int firstRow ) {
        this._firstRow = firstRow;
        return this;
    }

    /**
     * <p>Nastaví id posledního řádku, do kterého (včetně) se exportuje.
     * v případě, že je id větší než počet řádek exportuje se do konce souboru,
     * nedojde k vygenerování výjimky.</p>
     * @param lastRow id posledního řádku, který se bude exportovat (včetně).
     * Indexuje se od 0
     * @return instance importeru
     */
    public ExcelImporter<T> lastRow( final int lastRow ) {
        this._lastRow = lastRow;
        return this;
    }

    /**
     * <p>Nastaví mapu, ve které se atributům ve struktuře přiřadí id
     * sloupce v souboru. Indexuje se od 0.</p>
     * @param map mapa s přiřazením názvů atributů k id sloupců
     * @return instance importeru
     */
    public ExcelImporter<T> map( final Map<String, Integer> map ) {
        this._mapIndex = map;
        return this;
    }

    /**
     * <p>Nastaví mapu, ve které se atributům ve struktuře přiřadí názvy
     * sloupců na prvním řádku v souboru.</p>
     * @param map mapa s přiřazením názvů atributů k názvům sloupců na 1. řádku
     * @return instance importeru
     */
    public ExcelImporter<T> mapColumnName( final Map<String, String> map ) {
        this._mapName = map;
        return this;
    }

    /**
     * <p>Nastaví mapu, ve které se atributům ve struktuře přiřadí písmenné
     * názvy sloupců v záhlaví v souboru.</p>
     * @param map mapa s přiřazením názvů atributů k písmenným názvům sloupců v záhlaví
     * @return instance importeru
     */
    public ExcelImporter<T> mapColumnNameIndex( final Map<String, String> map ) {
        _mapIndex = new HashMap<String, Integer>();
        for ( String key : map.keySet() ) {
            _mapIndex.put( key, Integer.valueOf( ExcelImportUtils.convertStringIndexToInt( map.get( key ) ) ) );
        }
        return this;
    }

    /**
     * <p>Nastaví řídící sloupec importu. To zapřičiní, že se budou načítat data
     * pouze do té doby, co jsou data v tomto sloupci. Ve chvíli kdy se zde objeví
     * prázdný řádek, přeruší se načítání.</p>
     * @param column id hlavního sloupce importu, musí mít spojitá data. Id od 0
     * @return instance importeru
     */
    public ExcelImporter<T> mainColumn( final int column ) {
        this._mainColumn = column;
        return this;
    }

    /**
     * <p>Nastaví řídící sloupec importu. To zapřičiní, že se budou načítat data
     * pouze do té doby, co jsou data v tomto sloupci. Ve chvíli kdy se zde objeví
     * prázdný řádek, přeruší se načítání.</p>
     * @param index písmenný název sloupce v záhlavní souboru, sloupce musí mít spojitá data
     * @return instance importeru
     */
    public ExcelImporter<T> mainColumnNameIndex( final String index ) {
        _mainColumn = ExcelImportUtils.convertStringIndexToInt( index );
        return this;
    }

    /**
     * <p>Provede import dat podle zadaných parametrů a vrátí list instancí
     * požadované struktury.</p>
     * @return načtená data
     * @throws ExcelImportException
     */
    public List<T> create() throws ExcelImportException {
        try {
            prepareMap();

            prepareMainColumn();

            _firstRow = Math.max( 0, _firstRow );
            _lastRow = Math.min( getColumnLastRow( _firstRow, _mainColumn ), _lastRow );

            final List<T> data = new ArrayList<T>( Math.max( 0, _lastRow - _firstRow + 1 ) );

            for ( int row = 0; row < _lastRow - _firstRow + 1; row++ ) {
                final T instance = _clazz.newInstance();
                instance.setSourceRowIndex( row );
                instance.setValid( true );
                data.add( instance );
            }

            // pro všechny požadované sloupce načteme
            for ( String fieldName : _mapIndex.keySet() ) {
                final Field field = getFieldByName( fieldName, _clazz ); // název pole ve struktuře
                final ImportDataTypeStrategy importer = getStrategy( field.getType() ); // strategie pro import datového typu
                final int column = _mapIndex.get( fieldName ); // sloupec

                if ( _sheet.getColumns() < column+1)
                {
                    throw new ExcelImportException("Soubor neobsahuje všechny požadované sloupce. Číslo hledaného sloupce: " + column + ", hledaný pod názvem: " + fieldName);
                }

                field.setAccessible( true );

                for ( int row = 0; row < _lastRow - _firstRow + 1; row++ ) {
                    final T rowData = data.get( row );
                    try {
                        field.set( rowData, importer.getValue( _sheet.getCell( column, row + _firstRow ) ) );
                    }
                    catch ( ExcelImportParseException ex ) {
                        rowData.setValid( false );
                    }
                }
                field.setAccessible( false );
            }

            close();

            return data;
        }
        catch ( NoSuchFieldException ex ) {
            throw new ExcelImportException( "Data structure is broken.", ex );
        }
        catch ( InstantiationException ex ) {
            throw new ExcelImportException( "Data structure couldn't be created.", ex );
        }
        catch ( IllegalAccessException ex ) {
            throw new ExcelImportException( "Data structure couldn't be created.", ex );
        }
    }

    /**
     * <p>Nastaví řídící sloupec importu. To zapřičiní, že se budou načítat data
     * pouze do té doby, co jsou data v tomto sloupci. Ve chvíli kdy se zde objeví
     * prázdný řádek, přeruší se načítání.</p>
     * @param name název sloupce v 1. řádku, musí mít spojitá data
     * @return instance importeru
     */
    public ExcelImporter<T> mainColumnName( final String name ) {
        this._mainColumnName = name;
        return this;
    }

    /**
     * <p>Najde atribut podle názvu</p>
     * @param fieldName název aributu
     * @param clazz třída, ve které hledáme
     * @return nalezený field
     * @throws NoSuchFieldException v případě nenalezení
     */
    protected static Field getFieldByName( final String fieldName, final Class clazz ) throws NoSuchFieldException {
        if ( fieldName == null || fieldName.length() == 0 ) {
            throw new NoSuchFieldException("Field name is empty map for import.");
        }
        for ( Field field : clazz.getDeclaredFields() ) {
            if ( field.getName().equals( fieldName ) ) {
                return field;
            }
        }
        throw new NoSuchFieldException( "Field \"" + fieldName + "\" wasn't found in dataStructure for import." );
    }

    /**
     * <p>Zvolí strategii načítání dat podle typu atributu.</p>
     * @param clazz typ atributu
     * @return zvolená strategie
     * @throws ExcelImportException nepodporovaný typ
     */
    protected ImportDataTypeStrategy getStrategy( final Class clazz ) throws ExcelImportException {
        if ( String.class.isAssignableFrom( clazz ) ) {
            return StringStrategy.INSTANCE;
        } else if ( Integer.class.isAssignableFrom( clazz ) ) {
            return IntegerStrategy.INSTANCE;
        } else if ( Date.class.isAssignableFrom( clazz ) ) {
            return DateStrategy.INSTANCE;
        } else if ( Long.class.isAssignableFrom( clazz ) ) {
            return LongStrategy.INSTANCE;
        } else if ( Double.class.isAssignableFrom( clazz ) ) {
            return DoubleStrategy.INSTANCE;
        } else {
            throw new ExcelImportException("Unsupported type \"" + clazz + "\"in structure for import");
        }
    }

    /**
     * <p>Připraví mapu indexů v případě, že není nastavená nebo je nastavená
     * mapa názvů.</p>
     */
    protected void prepareMap() {
        if ( _mapIndex != null ) {
            return;
        }

        if ( _mapName == null ) {
            _mapIndex = new HashMap<String, Integer>();
            return;
        }

        _mapIndex = new HashMap<String, Integer>();

        for ( String key : _mapName.keySet() ) {
            final int index = getIndexOfColumnName( _mapName.get( key ) );
            if ( index != -1 ) {
                _mapIndex.put(key, index);
            }
        }
    }

    /**
     * <p>Nastaví hlavní sloupce v případě, že je nastaven jeho název</p>
     */
    protected void prepareMainColumn() {
        if ( _mainColumn != -1 ) {
            return;
        }
        if ( _mainColumnName == null || _mainColumnName.length() == 0 ) {
            return;
        }
        _mainColumn = getIndexOfColumnName( _mainColumnName );
    }

    /**
     * <p>Vrací id sloupce s daným názvem.</p>
     * @param columnName název sloupce
     * @return jeho id. v případě nenalezení -1
     */
    protected int getIndexOfColumnName( final String columnName ) {
        for ( int column = 0; column < _sheet.getColumns(); column++ ) {
            if ( columnName.equals( _sheet.getCell( column, 0 ).getContents() ) ) {
                return column;
            }
        }
        return -1;
    }

    /**
     * <p>Vrací poslední řádek který se má načíst v závislosni na první řádku a řídícím
     * sloupci</p>
     * @param firstRow první řádek
     * @param col sloupce, který zajišťuje spojitost dat
     * @return nalezené id posledního vhodného řádku
     * @throws ExcelImportException
     */
    protected int getColumnLastRow( final int firstRow, final int col ) throws ExcelImportException {
        if ( col == -1 ) {
            return _sheet.getRows() - 1;
        }

        if ( col >= _sheet.getColumns() ) {
            throw new ExcelImportException("Main column is not found in excel file. Index is " +
                    col + " but file has got only " + _sheet.getColumns() + " columns");
        }

        for ( int row = firstRow; row < _sheet.getRows(); row++ ) {
            if ( _sheet.getCell( col, row ).getContents() == null || _sheet.getCell( col, row ).getContents().length() == 0 ) {
                return row - 1;
            }
        }
        return _sheet.getRows() - 1;
    }

    /**
     * Ukončí práci se sešitem
     */
    protected void close() {
        if ( _workbook != null ) {
            _workbook.close();
        }
    }

    /**
     * Strategie načítání dat. Volá správnou metodu pro konverzi datového typu
     */
    public interface ImportDataTypeStrategy {

        Object getValue( Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException;
    }

    public static class DateStrategy implements ImportDataTypeStrategy {

        public static final ImportDataTypeStrategy INSTANCE = new DateStrategy();

        public Object getValue( final Cell cell ) throws ExcelImportCellTypeException {
            return ExcelImportUtils.getDate( cell );
        }
    }

    public static class IntegerStrategy implements ImportDataTypeStrategy {

        public static final ImportDataTypeStrategy INSTANCE = new IntegerStrategy();

        public Object getValue( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
            return ExcelImportUtils.getInteger( cell );
        }
    }

    public static class LongStrategy implements ImportDataTypeStrategy {

        public static final ImportDataTypeStrategy INSTANCE = new LongStrategy();

        public Object getValue( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
            return ExcelImportUtils.getLong( cell );
        }
    }

    public static class DoubleStrategy implements ImportDataTypeStrategy {

        public static final ImportDataTypeStrategy INSTANCE = new DoubleStrategy();

        public Object getValue( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
            return ExcelImportUtils.getDouble( cell );
        }
    }

    public static class StringStrategy implements ImportDataTypeStrategy {

        public static final ImportDataTypeStrategy INSTANCE = new StringStrategy();

        public Object getValue( final Cell cell ) throws ExcelImportCellTypeException {
            return ExcelImportUtils.getString( cell );
        }
    }
}
