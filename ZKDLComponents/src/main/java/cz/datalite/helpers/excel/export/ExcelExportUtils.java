package cz.datalite.helpers.excel.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import jxl.CellType;
import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.zkoss.util.media.AMedia;

/**
 * <p>Metoda usnadňuje exportování dat do excelu, soubor XLS. Využívá ke své práci
 * knihovnu jExcelApi. Umožňuje uivateli využí zcela automatický exportSimple stejně jako
 * použít pouze pomocné metody a celkový postup si udělat dle sebe.</p>
 * @author Karel Čemus <cemus@datalite.cz>
 */
public final class ExcelExportUtils {

    /**
     * Nelze vytvořit instanci třídy
     */
    private ExcelExportUtils() {
    }

    private static final WritableCellFormat DATE_CELL_FORMAT = new WritableCellFormat( new DateFormat( "d.M.yyyy" ) );
    private static final WritableCellFormat DATETIME_CELL_FORMAT = new WritableCellFormat( new DateFormat( "d.M.yyyy HH:mm" ) );

    /**
     * <p>Převede řetězec, kterým se v Excelu identifikují sloupce na číslo.
     * Jedná se o rekurzivní metodu.</p>
     * @param strIndex identifikátor sloupce, písmena
     * @return výsledná hodnota
     */
    private static int convertStringIndexToIntRecurs( final String strIndex ) {
        if ( strIndex.length() > 1 ) {
            return convertStringIndexToInt( strIndex.substring( 1 ) ) + convertStringIndexToInt( strIndex.substring( 0, 0 ) );
        } else {
            return ( int ) strIndex.charAt( 0 ) - 'A' + 1;
        }
    }

    /**
     * <p>Převede řetězec, kterým se v Excelu identifikují sloupce na číslo.</p>
     * @param strIndex identifikátor sloupce, písmena
     * @return výsledná hodnota
     */
    public static int convertStringIndexToInt( final String strIndex ) {
        return convertStringIndexToIntRecurs( strIndex ) - 1;
    }

    /**
     * <p>Převede číslo na řetězec, který odpovídá identifikaci v excelu</p>
     * @param index index sloupce
     * @return stringový identifikátor
     */
    public static String convertIntIndexToString( final int index ) {
        return (index / 26 > 0 ? String.valueOf( ( char ) (index / 26 + 'A' - 1) ) : "") + ( char ) (index % 26 + 'A');
    }

    /**
     * <p>Nastaví buňce zaslanou hodnotu string.</p>
     * @param ws WritableSheet
     * @param cell Bunka Excelu
     * @param text the actulal text value
     * @throws WriteException
     */
    public static void setTextCell( WritableSheet ws, String cell, String text ) throws WriteException {

        Label label = (( Label ) ws.getWritableCell( getColumnFromCell( cell ), getRowFromCell( cell ) ));

        if ( text == null ) {
            Label ec = new Label( getColumnFromCell( cell ), getRowFromCell( cell ), "" );
            ec.setCellFormat( label.getCellFormat() );
            ws.addCell( ec );
        } else {
            label.setString( text );
        }
    }

    /**
     * <p>Nastaví buňce zaslanou hodnotu number.</p>
     * @param ws WritableSheet
     * @param cell Bunka Excelu
     * @param cislo number
     * @throws WriteException
     */
    public static void setNumberCell( WritableSheet ws, String cell, Long cislo ) throws WriteException {

        jxl.write.Number number = (( jxl.write.Number ) ws.getWritableCell( getColumnFromCell( cell ), getRowFromCell( cell ) ));

        if ( cislo == null ) {
            Label ec = new Label( getColumnFromCell( cell ), getRowFromCell( cell ), "" );
            ec.setCellFormat( number.getCellFormat() );
            ws.addCell( ec );
        } else {
            number.setValue( cislo );
        }
    }

    /**
     * <p>Nastaví buňce zaslanou hodnotu date.</p>
     * @param ws WritableSheet
     * @param cell Bunka Excelu
     * @param date Datum
     * @throws WriteException
     */
    public static void setDateCell( WritableSheet ws, String cell, Date date ) throws WriteException {

        DateTime dateTime = (( DateTime ) ws.getWritableCell( getColumnFromCell( cell ), getRowFromCell( cell ) ));

        if ( date == null ) {
            Label ec = new Label( getColumnFromCell( cell ), getRowFromCell( cell ), "" );
            ec.setCellFormat( dateTime.getCellFormat() );
            ws.addCell( ec );
        } else {
            dateTime.setDate( date );
        }
    }

