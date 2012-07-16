package cz.datalite.zk.databinder;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Convert selected items to Set of beans and vice versa.
 */
public class SelectedItemsConverter implements TypeConverter, java.io.Serializable {
    private static final long serialVersionUID = 200808191439L;

    public Object coerceToUi(Object val, Component comp) { //load
        Listbox lbx = (Listbox) comp;
        final ListModel xmodel = lbx.getModel();
        Set selectedModels = (Set) val;
        Set<Listitem> selectedItems = new HashSet<Listitem>();

        if (selectedModels == null)
            return selectedItems;

        for (Object selecteModel : selectedModels) {
            if (xmodel instanceof BindingListModel) {
                final BindingListModel model = (BindingListModel) xmodel;
                int index = model.indexOf(val);
                if (index >= 0) {
                    selectedItems.add(lbx.getItemAtIndex(index));
                }
            } else if (xmodel == null) { //no model case, assume Listitem.value to be used with selectedItem
                //iterate to find the selected item assume the value (select mold)
                for (final Iterator it = lbx.getItems().iterator(); it.hasNext(); ) {
                    final Listitem li = (Listitem) it.next();
                    if (val.equals(li.getValue())) {
                        selectedItems.add(li);
                    }
                }
            } else {
                throw new UiException("model of the databind listbox " + lbx + " must be an instanceof of org.zkoss.zkplus.databind.BindingListModel." + xmodel);
            }
        }
        return selectedItems;
    }

    public Object coerceToBean(Object val, Component comp) { //save
        final Listbox lbx = (Listbox) comp;
        final Set<Listitem> listitems = (Set<Listitem>) val;

        if (Executions.getCurrent().getAttribute("zkoss.zkplus.databind.ON_SELECT" + lbx.getUuid()) != null) {
            //bug #2140491
            //triggered by coerceToUi(), ignore this
            Executions.getCurrent().removeAttribute("zkoss.zkplus.databind.ON_SELECT" + lbx.getUuid());
            return TypeConverter.IGNORE;
        }

        final ListModel model = lbx.getModel();
        Set listmodels = new HashSet();
        for (Listitem listitem : listitems) {
            listmodels.add(model != null ? model.getElementAt(listitem.getIndex()) : listitem.getValue());
        }
        return listmodels;
    }
}
