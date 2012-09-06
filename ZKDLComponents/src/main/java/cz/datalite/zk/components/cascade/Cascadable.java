package cz.datalite.zk.components.cascade;

/**
 *
 *
 * @param <T> 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface Cascadable<T> {

    /**
     * Adds parent to cascadable component (e.g. combobox). It is used in the component cascade where
     * parent is used like a filter for this value. If this componnent has defined
     * its parent, data model will be automatically updated when selected value
     * in the parent changed.
     * @param parent parent controller
     * @param column database column for the parent - it is used for the filter
     */
    void addParent( Cascadable parent, String column );

    /**
     * Returns actual selected entity
     * @return actual selected entity
     */
    T getSelectedItem();
}
