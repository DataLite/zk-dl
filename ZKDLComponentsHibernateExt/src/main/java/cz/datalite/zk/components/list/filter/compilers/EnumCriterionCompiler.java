/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.components.list.filter.compilers;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.zkoss.lang.reflect.Fields;

import java.util.Arrays;

/**
 * Compile criteria query for enumeration type.
 */
public class EnumCriterionCompiler extends FilterCriterionCompiler {

    protected final Enum[] enums;

    protected String searchPropertyName = null;

    /**
     * Create new compiler for criterion. Normally it supports only eq a ne operators, but it behavies with like operator if
     * filter value contains only a String type - this is the case of QuickFilter.
     *
     * @param enums available enum values
     */
    public EnumCriterionCompiler( final Enum[] enums ) {
        super();
        this.enums = enums.clone();
    }

    /**
     * Create new compiler for criterion. Normally it supports only eq a ne operators, but it behavies with like operator if
     * filter value contains only a String type - this is the case of QuickFilter.
     *
     * @param enums available enum values
     * @param searchPropertyName use this property for like operation for usage with Quick Filter
     */
    public EnumCriterionCompiler( final Enum[] enums, String searchPropertyName ) {
        super();
        this.enums = enums.clone();
        this.searchPropertyName = searchPropertyName;
    }

    @Override
    protected Criterion compileOperatorEqual(final String key, final Object... values) {
        final Object value = values[0];

        // special case - input from Quick Filter
        if (value instanceof String) {
            Disjunction disj = Restrictions.disjunction();
            final String val = (String) value;

            for (Enum en : enums)
            {
                String searchColumn = en.toString();
                if (searchPropertyName != null)
                    try {
                        searchColumn = Fields.getByCompound( en, searchPropertyName ).toString();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException("Misconfigured EnumCriterionCompiler: enum " + en +
                                " does not contain property: " + searchPropertyName);
                    }

                if (searchColumn != null && searchColumn.toLowerCase().contains(val.toLowerCase()))
                    disj.add(Restrictions.eq(key, en));
            }

            return disj.add(Restrictions.sqlRestriction("1=0"));
        } else // we expect exact enum value
        {
            return Restrictions.eq(key, value);
        }
    }

    /**
     * Not equals is used only from normal filter, so exact enum value should be available
     * @param key
     * @param values
     * @return
     */
    @Override
    protected Criterion compileOperatorNotEqual(final String key, final Object... values) {
        final Object value = values[0];
        return Restrictions.ne(key, value);
    }

    /**
     * Correct value can be a string (from quick filter) - then it works with like or one of the enums.
     *
     * @param value the value for filter
     * @return true for null, String, or one of init Enums
     */
    public boolean validateValue(Object value) {
        if (value == null)
            return true;
        else if (value instanceof String)
            return true;
        else if ( Arrays.asList(enums).contains(value) )
            return true;
        else
            return false;
    }
}
