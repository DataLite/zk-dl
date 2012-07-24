package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;

/**
 * <p>FilterByAllCompiler adds a support for 
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL} option
 * in in-memory filtering by {@link cz.datalite.zk.components.list.DLFilter}.</p>
 * 
 * <p>Quick filter provides a special option <strong>
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL}</strong>
 * which indicates that a user wants to search for given value 
 * in each visible column. If some column matches the criteria then the row
 * is valid and should be involved in the result.</p>
 * 
 * <p>This class provides functionality which allows to decide if the given
 * entity is valid under 
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL} 
 * option based on given column model with column definition.</p>
 * 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class FilterByAllCompiler implements FilterCompiler{

    public Object compile(DLFilterOperator operator, String key, Object... values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean validateValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
