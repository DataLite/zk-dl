package cz.datalite.helpers.excel.parser;

import java.io.InputStream;
import java.util.Date;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.DateFormulaCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * <p>Třída pomáhá importovat soubory z MS Excel. Využívá k tomu
 * knihovnu jExcelApi. Jedná se o knihovní třídu pouze se statickými
 * metodami.</p>
 * @author Karel Cemus
 */
public final class ExcelImportUtils {

    /**
     * <p>Knihovní třída, má pouze statické metody.</p>
     */
    private ExcelImportUtils() {
    }

    /**
     * <p>Převede řetězec, který identifikuje sloupec v xls souboru na číselný
     * identifikátor začínající od 1.</p>
     * @param strIndex řetězec k převodu
     * @return převedený řetězec začínající od 1
     */
    private static int convertStringIndexToIntRecurs( final String strIndex ) {
        if ( strIndex.length() > 1 )
            return convertStringIndexToIntRecurs( strIndex.substring( 1 ) ) * 26 + convertStringIndexToIntRecurs( strIndex.substring( 0, 1 ) );
        else
            return (int) strIndex.charAt( 0 ) - 'A' + 1;
    }

    /**
     * <p>Převede řetězec, který identifikuje sloupec v xls souboru na číselný
     * identifikátor začínající od 0.</p>
     * @param strIndex řetězec k převodu
     * @return převedený řetězec začínající od 0
     */
    public static int convertStringIndexToInt( final String strIndex ) {
        return convertStringIndexToIntRecurs( strIndex ) - 1;
    }

    /**
     * <p>Převede číselný identifikátor začínající od 0 na písmenný identifikátor,
     * který se používá v xls souborech.</p>
     * @param index číselný identifikátor začínající od 0
     * @return výsledný řetězec
     */
    public static String convertIntIndexToString( final int index ) {
        return ( index / 26 > 0 ? String.valueOf( (char) ( index / 26 + 'A' - 1 ) ) : "" ) + (char) ( index % 26 + 'A' );
    }

    /**
     * <p>Vrací hodnotu buňky, ve které se očekává datum. V případě,
     * že je buňka prázdná vrací null</p>
     * @param cell buňka, ze které taháme hodnotu
     * @return načtené datum nebo null
     * @throws ExcelImportCellTypeException v případě, že datový typ buňky
     * není dle očekávání
     */
    public static Date getDate( final Cell cell ) throws ExcelImportCellTypeException {
        if ( CellType.EMPTY.equals( cell.getType() ) )
            return null;
        if ( CellType.DATE.equals( cell.getType() ) )
            return ( (DateCell) cell ).getDate();
        if ( CellType.DATE_FORMULA.equals( cell.getType() ) )
            return ( (DateFormulaCell) cell ).getDate();
        throw new ExcelImportCellTypeException( "Illegal cell type. Required type is \"Date\" but it is \"" + cell.getType() + "\" at " + cell.getColumn() + ", " + cell.getRow() + ". (Index from 0)" );
    }

    /**
     * <p>Vrací hodnotu buňky, ve které se očekává řetězec. V případě,
     * že je buňka prázdná vrací null</p>
     * @param cell buňka, ze které taháme hodnotu
     * @return načtený string nebo null
     * @throws ExcelImportCellTypeException v případě, že datový typ buňky
     * není dle očekávání
     */
    public static String getString( final Cell cell ) throws ExcelImportCellTypeException {
        if ( CellType.EMPTY.equals( cell.getType() ) )
            return null;
        if ( CellType.LABEL.equals( cell.getType() ) )
            return ( (LabelCell) cell ).getString();
        if ( CellType.NUMBER.equals( cell.getType() ) )
            return new Double(( (NumberCell) cell ).getValue()).toString();
        if ( CellType.STRING_FORMULA.equals( cell.getType() ) )
            return ( (StringFormulaCell) cell ).getString();
        if ( CellType.NUMBER_FORMULA.equals( cell.getType() ) )
            return new Double( ((NumberFormulaCell) cell ).getValue() ).toString();

        throw new ExcelImportCellTypeException( "Illegal cell type. Required type is \"Label\" but it is \"" + cell.getType() + "\" at " + cell.getColumn() + ", " + cell.getRow() + ". (Index from 0)" );
    }

