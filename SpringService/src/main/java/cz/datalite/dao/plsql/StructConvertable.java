package cz.datalite.dao.plsql;

/**
 * Created with IntelliJ IDEA.
 * User: karny
 * Date: 4/17/13
 * Time: 5:11 PM
 *
 * Definice interface pro konverzi na STRUCT
 */
public interface StructConvertable
{
    /**
     * @return atributy objektu pro zaslání do DB
     */
    Object[] getStructureAttributes() ;

    /**
     *
     * Nastaveni vlastností objektu podle atributů z databaze
     *
     * @param attributes  atributy objektu
     */
    void setStructureAttributes(Object[] attributes) ;
}
