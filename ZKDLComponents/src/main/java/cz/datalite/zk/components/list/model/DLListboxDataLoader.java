package cz.datalite.zk.components.list.model;

import org.zkoss.lang.Library;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.ListboxDataLoader;

/**
 *
 * @author Jiri Bubnik
 */
public class DLListboxDataLoader extends ListboxDataLoader {

    private int preloadLimit = Library.getIntProperty("cz.datalite.zk.components.list.model.DLListboxDataLoader.preloadLimit", 100);

    @Override
    public int getLimit()
    {
        Listbox listbox = (Listbox) getOwner();
        return listbox.getRows() > 0 ? listbox.getRows() + 5 : preloadLimit;
    }


}
