package cz.datalite.zk.components.cascade;

import cz.datalite.helpers.ZKBinderHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Utility methods for cascading.
 * 
 * @param <T> 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class CascadeUtil<T> {

    /** list of the follower - they must be notified when value is changed */
    protected Set<CascadableExt> followers = new HashSet<CascadableExt>();
    /** list of the parents - there are defined pairs column - parent - it means which parent affects which value */
    protected Map<Cascadable, String> parentMap = new HashMap<Cascadable, String>();
    /** this */
    protected CascadableExt<T> thisCtl;

    public CascadeUtil( final CascadableExt<T> thisCtl ) {
        this.thisCtl = thisCtl;
    }

    /**
     * Call fireParentChanges on all followers
     * @throws NoSuchMethodException
     * @throws ModificationException
     */
    public void dofireParentChanges() {
        for ( CascadableExt follower : followers ) {
            follower.fireParentChanges( thisCtl );
        }
    }

    public void addParent( final Cascadable parent, final String column ) {
        final CascadableExt ctl = (CascadableExt) parent;
        ctl.addFollower( thisCtl );
        parentMap.put( ctl, column );
    }

    public void addFollower( final CascadableExt follower ) {
        followers.add( follower );
    }

    public String getParentColumn( final Cascadable parent ) {
        return parentMap.get( parent );
    }

    /**
     * Adds cascading combo constraints through parentComboId and parentComboColumn combobox property.
     *
     * Method checks if combobox getParentComboId() is not null and then tries to find associated component
     * (either directly or through binding sibling).<br/>
     * Parent must be of type DLCombobox.<br/>
     * Finally method calls addParent(combo.getController(), combo.getParentComboColumn()).
     */
    public void addDefaultParent( final CascadableComponent component ) {
        if ( component.getParentCascadeId() == null )
            return;

        Component parent = component.getFellowIfAny( component.getParentCascadeId() );
        if ( parent == null && ZKBinderHelper.getBinder( component ) != null )
            parent = ZKBinderHelper.getBindingSibling( component, component.getParentCascadeId() );

        if ( parent == null )
            throw new UiException( "parentComboId component not found for component: " + component );
        else if ( !( parent instanceof CascadableComponent ) )
            throw new UiException( "parentComboId must refer to component of type DLCombobox for component: " + component );


        // ensure controller is loaded
        if ( ZKBinderHelper.getBinder( parent ) != null )
            ZKBinderHelper.loadComponentAttribute( parent, "controller" );

        addParent( ( (CascadableComponent) parent ).getCascadableController(), component.getParentCascadeColumn() );
    }
}
