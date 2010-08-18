package cz.datalite.zk.components.cascade;

import org.zkoss.util.ModificationException;

/**
 *
 * @param <T> 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface CascadableExt<T> extends Cascadable<T> {

    /**
     * Notify from the component that parent was changed
     *
     * @param parent parent who changed.
     *
     * @throws NoSuchMethodException
     * @throws ModificationException
     */
    void fireParentChanges( final Cascadable parent );

    /**
     * Adds follower to the controller. It's required to notifiing children when
     * parent is changed.
     * @param follower follower in the combobox cascade
     */
    void addFollower( CascadableExt follower );
}
