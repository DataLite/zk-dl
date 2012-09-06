package cz.datalite.zk.components.list.window.controller;

import cz.datalite.zk.bind.ZKBinderHelper;
import cz.datalite.zk.components.list.view.DLListbox;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.zkoss.zul.Listitem;

/**
 * Controller for component consists from two listboxes which can move items
 * between each other.
 * @author Karel Čemus <cemus@datalite.cz>
 */
@SuppressWarnings( "unchecked" )
public class ListboxSelectorController {

    // model
    protected List<Map<String, Object>> usedModel;
    protected List<Map<String, Object>> unusedModel;

    // view
    protected DLListbox usedListbox;
    protected DLListbox unusedListbox;


    protected static final String CONST_USED_LISTBOX = "usedListbox";
    protected static final String CONST_UNUSED_LISTBOX = "unusedListbox";
    protected static final String CONST_USED_MODEL = "usedModel";
    protected static final String CONST_UNUSED_MODEL = "unusedModel";
    protected static final String CONST_SOURCE_MODEL = "sourceModel";

    public ListboxSelectorController( final List<Map<String, Object>> usedModel, final List<Map<String, Object>> unusedModel, final DLListbox usedListbox, final DLListbox unusedListbox ) {
        this.usedModel = usedModel;
        this.unusedModel = unusedModel;
        this.usedListbox = usedListbox;
        this.unusedListbox = unusedListbox;

        for ( Map<String, Object> map : unusedModel ) {
            map.put( CONST_SOURCE_MODEL, CONST_UNUSED_MODEL );
        }

        for ( Map<String, Object> map : usedModel ) {
            map.put( CONST_SOURCE_MODEL, CONST_USED_MODEL );
        }
    }

    public List<Map<String, Object>> getUnusedModel() {
        return unusedModel;
    }

    public List<Map<String, Object>> getUsedModel() {
        return usedModel;
    }

    public void moveItem( final Listitem dragged, final Listitem dropped)
    {
        final boolean isUsedSource = isUsedSource( dragged );
        final boolean isUsedTarget = CONST_USED_LISTBOX.equals( dropped.getListbox().getId() );

        // remove from source model
        removeFromSource( dragged, isUsedSource ? usedModel : unusedModel );

        // index of dropped item
        final int dropIndex = dropped.getIndex();

        ( isUsedTarget ? usedModel : unusedModel ).add( dropIndex, (Map<String, Object>) dragged.getValue() );

        // update source model note
        ( (Map<String, Object>) dragged.getValue() ).put( CONST_SOURCE_MODEL, isUsedTarget ? CONST_USED_MODEL : CONST_UNUSED_MODEL );

        // refresh
        ZKBinderHelper.loadComponent( usedListbox.getFellow( isUsedTarget ? CONST_USED_LISTBOX : CONST_UNUSED_LISTBOX ) );
    }

    public void moveItem( final Listitem dragged, final DLListbox dropped )
    {
        final boolean isUsedSource = isUsedSource( dragged );
        final boolean isUsedTarget = CONST_USED_LISTBOX.equals( dropped.getId() );

        // remove from source model
        removeFromSource( dragged, isUsedSource ? usedModel : unusedModel );

        // add to the new listbox
        ( isUsedTarget ? usedModel : unusedModel ).add( (Map<String, Object>) dragged.getValue() );

        // update note about source model
        ( (Map<String, Object>) dragged.getValue() ).put( CONST_SOURCE_MODEL, isUsedTarget ? CONST_USED_MODEL : CONST_UNUSED_MODEL );

        // refresh
        ZKBinderHelper.loadComponent( usedListbox.getFellow( isUsedTarget ? CONST_USED_LISTBOX : CONST_UNUSED_LISTBOX ) );
    }

    protected Object getAttribute( final Listitem item, final String attribute ) {
        return ( (Map<String, Object>) item.getValue() ).get( attribute );
    }

    protected void removeFromSource( final Listitem item, final List<Map<String, Object>> source ) {
        // remove from source model
        source.remove( (Map<String, Object>) item.getValue() );
        // remove from source listbox
        item.getListbox().removeChild( item );
    }

