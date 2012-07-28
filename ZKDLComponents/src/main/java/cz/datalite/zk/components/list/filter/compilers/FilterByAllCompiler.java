package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.FilterUtils;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.List;

/**
 * <p>FilterByAllCompiler adds a support for
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL} option in
 * in-memory filtering by {@link cz.datalite.zk.components.list.DLFilter}.</p>
 *
 * <p>Quick filter provides a special option <strong>
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL}</strong>
 * which indicates that a user wants to search for given value in each visible
 * column. If some column matches the criteria then the row is valid and should
 * be involved in the result.</p>
 *
 * <p>This class provides functionality which allows to decide if the given
 * entity is valid under
 * {@link cz.datalite.zk.components.list.filter.NormalFilterModel#ALL} option
 * based on given column model with column definition.</p>
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class FilterByAllCompiler implements FilterCompiler {

    /**
     * singleton
     */
    public static final FilterCompiler INSTANCE = new FilterByAllCompiler();

    /**
     * private constructor for singleton design pattern
     */
    private FilterByAllCompiler() {
    }

    public Object compile(DLFilterOperator unusedOperator, String key, Object... values) {
        // as a part of change ZK-161 the model is propagated as 2nd parameter
        List<DLColumnUnitModel> model = (List<DLColumnUnitModel>) values[2];

        // the key used as needle
        final String needle = (String) values[1];

        // for each column
        for (DLColumnUnitModel unit : model) {

            try {

                // filter by visible columns only
                // filter by recognized columns only
                // filter by columns available in QF only
                if (!unit.isVisible() || !unit.isColumn() || !unit.isQuickFilter()) {
                    continue;
                }

                // get compiler for the value and operator to be able to evaluate the rule
                final FilterCompiler compiler = unit.getFilterCompiler() == null ? FilterSimpleCompiler.INSTANCE : unit.getFilterCompiler();

                // get filter operater. There are expected 2 values:CONTAINS for Strings and EQUALS for others
                final DLFilterOperator operator = unit.getQuickFilterOperator();

                // get value to be evaluated against given key
                final Object value = FilterUtils.getValue(values[0], unit.getColumn());

                // if the value is missing it cannot match the rules
                if (value == null) {
                    continue;
                }

                // try to evaluate column
                // if matches, entity is valid
                if ((Boolean) compiler.compile(operator, unit.getColumn(), value, needle)) {
                    return true;
                }

            } catch (NoSuchMethodException ex) {
                // ignore, field cannot be used in filter
            }
        }

        // no column matches the rule, entity is not valid
        return false;
    }

    /**
     * By default, all values can be used, override if you need another
     * behaviour.
     *
     * @param value the value for filter
     * @return always true
     */
    public boolean validateValue(Object value) {
        return true;
    }
}