    /**
     * <p>Vrací hodnotu buňky, ve které se očekává double. V případě,
     * že je buňka prázdná vrací null</p>
     * @param cell buňka, ze které taháme hodnotu
     * @return načtený double nebo null
     * @throws ExcelImportCellTypeException v případě, že datový typ buňky
     * není dle očekávání
     * @throws ExcelImportParseException
     */
    public static Double getDouble( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
        if ( CellType.EMPTY.equals( cell.getType() ) )
            return null;

        if ( CellType.LABEL.equals( cell.getType() ) )
        {
            try {
                return Double.parseDouble( ( (LabelCell) cell ).getString().replace(",", ".") );
            }
            catch ( NumberFormatException ex ) {
                throw new ExcelImportParseException( ex );
            }
        }

        if ( CellType.NUMBER_FORMULA.equals( cell.getType() ) )
            return ( (NumberFormulaCell) cell ).getValue();
        if ( CellType.NUMBER.equals( cell.getType() ) )
            return ( (NumberCell) cell ).getValue();
        throw new ExcelImportCellTypeException( "Illegal cell type. Required type is \"Number\" but it is \"" + cell.getType() + "\" at " + cell.getColumn() + ", " + cell.getRow() + ". (Index from 0)" );
    }

    /**
     * <p>Vrací hodnotu buňky, ve které se očekává int. V případě,
     * že je buňka prázdná vrací null</p>
     * @param cell buňka, ze které taháme hodnotu
     * @return načtený int nebo null
     * @throws ExcelImportCellTypeException v případě, že datový typ buňky
     * není dle očekávání
     * @throws ExcelImportParseException
     */
    public static Integer getInteger( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
        if ( CellType.EMPTY.equals( cell.getType() ) )
            return null;
        if ( CellType.LABEL.equals( cell.getType() ) )
            try {
                return Integer.parseInt( ( (LabelCell) cell ).getString() );
            }
            catch ( NumberFormatException ex ) {
                throw new ExcelImportParseException( ex );
            }
        if ( CellType.NUMBER_FORMULA.equals( cell.getType() ) )
            return (int) ( (NumberFormulaCell) cell ).getValue();
        if ( CellType.NUMBER.equals( cell.getType() ) )
            return (int) ( (NumberCell) cell ).getValue();
        if (CellType.STRING_FORMULA.equals(  cell.getType() ))
        {
            try {
                return Integer.parseInt( ((StringFormulaCell) cell).getContents() );
            }
            catch ( NumberFormatException ex ) {
                throw new ExcelImportParseException( ex );
            }
        }

        throw new ExcelImportCellTypeException( "Illegal cell type. Required type is \"Number\" but it is \"" + cell.getType() + "\" at " + cell.getColumn() + ", " + cell.getRow() + ". (Index from 0)" );
    }

    /**
     * <p>Vrací hodnotu buňky, ve které se očekává long. V případě,
     * že je buňka prázdná vrací null</p>
     * @param cell buňka, ze které taháme hodnotu
     * @return načtený long nebo null
     * @throws ExcelImportCellTypeException v případě, že datový typ buňky
     * není dle očekávání
     * @throws ExcelImportParseException
     */
    public static Long getLong( final Cell cell ) throws ExcelImportCellTypeException, ExcelImportParseException {
        if ( CellType.EMPTY.equals( cell.getType() ) )
            return null;
        if ( CellType.LABEL.equals( cell.getType() ) )
            try {
                return Long.parseLong( ( (LabelCell) cell ).getString() );
            }
            catch ( NumberFormatException ex ) {
                throw new ExcelImportParseException( ex );
            }
        if ( CellType.NUMBER_FORMULA.equals( cell.getType() ) )
            return (long) ( (NumberFormulaCell) cell ).getValue();
        if ( CellType.NUMBER.equals( cell.getType() ) )
            return (long) ( (NumberCell) cell ).getValue();
        throw new ExcelImportCellTypeException( "Illegal cell type. Required type is \"Number\" but it is \"" + cell.getType() + "\" at " + cell.getColumn() + ", " + cell.getRow() + ". (Index from 0)" );
    }

    /**
     * <p>Nahraje ze streamu sešit a převede případné vyjímky na
     * typ ExcelImportException.</p>
     * @param io proud, ze kterého načítáme sešit
     * @return sešit
     * @throws ExcelImportException vyjímka, pokud se nepodařilo stream načíst
     */
    public static Workbook loadWorkbook( final InputStream io ) throws ExcelImportException {
        return loadWorkbook(io, new WorkbookSettings());
    }
    
    /**
     * <p>Nahraje ze streamu sešit a převede případné vyjímky na
     * typ ExcelImportException.</p>
     * @param io proud, ze kterého načítáme sešit
     * @return sešit
     * @throws ExcelImportException vyjímka, pokud se nepodařilo stream načíst
     */
    public static Workbook loadWorkbook( final InputStream io, WorkbookSettings settings ) throws ExcelImportException {
        try {
            return Workbook.getWorkbook( io, settings );
        }
        catch ( Exception ex ) {
            throw new ExcelImportException( ex );
        }
    }
}
