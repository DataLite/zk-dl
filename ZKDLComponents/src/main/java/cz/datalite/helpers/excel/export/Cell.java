package cz.datalite.helpers.excel.export;

import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * <p>Rozhraní udává, že se jedná o buňku zapsatelnou do sešitu v excelu.
 * Definuje možnosti jako font styl, pozice a hodnota.</p>
 * @author Karel Cemus
 * @deprecated since 1.4.5.2 use {@link cz.datalite.helpers.excel.export.poi.POICell}
 */
@Deprecated
public interface Cell {

    /**
     * <p>Vrací hodnotu, jež má být zapsána do buňky.</p>
     * <p>Přijímá datový typy String, Integer, Long, Date 
     * a Double.</p>
     * @return hodnota buňky
     */
    Object getValue();

    /**
     * <p>Nastavuje hodnotu jež se bude zapisovat do buňky</p>
     * @param value hodnota buňky
     */
    void setValue( Object value );

    /**
     * <p>Vrací X souřadnici buňky. Buňky se indexují od <b>0</b></p>
     * @return X souřadnice buňky, udává číslo sloupce od 0
     */
    Integer getX();

    /**
     * <p>Vrací Y souřadnici buňky. Buňky se indexují od <b>0</b></p>
     * @return Y souřadnice buňky, udává číslo řádku od 0
     */
    Integer getY();

    /**
     * <p>Nastavuje Y souřadnici buňky. Buňky se indexují od <b>0</b></p>
     * @param y číslo řádku od 0
     */
    void setY( Integer y );

    /**
     * <p>Vrací formát buňky, ve kterém lze definovat barvu, barvu
     * pozadí, ohraničení, datový typ atd. Pokud není definován, je
     * použit defaultní font a formát se automaticky generuje z 
     * datového typu v knihovní metodě
     * {@link cz.datalite.helpers.excel.export.ExcelExportUtils#createCell(Cell, CellFormats)}</p>
     * @return formát pro výpis buňky.
     */
    WritableCellFormat getFormat();

    /**
     * <p>Nastaví formát buňky, který definuje font, ohraničení,
     * barvu, datový typ.</p>
     * @param format formát buňky
     */
    void setFormat( WritableCellFormat format );

    /**
     * <p>Vrací font pro použití v buňce, využívá se pokud není definován
     * formát. Momentálně se nedá nastavit, vrací se defaultní nastavení.</p>
     * @return font pro použití v buňce
     */
    WritableFont getFont();
}
