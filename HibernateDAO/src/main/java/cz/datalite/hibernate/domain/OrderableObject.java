package cz.datalite.hibernate.domain;

/**
 * Objekt, který lze třídit - definice pořadí
 */
public interface OrderableObject {
    /**
     * @return pořadí třídění
     */
    Long getOrd();

    /**
     * Nastavení třidicího pořadí
     *
     * @param ord     hodnota
     */
    void setOrd(Long ord);
}
