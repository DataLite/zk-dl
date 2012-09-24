package cz.datalite.zk.components.list.enums;

/**
 * There are defined keys which are used as a key in the maps when the filter model
 * is sended to the normal filter modal window.
 * @author Karel Cemus
 */
public enum DLNormalFilterKeys {

    /** There are defined column templates and configuration */
    TEMPLATES(),
    /** There is defined controller - this have not been used yet . */
    CONTROLLER(),
    /** There are model of active filters */
    FILTERS();

    private DLNormalFilterKeys() {
    }
}
