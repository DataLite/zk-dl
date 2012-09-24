package cz.datalite.zk.components.list.filter;

import cz.datalite.helpers.StringHelper;

import java.util.LinkedList;

/**
 * Model for normal filter - groups unit models. This is ordinary LinkedList
 * with defined generic and redefined clone method. This modification of ordinary
 * class makes code much simpler.
 *
 * 
 * @author Karel Cemus
 */
public class NormalFilterModel extends LinkedList<NormalFilterUnitModel> implements Cloneable {

    public static final String ALL = QuickFilterModel.CONST_ALL;

    public NormalFilterModel() {
        super();
        // creates empty filter model with no units
    }

    public NormalFilterModel( final NormalFilterModel normalFilterModel ) {
        super();
        addAll( normalFilterModel );
    }

    @Override
    public NormalFilterModel clone() {
        return new NormalFilterModel( this );
    }

    /**
     * Findl normal filter unit model by colum name
     *
     * @param columnName column name to find
     * @return found object or null
     */
    public NormalFilterUnitModel findUnitModelByColumnName(String columnName) {
        if (StringHelper.isNull(columnName))
            throw new IllegalArgumentException("findUnitModelByColumnName without columnName secified");

        for (NormalFilterUnitModel unitModel : this)
            if (columnName.equals(unitModel.getColumn()))
                return unitModel;

        return null;
    }
}
