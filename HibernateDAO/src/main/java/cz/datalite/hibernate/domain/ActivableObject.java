package cz.datalite.hibernate.domain;

import java.util.Date;

/**
 * Objekt, kterému lze ukončit platnost.
 * Entita se nemaže z DB, ale platnostDo se jí nastaví na aktuální datum. Lze zase obnovit nastavením na null.
 */
public interface ActivableObject {
    /**
     * @return datum konce platnosti nebo null
     */
    Date getPlatnostDo();

    /**
     * @param platnostDo datum konce platnosti nebo null
     */
    void setPlatnostDo(Date platnostDo);

    /**
     * @return true pokud je objekt aktivní, false pokud je neaktivní
     */
    boolean isActive();
}
