package cz.datalite.zk.components.combo;

import java.util.List;
import java.util.Map;

/**
 * Simple implementation of ComboboxController.<br/>
 * It demands only metod loadData() without any parameters to get list of data to show data in combo.
 * The only difference to direct model setup of combobox is that the list is loaded <b>after</b> user opens the combo.
 *
 * @author Jiri Bubnik
 */
public abstract class DLComboboxSimpleController<T> extends DLComboboxGeneralController<T>  {

    /**
     * Load data from the datastore.
     *
     * @return loaded data
     */
    protected abstract List<T> loadData( );

    @Override
    protected List<T> loadData(final String orderBy, final Map<String, Object> filters) {
        return loadData();
    }

}
