package cz.datalite.zk.components.list.window.controller;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.DLFilter;
import cz.datalite.zk.components.list.DLListboxGeneralController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of the listbox controller on top of an enum values.
 *
 * @param <T> enum type
 * @author Jiri Bubnik
 */
public class DLListboxEnumController<T extends Enum> extends DLListboxGeneralController<T> {

    // Actual enum values
    List<T> enums = new ArrayList<T>();

    /**
     * Creates simple controller which can operate without database. This can
     * work with list.
     *
     * @param identifier unique identifier to set data model to the session
     */
    public DLListboxEnumController(final String identifier, Class<T> clazz) {
        super(identifier, clazz);

        for (Object enumValue : clazz.getEnumConstants()) {
            if (isEnumValueValid((T) enumValue))
                enums.add((T) enumValue);
        }
    }

    @Override
    protected DLResponse<T> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts) {
        return DLFilter.filterAndCount(filter, enums, firstRow, rowCount, sorts);
    }


    @Override
    protected DLResponse<String> loadDistinctColumnValues(final String column, final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<cz.datalite.dao.DLSort> sorts) {
        throw new UnsupportedOperationException("Distinct column values are not implemented.");
    }


    /**
     * Template method to allow subclasses to exclude some values.
     *
     * @param enumValue the value
     * @return true if is valid and should be in the listbox
     */
    protected boolean isEnumValueValid(T enumValue) {
        return true;
    }
}
