package cz.datalite.helpers.excel.export;

import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * <p>Objekt zastupující jednu buňku v listu. Udává, že se jedná
 * o hlavičkovou buňku. Tento objekt se využívá pro práci s
 * {@link cz.datalite.helpers.excel.export.ExcelExportUtils} 
 * využívající knihovnu jExcelApi<p>
 * @author Karel Cemus
 */
public class HeadCell implements Cell {

    /** <p>Souřadnice x - číslo sloupce.</p> */
    protected Integer x;
    /** <p>Souadnice y - číslo řádků</p> */
    protected Integer y;
    /** <p>Hodnota buňky</p> */
    protected Object value;
    /** <p>Formát buňky</p> */
    protected WritableCellFormat format;
    /** <p>Defaultní písmo použité pro hlavičkové buňky.</p> */
    protected final WritableFont CONST_DEFAULT_FONT = new WritableFont( WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD );

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na neznámé pozici,
     * nutno dokonfigurovat.</p>
     * @param value hodnota buňky
     */
    public HeadCell( final Object value ) {
        this.value = value;
    }

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na neznámé pozici,
     * nutno dokonfigurovat. Pro zobrazení se použije definovaný formát.</p>
     * @param value hodnota buňky
     * @param format formát buňky
     */
    public HeadCell( final Object value, final WritableCellFormat format ) {
        this.value = value;
        this.format = format;
    }

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na pozici x,y.</p>
     * @param value hodnota buňky
     * @param x souadnice x, číslo sloupce
     * @param y souadnice y, číslo řádku
     */
    public HeadCell( final Object value, final Integer x, final Integer y ) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na pozici x,y.
     * Pro zobrazení se použije definovaný formát.</p>
     * @param value hodnota buňky
     * @param x souadnice x, číslo sloupce
     * @param y souadnice y, číslo řádku
     * @param format formát buňky
     */
    public HeadCell( final Object value, final Integer x, final Integer y, final WritableCellFormat format ) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.format = format;
    }

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na pozici x,y.</p>
     * @param value hodnota buňky
     * @param x souadnice x, název sloupce
     * @param y souadnice y, číslo řádku
     */
    public HeadCell( final Object value, final String x, final Integer y ) {
        this.value = value;
        this.x = ExcelExportUtils.convertStringIndexToInt( x );
        this.y = y;
    }

    /**
     * <p>Vytvoří hlavičkovou buňku s danou hodnotou na pozici x,y.
     * Pro zobrazení se použije definovaný formát.</p>
     * @param value hodnota buňky
     * @param x souadnice x, název sloupce
     * @param y souadnice y, číslo řádku
     * @param format formát buňky
     */
    public HeadCell( final Object value, final String x, final Integer y, final WritableCellFormat format ) {
        this.value = value;
        this.x = ExcelExportUtils.convertStringIndexToInt( x );
        this.y = y;
        this.format = format;
    }

    public Integer getX() {
        if ( x == null ) {
            return 0;
        }
        return x;
    }

    /**
     * <p>Nastaví X souřadnici na danou hodnotu. Indexuje se od 0.</p>
     * @param x souadnice X, číslo sloupce od 0
     */
    public void setX( final Integer x ) {
        this.x = x;
    }

    public void setX( final String x ) {
        this.x = ExcelExportUtils.convertStringIndexToInt( x );
    }

    public Integer getY() {
        if ( y == null ) {
            return 0;
        }
        return y;
    }

    public void setY( final Integer y ) {
        this.y = y;
    }

    public Object getValue() {
        return value;
    }

    public void setValue( final Object value ) {
        this.value = value;
    }

    public WritableCellFormat getFormat() {
        return format;
    }

    public void setFormat( final WritableCellFormat format ) {
        this.format = format;
    }

    public WritableFont getFont() {
        return CONST_DEFAULT_FONT;
    }
}
