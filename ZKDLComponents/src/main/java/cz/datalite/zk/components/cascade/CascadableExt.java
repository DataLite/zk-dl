package cz.datalite.zk.components.cascade;

/**
 *
 * @param <T> 
 * @author Karel Cemus
 */
public interface CascadableExt<T> extends Cascadable<T> {

    /**
     * Notify from the component that parent was changed
     *
     * @param parent parent who changed.
     *
     * @throws NoSuchMethodException
     * @throws java.util.ConcurrentModificationException
     */
    void fireParentChanges( final Cascadable parent );

    /**
     * Notify all followers;
     */
    void fireCascadeChanges();

    /**
     * Adds follower to the controller. It's required to notifiing children when
     * parent is changed.
     * @param follower follower in the combobox cascade
     */
    void addFollower( CascadableExt follower );
}