    protected boolean isUsedSource( final Listitem item ) {
        return CONST_USED_MODEL.equals( getAttribute( item, CONST_SOURCE_MODEL ).toString() );
    }

    /**
     * All selected items in used to unused.
     */
    public void onUsedToUnusedMove()
    {
        final int selected = unusedListbox.getSelectedIndex();

        if ( usedListbox.getSelectedItems() != null )
        {
            Set<Listitem> copy = new HashSet<Listitem>(usedListbox.getSelectedItems());

            for(Listitem item : copy)
            {
                if ( unusedListbox.getSelectedItem() == null )
                {
                    moveItem( item, unusedListbox );
                }
                else
                {
                    moveItem( item, unusedListbox.getSelectedItem() );
                    unusedListbox.setSelectedIndex(selected);
                }
            }
        }
    }

    public void onUsedToUnusedAllMove() {
        for ( Map<String, Object> map : usedModel ) {
            map.put( CONST_SOURCE_MODEL, CONST_UNUSED_MODEL );
            unusedModel.add( map );
        }
        usedModel.clear();

        ZKBinderHelper.loadComponent( unusedListbox );
        ZKBinderHelper.loadComponent( usedListbox );
    }

    /**
     * All selected items in Unused to used.
     */
    public void onUnusedToUsedMove()
    {
        final int selected = usedListbox.getSelectedIndex();

        if ( unusedListbox.getSelectedItems() != null )
        {
            Set<Listitem> copy = new HashSet<Listitem>(unusedListbox.getSelectedItems());

            for(Listitem item : copy)
            {
                if ( usedListbox.getSelectedItem() == null )
                {
                    moveItem( item, usedListbox );
                }
                else
                {
                    moveItem( item, usedListbox.getSelectedItem() );
                    usedListbox.setSelectedIndex(selected);
                }
            }
        }
    }

    public void onUnusedToUsedAllMove() {
        for ( Map<String, Object> map : unusedModel ) {
            map.put( CONST_SOURCE_MODEL, CONST_USED_MODEL );
            usedModel.add( map );
        }
        unusedModel.clear();
        ZKBinderHelper.loadComponent( unusedListbox );
        ZKBinderHelper.loadComponent( usedListbox );
    }

    public void onTopMove() {
        if ( !usedModel.isEmpty() && usedListbox.getSelectedItem() != null && usedListbox.getSelectedIndex() != 0 )
            moveItem( usedListbox.getSelectedItem(), usedListbox.getItemAtIndex( 0 ) );

        usedListbox.setSelectedIndex( 0 );
    }

    public void onUpMove() {
        final int itemIndex = usedListbox.getSelectedIndex() - 1;
        if ( !usedModel.isEmpty() && usedListbox.getSelectedItem() != null && itemIndex >= 0 ) {
            moveItem( usedListbox.getSelectedItem(), usedListbox.getItemAtIndex( itemIndex ) );
            usedListbox.setSelectedIndex( itemIndex );
        }
    }

    public void onDownMove() {
        final int itemIndex = usedListbox.getSelectedIndex() + 1;
        if ( !usedModel.isEmpty() && usedListbox.getSelectedItem() != null && itemIndex != usedListbox.getItemCount() ) {
            if ( ( usedListbox.getSelectedIndex() + 2 ) < usedListbox.getItemCount() )
                moveItem( usedListbox.getSelectedItem(), usedListbox.getItemAtIndex( itemIndex + 1 ) );
            else moveItem( usedListbox.getSelectedItem(), usedListbox );
            usedListbox.setSelectedIndex( itemIndex );
        }
    }

    public void onBottomMove() {
        if ( !usedModel.isEmpty() && usedListbox.getSelectedItem() != null && ( usedListbox.getSelectedIndex() + 1 ) != usedListbox.getItemCount() )
            moveItem( usedListbox.getSelectedItem(), usedListbox );

        usedListbox.setSelectedIndex( usedListbox.getItemCount() - 1 );
    }
}
