package cz.datalite.zk.components.grid;

import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;

/**
 * ZK Grid extensions.
 *
 * List of extensions:
 * <ul>
 *   <li>Get item index by item value</li>
 * </ul>
 *
 * @author Jiri Bubnik
 */
public class DLGrid extends Grid {

    /**
     * Get item index by item value
     *
     * @param element value
     * @return  item index
     **/
    public int getIndexByValue(Object element) {
        if (getModel() != null) {
            for (int i = 0; i < getModel().getSize(); i++) {
                if (getModel().getElementAt(i) == element) {
                    return i;
                }
            }
        } else {
            int i = 0;

            for (Object obj : getRows().getChildren()) {
                if (obj instanceof Row) {
                    Row item = (Row) obj;

                    if (item.getValue() == element) {
                        return i;
                    }
                    i++;
                }
            }
        }

        return -1;
    }
}
