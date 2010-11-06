package cz.datalite.zk.components.list.model;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.ListboxDataLoader;

/**
 *
 * @author Jiri Bubnik
 */
public class DLListboxDataLoader extends ListboxDataLoader {

    @Override
    public int getLimit()
    {
        Listbox listbox = (Listbox) getOwner();
        return listbox.getRows() > 0 ? listbox.getRows() + 5 : 100; //Change preload limit from 20 to 100
    }


}
