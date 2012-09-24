package cz.datalite.helpers.excel.export;

import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * <p>Objekt zastupující jednu buňku v listu. Udává, že se jedná
 * o datovou buňku. Tento objekt se využívá pro práci s
 * {@link cz.datalite.helpers.excel.export.ExcelExportUtils} a
 * využívající knihovnu jExcelApi<p>
 * @author Karel Cemus
 */
public class DataCell implements Cell {

    /** <p>souřadnice X, číslo sloupce - volitelné</p> */
    protected Integer x;
    /** <p>souřadnice Y, číslo řádku</p> */
    protected Integer y;
    /** <p>hodnota buňky</p> */
    protected Object value;
    /** <p>hlavička, ke které se data vážnou - volitelná</p> */
    protected HeadCell head;
    /** <p>formát buňky pro vykreslení - volitelný</p> */
    protected WritableCellFormat format;
    /** <p>defaultní nastavení písma</p> */
    protected final WritableFont CONST_DEFAULT_FONT = new WritableFont( WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD );

    /**
     * <p>Vytvoří datovou buňku na souřadnicích head.x, y s hodnotou value. V tuto
     * chvíli ještě nemusí mít hlavička definovánu x souřadnici.</p>
     * @param y souřadnice Y
     * @param value hodnota
     * @param head hlavička
     */
    public DataCell( final Integer y, final Object value, final HeadCell head ) {
        this.value = value;
        this.head = head;
        this.y = y;
    }

    /**
     * <p>Vytvoří datovou buňku na souřadnicích head.x, y s hodnotou value. V tuto
     * chvíli ještě nemusí mít hlavička definovánu x souřadnici. Formát buňky definuje
     * datový typ a způsob vykreslení jako ohraničení, barvy a font.</p>
     * @param y souřadnice Y
     * @param value hodnota
     * @param head hlavička
     * @param format formát buňky
     */
    public DataCell( final Integer y, final Object value, final HeadCell head, final WritableCellFormat format ) {
        this.value = value;
        this.head = head;
        this.y = y;
        this.format = format;
    }

    /**
     * <p>Vytvoří datovou buňku na souřadnicích x, y s hodnotou value.</p>
     * @param x souadnice X
     * @param y souřadnice Y
     * @param value hodnota
     */
    public DataCell( final Integer x, final Integer y, final Object value ) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    /**
     * <p>Vytvoří datovou buňku na souřadnicích x, y s hodnotou value.  Formát
     * buňky definuje datový typ a způsob vykreslení jako ohraničení, barvy
     * a font.</p>
     * @param x souadnice X
     * @param y souřadnice Y
     * @param value hodnota
     * @param format formát buňky
     */
    public DataCell( final Integer x, final Integer y, final Object value, final WritableCellFormat format ) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.format = format;
    }

    /**
     * <p>Vytvoří datovou buňku na souřadnicích x, y s hodnotou value.</p>
     * @param x souadnice X - písmenný název sloupce
     * @param y souřadnice Y
     * @param value hodnota
     */
    public DataCell( final String x, final Integer y, final Object value ) {
        this.value = value;
        this.x = ExcelExportUtils.convertStringIndexToInt( x );
        this.y = y;
    }

    /**
     * <p>Vytvoří datovou buňku na souřadnicích x, y s hodnotou value.  Formát
     * buňky definuje datový typ a způsob vykreslení jako ohraničení, barvy
     * a font.</p>
     * @param x souadnice X - písmenný název sloupce
     * @param y souřadnice Y
     * @param value hodnota
     * @param format formát buňky
     */
    public DataCell( final String x, final Integer y, final Object value, final WritableCellFormat format ) {
        this.value = value;
        this.x = ExcelExportUtils.convertStringIndexToInt( x );
        this.y = y;
        this.format = format;
    }

    public Object getValue() {
        return value;
    }

    public void setValue( final Object value ) {
        this.value = value;
    }

    public Integer getX() {
        return x == null ? head.getX() : x;
    }

    public Integer getY() {
        return y;
    }

    public void setY( final Integer y ) {
        this.y = y;
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
