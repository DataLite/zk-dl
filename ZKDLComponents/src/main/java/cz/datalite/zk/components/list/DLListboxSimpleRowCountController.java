package cz.datalite.zk.components.list;

/**
 * Simple implementation of the listbox controller. Adds rowcount capability to simple listbox
 * @param <T> 
 * @author Jiri Bubnik
 */
public abstract class DLListboxSimpleRowCountController<T> extends DLListboxSimpleController<T> implements RowCount {

    /**
     * Creates simple controller which can operate without database. This can
     * work with list.
     * @param identifier unique identifier to set data model to the session
     */
    public DLListboxSimpleRowCountController( final String identifier ) {
        this( identifier, null );
    }

    public DLListboxSimpleRowCountController( final String identifier, final Class<T> clazz ) {
        super( identifier, clazz );
    }

}
