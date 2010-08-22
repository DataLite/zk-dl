package cz.datalite.helpers.excel.parser;

/**
 * <p>Určuje, že se jedná o strukturu, do které budu naplněna data
 * při importu z excelu. Toto rozhraní umožňuje definovat rozšiřující
 * metody pro vložení rozšiřujících dat</p>
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface ExcelImportStructure {

    /**
     * <p>Vloží číslo řádku, ze kterého byla data načtena.</p>
     * @param row řádek, ze kterého jsou data načtena. Indexuje se od 0
     */
    void setSourceRowIndex( int row );

    /**
     * <p>Nastaví, zda-li je řádek validní nebo ne.</p>
     * @param valid validita řádku
     */
    void setValid( boolean valid );
}