    /**
     * <p>Vrátí číslo sloupce (o 1 menší, protože Excel počítá od 0).</p>
     * @param cell bunka Excellu
     * @return číslo sloupce
     */
    public static int getColumnFromCell( String cell ) {
        return convertStringIndexToInt( cell.replaceAll( "[\\d]", "" ) );
    }

    /**
     * <p>Vrátí číslo řádku (o 1 menší, protože Excel počítá od 0).</p>
     * @param cell bunka Excellu
     * @return číslo řádku
     */
    public static int getRowFromCell( String cell ) {
        return Integer.parseInt( cell.replaceAll( "[A-Z]", "" ) ) - 1;
    }

    /**
     * <p>Nastavuje šířku buněk v závislosti na obsahu. Dochází k výpočtu velikosti
     * obsahu a velikosti fontu + se započítává koeficient pro přebytečné místo.
     * Pokud jsou ve sloupci nějaké buňky určené k merge, může autofit končit
     * s nevhodnou velikostí, tedy <b>pro sloupce s řídící buňkou u merge se
     * NEDOPORUČUJE tuto metodu používat</b>, pokud obsahuje více dat, než je
     * očekávaná šířka sloupce.</p>
     * <p><b><i>Poznámka:</i></b></p>
     * <p>V případě, že je ve sloupci buňka pro merge, lze zavolat tuto metodu dříve,
     * než je daná buňka zapsána do sešitu. Pak se šířka sloupce nastaví správně.</p>
     * @param column číslo sloupce (od 0)
     * @param sheet list v sešitu, ve kterém jsou již data vygenerována
     */
    public static void autofitColumnWidth( final int column, final WritableSheet sheet ) {
        int width = 0;
        for ( jxl.Cell cell : sheet.getColumn( column ) ) {

            if ( cell.getContents().length() > 0 ) {
                int len = 0;
                if ( CellType.DATE.equals( cell.getType() ) ) {
                    len = 10;
                } else {
                    len = cell.getContents().length();
                }

                len *= getWidthCoeficient( cell );
                if ( width < len ) {
                    width = len;
                }
            }
        }
        setWidth( width, column, sheet );
    }

    /**
     * <p>Vypočítá koeficient šířky pro danou buňku. V úvahu se bere velikost fontu
     * + konstanta pro koeficient volného místa. Pro získání vhodné šířky je nutné
     * vynásobit koeficient stringovou délkou hodnoty.</p>
     * @param cell buňka, pro kterou se koeficient počítá
     * @return vypočítaný koeficient šířky sloupce
     */
    public static double getWidthCoeficient( final jxl.Cell cell ) {
        return cell.getCellFormat().getFont().getPointSize() / 10.0 * 1.2;
    }

    /**
     * <p>Nastaví sloupec v sešitu na zadanou šířku.</p>
     * @param width šířka sloupce
     * @param column index sloupce (od 0)
     * @param sheet sešit, ve kterém nastavení provádíme.
     */
    public static void setWidth( final int width, final int column, final WritableSheet sheet ) {
        sheet.setColumnView( column, width );
    }

    /**
     * <p>Vytvoří soubor, do kterého lze vkládat listy a pak jej lze exportovat
     * jako dokumet pro MS Excel.</p>
     * @param os stream, do kterého chceme data zapisovat
     * @return vytvoření workbook
     * @throws IOException
     */
    public static WritableWorkbook createWorkbook( final OutputStream os ) throws IOException {
        return Workbook.createWorkbook( os );
    }

    /**
     * <p>Vrací stream vhodný pro exportSimple do Excelu. Ne všechny streamy jsou
     * podporované.</p>
     * @return vhodný stream pro exportSimple do Excelu.
     */
    public static ByteArrayOutputStream createStream() {
        return new ByteArrayOutputStream();
    }

    /**
     * <p>Vloží nový list do daného workbooku. Vloží jej na začátek.</p>
     * @param workbook workbook, kam chceme list vložit
     * @param sheetName název listu
     * @return nový list
     */
    public static WritableSheet insertSheet( final WritableWorkbook workbook, final String sheetName ) {
        return insertSheet( workbook, sheetName, 0 );
    }

    /**
     * <p>Vloží nový list do daného workbooku. Vloží jej před list na daném indexu.
     * Pro vložení na začátek je index 0.</p>
     * @param workbook workbook, kam chceme list vložit
     * @param sheetName název listu
     * @param index index, kam chceme list vložit
     * @return nový list
     * @see jxl.write.WritableWorkbook#createSheet(java.lang.String, int) 
     */
    public static WritableSheet insertSheet( final WritableWorkbook workbook, final String sheetName, final int index ) {
        return workbook.createSheet( sheetName, index );
    }

