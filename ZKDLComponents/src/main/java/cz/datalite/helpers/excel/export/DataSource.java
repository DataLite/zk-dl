package cz.datalite.helpers.excel.export;

import java.util.List;

/**
 * <p>Abstraktní třída pro definoci datového zdroje pro list. Definuje 
 * seznam buňek, které mají být zapsány.</p>
 *
 * @author Karel Cemus
 */
public abstract class DataSource {

    /**
     * <p>Vrací seznam všech položek, které mají být zapsány do sešitu.
     * Datové buňku musí být řádně provázány na hlavičkové buňky nebo
     * musí obsahovat index sloupce. Hlavičkové buňky musí obsahovat
     * souřadnice, na které mají být zapsány. Volitelně mohou buňky
     * obsahovat formát</p>
     * @return seznam buňek k zapsání na list
     */
    abstract public List<Cell> getCells();

    /**
     * <p>Definuje počet sloupců použitých v sešitu. Pokud není
     * tato metoda překryta dochází k automatickému výpočtu, který
     * je časově náročný, proto se doporučuje metodu překrýt a vracet
     * ideálně konstantu.</p>
     * @return počet sloupců
     */
    public int getCellCount() {
        int maxCellIndex = -1; // nejvyšší index buňky

        for ( Cell cell : getCells() ) {
            if ( cell.getX() > maxCellIndex ) maxCellIndex = cell.getX();
        }

        return maxCellIndex + 1;
    }
}
