package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.compilers.FilterByAllCompiler;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.compilers.FilterSimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.mesg.MCommon;

import java.text.Collator;
import java.util.*;

import static cz.datalite.zk.components.list.filter.FilterUtils.getConvertedValue;
import static cz.datalite.zk.components.list.filter.FilterUtils.getValue;

/**
 * Utility to filter and sort list with entities.
 * <p>
 * Prins HACK: sort null values, too. Optimalization.
 *
 * @author Karel Cemus
 * @author xmedeko
 */
public final class DLFilter {
    
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DLFilter.class);

	private DLFilter() {
	}

	/**
	 * Method filters and sort list with entities. DLFilter criterias is defined in the list as well as sorts. Index of
	 * first row starts at 0. Also can be defined number of result rows. It row count is 0 all records are returned.
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param sorts
	 *            list of sorts criterias
	 * @param list
	 *            list of entities
	 * @return filtered list - new instance
	 */
	public static <T> List<T> filter(final List<NormalFilterUnitModel> filterModel, final int firstRow,
			final int rowCount, final List<DLSort> sorts, final List<T> list) {
		final List<T> data = new LinkedList<>(list);

		sort(sorts, data);
		return filter(filterModel, data, firstRow, rowCount, null, rowCount == 0);
	}

	/**
	 * Method filters and sort list with entities. DLFilter criterias is defined in the list as well as sorts. Index of
	 * first row starts at 0. Also can be defined number of result rows. It row count is 0 all records are returned.
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param sorts
	 *            list of sorts criterias
	 * @param list
	 *            list of entities
	 * @param distinct
	 *            name of the column with unique data
	 * @return filtered list - new instance
	 */
	public static <T> List<Object> filterDistinct(final List<NormalFilterUnitModel> filterModel, final int firstRow,
			final int rowCount, final List<DLSort> sorts, final List<T> list, final String distinct) {
		List<T> data = new LinkedList<>(list);

		sort(sorts, data);

		data = filter(filterModel, data, firstRow, rowCount, distinct, rowCount == 0);

		final List<Object> distinctData = new LinkedList<>();
		for (T entity : data) {
			try {
				distinctData.add(getValue(entity, distinct));
			} catch (NoSuchMethodException ex) {
				LOGGER.error("Filtering in DLFilter failed!",ex);
			}
		}
		return distinctData;
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list.
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param list
	 *            list of entities
	 * @param distinct
	 *            name of the column with unique data
	 * @return filtered list - new instance
	 */
	public static <T> List<Object> filterDistinct(final List<NormalFilterUnitModel> filterModel, final List<T> list,
			final String distinct) {
		return filterDistinct(filterModel, 0, 0, new LinkedList<>(), list, distinct);
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list as well as sorts. Index of first row
	 * starts at 0. Also can be defined number of result rows. It row count is 0 all records are returned.
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param list
	 *            list of entities
	 * @return filtered list - new instance
	 */
	public static <T> List<T> filter(final List<NormalFilterUnitModel> filterModel, final List<T> list,
			final int firstRow, final int rowCount) {
		return filter(filterModel, list, firstRow, rowCount, null, rowCount == 0);
	}

    /**
     * Method filters and sort list with entities. DLFilter criterias is defined in the list as well as sorts. Index of
     * first row starts at 0. Also can be defined number of result rows. It row count is 0 all records are returned.
     *
     * @param <T>
     *            entity type in the listbox
     * @param filterModel
     *            model criterias
     * @param firstRow
     *            index of the first row
     * @param rowCount
     *            max row count
     * @param list
     *            list of entities
     * @param distinct
     *            name of the column with unique data
     * @param all
     *            return all records or only coresponding
     * @return filtered list - new instance
     */
    private static <T> List<T> filter(final List<NormalFilterUnitModel> filterModel, final List<T> list,
                                      final int firstRow, final int rowCount, final String distinct, final boolean all)
    {
        return filter(filterModel, list, firstRow, rowCount, distinct, all, false);
    }

	/**
	 * Method filters and sort list with entities. DLFilter criterias is defined in the list as well as sorts. Index of
	 * first row starts at 0. Also can be defined number of result rows. It row count is 0 all records are returned.
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param list
	 *            list of entities
	 * @param columnName
	 *            name of the column with unique data
	 * @param all
	 *            return all records or only coresponding
     * @param disjunction
     *            multiple filter condition - disjunction or conjunction (OR or AND)
     *
	 * @return filtered list - new instance
	 */
	private static <T> List<T> filter(final List<NormalFilterUnitModel> filterModel, final List<T> list,
			final int firstRow, final int rowCount, final String columnName, final boolean all, final boolean disjunction) {

                // Changed by ZK-161 on 27.6.2012
                //  @author Karel Cemus
                //  Implemented support for filter method FilterByAll
                //  This option need specialized compiler. It can be
                //  determined when the model is used but because of repeated
                //  calling in a loop, the compiler is set now to allow for 
                //  better performance
		for (NormalFilterUnitModel unit : filterModel) {
			if (NormalFilterModel.ALL.equals(unit.getColumn())) {
                            unit.setFilterCompiler(FilterByAllCompiler.INSTANCE);
                            unit.setOperator(DLFilterOperator.EQUAL, true);
			}
		}
                
		try {
			final Set<Object> values = new HashSet<>();

			int records = -firstRow;
			final List<T> output = new LinkedList<>();

			for (T entity : list) {
				if (columnName != null && values.contains(getValue(entity, columnName))) {
					continue; // if columnName and value is in the values continue;
				}
				if (!filter(filterModel, entity, disjunction)) {
					continue;
				}

				if (columnName != null) {
					values.add(getValue(entity, columnName));
				}

				records++;
				if (records > 0 || all) {
					output.add(entity);
				}

				if (records == rowCount && rowCount != 0 && !all) {
					return output;
				}
			}
			return output;
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

    private static <T> boolean filter(final List<NormalFilterUnitModel> filterModel, final T entity, final boolean disjunction) throws NoSuchMethodException {

         // if no filter specified, it is ok. all "rules" are satisfied
        if (filterModel.isEmpty()) {
            return true;
        }
        
        // apply each filter rule on given entity
        for (NormalFilterUnitModel unit : filterModel) {
            
            // get compiler for the value and operator to be able to evaluate the rule
            final FilterCompiler compiler = unit.getFilterCompiler() == null ? FilterSimpleCompiler.INSTANCE : unit.getFilterCompiler();            

            // prevent compilation of rules with arity higher or equal to 1 
            // to prevent null pointer exception on value1.compareTo(value2)
            // what is common implementation of comparison
            if (unit.getOperator() != null && unit.getOperator().getArity() >= 1 & unit.getValue(1) == null) {
                
                // if the filter is not in disjunction mode then the rule cannot 
                // be satisfied. If the filter is in the disjunction mode
                // than another rule can be satisfied and entity can be valid
                if (!disjunction) {
                    // conversion not satisfied
                    return false; 
                }
            }

            // value to be compiled and compared
            final Object fieldValue = getConvertedValue( getValue(entity, unit.getColumn()), unit.getColumnModel());
            
            // check the result, make comparison based on given operator
            if ((Boolean) compiler.compile(unit.getOperator(), unit.getColumn(), fieldValue, unit.getValue(1), unit.getValue(2))) {
                // comparison was succesfull, value fits the rules
                
                // when the filter is in disjunction mode, than is enough to 
                // satisfy just one rule, so the entity is valid
                if (disjunction) {
                    return true;
                }
            } else {
                
                // if the filter is not in disjunction mode then the rule cannot 
                // be satisfied. If the filter is in the disjunction mode
                // than another rule can be satisfied and entity can be valid
                if (!disjunction) {
                    return false;
                }
            }
        }

        
        // If the filter is in a disjunction mode, than we are looking for at least
        // one rule (nonempty ruleset) that the value matches. If there is no 
        // such rule thus the value is not valid.
        // If the filter is in a conjunction mode, than we are looking for just one
        // rule, that the value doesn't match. Otherwise the value is valid. 
        // Now there are no other rules, so all rules are satisfied in conjunction mode
        // but none of them is satisfied in disjunction mode.
        // so return TRUE for conjunction mode, otherwise FALSE
        return !disjunction;
    }

	/**
	 * Sorts list of entities according to list of sorts.
	 *
	 * @param <T>
	 *            entity type in the list
	 * @param sorts
	 *            list of sort parameters
	 * @param list
	 *            entitites
	 * @return sorted list - <b>SAME</b> instance
	 */
	public static <T> List<T> sort(final List<DLSort> sorts, final List<T> list) {

        if (sorts == null || sorts.isEmpty()) {
                    return list; // no sorting
        }

		final Comparator<T> comparator = new Comparator<T>() {

			public int compare(final T o1, final T o2) {
				if (o1 == o2) {
					return 0;
				}

				for (DLSort sort : sorts) {
					if (DLSortType.NATURAL == sort.getSortType()) {
						continue;
					}

                    if (sort.getColumn() == null)
                        throw new IllegalArgumentException("DLFilter cannot be used with sqlFormula sort: " + sort.toString());

					int compare = beanCompare(sort.getColumn(), o1, o2);
					if (compare == 0) {
						continue;
					}
					switch (sort.getSortType()) {
					case ASCENDING:
						return compare;
					case DESCENDING:
						return compare * -1;
					default:
					}
				}
				return 0;
			}
		};

		Collections.sort(list, comparator);

		return list;
	}

	/**
	 * Compare beans by their common property.
	 *
	 * @param property
	 *            The property.
	 * @param o1
	 *            The first bean.
	 * @param o2
	 *            The second bean.
	 * @return a negative integer, zero, or a positive integer as this protperty in the forst bean is less than, equal
	 *         to, or greater than the property in the second bean.
	 *
	 * @see Comparable#compareTo(Object)
	 */
	public static int beanCompare(String property, Object o1, Object o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		if (property == null) {
			return compare(o1, o2);
		}
		try {
			Object value1 = Fields.getByCompound(o1, property);//PropertyUtils.getProperty(o1, property);
			Object value2 = Fields.getByCompound(o2, property); //PropertyUtils.getProperty(o2, property);
			if (value1 == value2) {
				return 0;
			}
			if (value1 == null) {
				return -1;
			}
			if (value2 == null) {
				return 1;
			}
			return compare(value1, value2);
			// this is overbloated in commons-collestions 2.1, in commons-collestions 3.0 and more may be used
			//return ComparableComparator.getInstance().compare(value1, value2);
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex, MCommon.NOT_FOUND, property);
		}
	}

	/**
	 * Comapre two objects if the forst of.
	 * When comparing strings collator is used. Locale for collator can be changed by setting zk-dl.dlFilter.stringSort.locale library property
	 *
	 * @param o1
	 *            First object, must not be {@code null}. Must be the instance of {@link Comparable}.
	 * @param o2
	 *            The second object.
	 * @return a negative integer, zero, or a positive integer as {@code o1} is less than, equal to, or greater than
	 *         {@code o2}.
	 *
	 * @see Comparable#compareTo(Object)
	 */
	@SuppressWarnings("unchecked")
	public static int compare(Object o1, Object o2) {

		if(o1 instanceof String && o2 instanceof String){
			String s1 = (String) o1;
			String s2 = (String) o2;

			String sortLocale = Library.getProperty("zk-dl.dlFilter.stringSort.locale");
			Locale locale;

			if(StringHelper.isNull(sortLocale)){
				locale = Locale.getDefault();
			} else {
				locale = Locale.forLanguageTag(sortLocale);
			}

			Collator collator = Collator.getInstance(locale);

			return collator.compare(s1,s2);

		} else {
			return ((Comparable<Object>) o1).compareTo(o2);
		}
		// this is overbloated in commons-collestions 2.1, in commons-collestions 3.0 and more may be used
		//return ComparableComparator.getInstance().compare(o1, o2);
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list. Index of first row starts at 0.
	 * Also can be defined number of result rows. If row count is 0 all records are returned. There is also return
	 * number of total size of filtered rows
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param list
	 *            list of entities
	 * @return filtered list - new instance
	 */
	public static <T> DLResponse<T> filterAndCount(final List<NormalFilterUnitModel> filterModel, final List<T> list,
			final int firstRow, final int rowCount) {
		return filterAndCount(filterModel, list, firstRow, rowCount, new LinkedList<>());
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list. Index of first row starts at 0.
	 * Also can be defined number of result rows. If row count is 0 all records are returned. There is also return
	 * number of total size of filtered rows
	 *
	 * @param <T>
	 *            entity type in the listbox
	 * @param filterModel
	 *            model criterias
	 * @param firstRow
	 *            index of the first row
	 * @param rowCount
	 *            max row count
	 * @param list
	 *            list of entities
	 * @param sorts
	 *            list of sorting criterias
	 * @return filtered list - new instance
	 */
	public static <T> DLResponse<T> filterAndCount(final List<NormalFilterUnitModel> filterModel, final List<T> list,
			final int firstRow, final int rowCount, final List<DLSort> sorts) {
        return filterAndCount(filterModel, list, firstRow, rowCount, sorts, false);
	}

    /**
     * Method filters list with entities. DLFilter criterias is defined in the list. Index of first row starts at 0.
     * Also can be defined number of result rows. If row count is 0 all records are returned. There is also return
     * number of total size of filtered rows
     *
     * @param <T>
     *            entity type in the listbox
     * @param filterModel
     *            model criterias
     * @param firstRow
     *            index of the first row
     * @param rowCount
     *            max row count
     * @param list
     *            list of entities
     * @param sorts
     *            list of sorting criterias
     * @param disjunction
     *            multiple filter condition - disjunction or conjunction (OR or AND)
     * @return filtered list - new instance
     */
    public static <T> DLResponse<T> filterAndCount(final List<NormalFilterUnitModel> filterModel, final List<T> list,
                                                   final int firstRow, final int rowCount, final List<DLSort> sorts,
                                                   boolean disjunction)
    {
        final List<T> data = filter(filterModel, list, firstRow, rowCount, null, true, disjunction);
        sort(sorts, data);

        if (rowCount == 0)
        {
            return new DLResponse<>(data, data.size()); // all data
        }
        else if (data.size() > firstRow) {
            return new DLResponse<>(data.subList(firstRow, Math.min(data.size(), firstRow + rowCount)), data.size());
        } else {
            final List<T> emptyList = new LinkedList<>();
            return new DLResponse<>(emptyList, data.size());
        }
    }

    
}