    /**
     * <p>Uzavře sešit a vráti AMedia objekt</p>
     * @param workbook sešit, který chceme uzavřít
     * @param os stream, do kterého se zapisovala data
     * @param exportName název souboru pro exportSimple
     * @return vygenerovaný AMedia připravený ke stažení
     * @throws IOException
     * @throws WriteException 
     */
    public static AMedia getAMediaOutput( final WritableWorkbook workbook, final ByteArrayOutputStream os, final String exportName ) throws IOException, WriteException {
        if ( os == null ) {
            return null;
        }

        workbook.write();
        workbook.close();
        final InputStream is = new ByteArrayInputStream( os.toByteArray() );
        return new AMedia( exportName + ".xls", "xls", "application/vnd.ms-excel", is );
    }

    /**
     * <p>Vytvoří buňku vhodnou pro zápis do Excelu. Pokud není definován formát,
     * použije defaultní font a formát buňky určí z datového typu záznamu.
     * Podle datovýho typu dat nastaví její formát.</p>
     * @param cell buňka ke konverzi pro zápis
     * @return připravená buňka
     * @throws jxl.write.WriteException
     */
    public static WritableCell createCell( final Cell cell ) throws WriteException {
        Object value = cell.getValue();
        WritableCellFormat format = cell.getFormat();

        if ( value == null ) { // no information about format
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont() );
                cell.setFormat( format );
            }
            return new jxl.write.Blank( cell.getX(), cell.getY(), format );
        } else if ( value instanceof Integer ) {
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont(), NumberFormats.INTEGER );
                cell.setFormat( format );
            }
            return new jxl.write.Number( cell.getX(), cell.getY(), ( Integer ) value, format );
        } else if ( value instanceof Long ) {
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont(), NumberFormats.INTEGER );
                cell.setFormat( format );
            }
            return new jxl.write.Number( cell.getX(), cell.getY(), (( Long ) value), format );
        } else if ( value instanceof BigDecimal ) {  // BigDecimal type
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont(), NumberFormats.FLOAT );
                cell.setFormat( format );
            }
            return new jxl.write.Number( cell.getX(), cell.getY(), (( BigDecimal ) value).doubleValue(), format );
        } else if ( value instanceof Double ) {  // double type
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont(), NumberFormats.FLOAT );
                cell.setFormat( format );
            }
            return new jxl.write.Number( cell.getX(), cell.getY(), ( Double ) value, format );
        } else if ( value instanceof Date ) {  // Date type
            if ( format == null ) {
                Calendar dateValue = Calendar.getInstance();
                dateValue.setTime((Date) value);

                // if the time contains hours and minutes, set date time format
                if (dateValue.get(Calendar.HOUR) != 0 || dateValue.get(Calendar.MINUTE) != 0)
                   format = DATETIME_CELL_FORMAT;
                else
                    format = DATE_CELL_FORMAT;

                cell.setFormat( format );
            }
            return new DateTime( cell.getX(), cell.getY(), ( Date ) value, format );
        } else if ( value instanceof Enum ) {
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont() );
                cell.setFormat( format );
            }
            return new Label( cell.getX(), cell.getY(), value.toString(), format );
        } else { // string or other object type
            if ( format == null ) {
                format = new WritableCellFormat( cell.getFont() );
                cell.setFormat( format );
            }
            return new Label( cell.getX(), cell.getY(), String.valueOf( value ), cell.getFormat() );
        }
    }

    /**
     * <p>Metoda usnadňuje exportování dat do excelu, soubor XLS. 
     * Jako výsledek vrací hotový {@link org.zkoss.util.media.AMedia},
     * který lze například zpřístupnit uživateli přes
     * {@link org.zkoss.zul.Filedownload}.</p>
     * @param reportName název souboru který se generuje
     * @param sheetName název listu
     * @param dataSource datasource s datovými a hlavičkovými buňkami
     * @return hotový AMedia připravený ke stažení
     * @throws IOException 
     */
    public static AMedia exportSimple( final String reportName, final String sheetName, final DataSource dataSource ) throws IOException {

        try {
            final ByteArrayOutputStream os = createStream();
            final WritableWorkbook workbook = createWorkbook( os );
            final WritableSheet sheet = insertSheet( workbook, sheetName );

            for ( Cell cell : dataSource.getCells() ) { // zapsání buněk
                sheet.addCell( createCell( cell ) );
            }

            // nastavení autofit
            for ( int i = 0; i < dataSource.getCellCount(); i++ ) {
                autofitColumnWidth( i, sheet );
            }

            // ukončení práce se sešitem
            return getAMediaOutput( workbook, os, reportName );

        } catch ( WriteException ex ) {
            throw new IOException( "Chyba přístupu k souboru: " + ex.getMessage() );
        }
    }
}
