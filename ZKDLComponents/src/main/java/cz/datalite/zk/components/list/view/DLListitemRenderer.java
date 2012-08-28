package cz.datalite.zk.components.list.view;

import org.zkoss.bind.impl.BindListitemRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listitem;

/**
 *
 * @author Karel Cemus <cemus@datalite.cz>
 */
public class DLListitemRenderer extends BindListitemRenderer {

    @Override
    public void render( Listitem item, Object data, int index ) throws Exception {
        // the carry will be replaced by the new item
        // getting the previous sibling we have an invariant
        // to render method
        final Component previous = item.getPreviousSibling();

        // do render new item
        super.render( item, data, index );

        // get the rendered item
        final Listitem newItem = ( Listitem ) previous.getNextSibling();

        // if the parent is not DLListbox, renreder shouldn't be used.
        if ( newItem.getParent() instanceof DLListbox ) {
            final DLListbox listbox = ( DLListbox ) newItem.getParent();
            
            // update column order based on the model
            listbox.updateListItem(newItem);
        } else
            throw new IllegalStateException( "The 'DLListitemRenderer' is intended to be used with 'DLListbox' only. Please use pure 'BindListitemRenderer' instead." );


    }
}