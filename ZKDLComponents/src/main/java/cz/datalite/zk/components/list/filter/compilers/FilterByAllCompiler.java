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
 * @author Karel Cemus
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
        // that is needed allow to translate the ALL operator. So there are needed full model
        List<DLColumnUnitModel> model = (List<DLColumnUnitModel>) values[2];

        // the key used as needle, the value input by the user
        // occurrence of this value is tested in the each column
        final String needle = (String) values[1];

        // for each column
        for (DLColumnUnitModel unit : model) {

            try {

                // filter by visible columns only
                // filter by recognized (deifned) columns only
                // filter by columns available in QF only
                if (!unit.isVisible() || !unit.isColumn() || !unit.isQuickFilter()) {
                    continue;
                }

                // get compiler for the value and operator to be able to evaluate the rule
                final FilterCompiler compiler = unit.getFilterCompiler() == null ? FilterSimpleCompiler.INSTANCE : unit.getFilterCompiler();

                // When the user wants to filter by ALL then he wants to use WYSIWYG search
                // Due to his expectations the filter uses CONTAINS operator to make a STRING
                // comparison as the user expect Eg. integer: value "1223", needle "223". CONTAINS evaluates this
                // as the valid entity but the EQUALS doesn't.
                final DLFilterOperator operator = DLFilterOperator.LIKE;

                // get value to be evaluated against given key
                // entity property - the value shown in the listbox
                // try to convert value
                final Object value = FilterUtils.getConvertedValue( FilterUtils.getValue(values[0], unit.getColumn()), unit);

                // if the value is missing it cannot match the rules
                // this if also prevents the NPE in the following code
                if (value == null) {
                    continue;
                }

                // try to evaluate column
                // if matches, entity is valid
                // this is the basic implementation used also in DLFilter
                if ((Boolean) compiler.compile(operator, unit.getColumn(), value, needle)) {
                    // matching property is found, the entity is valid
                    // otherwise we have to continue searching for matching column
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
