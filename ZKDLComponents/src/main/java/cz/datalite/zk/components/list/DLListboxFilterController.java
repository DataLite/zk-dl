/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the simplest controller of  DLListboxXxxController family. Just load all the data from database
 * and do the filter only in memory. All you need to implement is the List<T> loadData() method.
 * <p/>
 * Filter, sort and page data in memory.
 *
 * @param <T> model type
 */
public abstract class DLListboxFilterController<T> extends DLListboxSimpleController<T> {
	
	/**
	 * Controller does not refresh data in case of paging or sorting if set to true.
	 */
	private boolean disableReload = false;
	
	/**
	 * Controller holds data in this list if disableReload set to true. 
	 */
	private List<T> data = null;
	
	public DLListboxFilterController() {
        super();
    }

    public DLListboxFilterController(final String identifier) {
        super(identifier);
    }

    public DLListboxFilterController(final String identifier, final Class<T> clazz) {
        super(identifier, clazz);
    }
    
    public DLListboxFilterController(final boolean disableReload) {
        super();
        this.disableReload = disableReload;
    }

    public DLListboxFilterController(final String identifier, final boolean disableReload) {
    	super(identifier);
        this.disableReload = disableReload;        
    }

    public DLListboxFilterController(final String identifier, final Class<T> clazz, final boolean disableReload) {
        super(identifier, clazz);
        this.disableReload = disableReload;
    }

    @Override
    protected DLResponse<T> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts) {
        boolean disjunction = prepareFilter(filter);
        
        if (this.data == null || !this.disableReload) {        	
        	this.data = loadData();
        }

            return DLFilter.filterAndCount(filter, this.data, firstRow, rowCount, sorts, disjunction);
    }

    /**
     * Prepare the filter - unpack filter All if needed.
     *
     * @param filter filter to prepare
     * @return true if the result should use disjunction (in case of filter operator ALL)
     */
    private boolean prepareFilter(List<NormalFilterUnitModel> filter) {
        NormalFilterUnitModel unitFilterAll = null;
        for (NormalFilterUnitModel unitFilter : filter)
            if (NormalFilterModel.ALL.equals(unitFilter.getColumn()))
                unitFilterAll = unitFilter;

        if (unitFilterAll != null) {
            filter.remove(unitFilterAll);
            filter.addAll(unpackFilterAll(unitFilterAll));
            return true;
        } else
            return false;
    }

    /**
     * Filter by all fields - expand to filter sepearate filters for each fields.
     * Default implementation adds all listbox columns witch are isColumn() and isQuickFilter()
     *
     * @param unitFilterAll filter with key ALL
     * @return list of filters by all columns - result should be evaluated with disjunction (OR)
     */
    protected List<NormalFilterUnitModel> unpackFilterAll(NormalFilterUnitModel unitFilterAll) {
        List<NormalFilterUnitModel> toAddList = new ArrayList<NormalFilterUnitModel>();
        for (DLColumnUnitModel unit : getColumnModel().getColumnModels()) {
            if (unit.isColumn() && unit.isQuickFilter()) {
                NormalFilterUnitModel toAdd = new NormalFilterUnitModel(unit);
                toAdd.setOperator(unit.getQuickFilterOperator());
                toAdd.setValue(1, unitFilterAll.getValue(1));
                toAddList.add(toAdd);
            }
        }

        return toAddList;
    }
    
    @Override
	public void clearDataModel() {
		this.data = null;
		super.clearDataModel();
	}

    /**
     * Load data from the database.
     *
     * @return data simple list with coresponding data.
     */
    protected abstract List<T> loadData();

}
