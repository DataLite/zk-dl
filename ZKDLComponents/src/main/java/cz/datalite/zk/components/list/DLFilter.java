package cz.datalite.zk.components.list;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zkoss.lang.SystemException;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.mesg.MCommon;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.zk.components.list.filter.compilers.FilterSimpleCompiler;

/**
 * Utility to filter and sort list with entities.
 * <p>
 * Prins HACK: sort null values, too. Optimalization.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 * @author xmedeko
 */
public final class DLFilter {

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
		final List<T> data = new LinkedList<T>(list);

		sort(sorts, data);
		return filter(filterModel, list, firstRow, rowCount, null, false);
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
		List<T> data = new LinkedList<T>(list);

		sort(sorts, data);

		data = filter(filterModel, list, firstRow, rowCount, distinct, false);

		final List<Object> distinctData = new LinkedList<Object>();
		for (T entity : data) {
			try {
				distinctData.add(getValue(entity, distinct));
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(DLFilter.class.getName()).log(Level.SEVERE, null, ex);
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
		return filterDistinct(filterModel, 0, 0, new LinkedList<DLSort>(), list, distinct);
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
		return filter(filterModel, list, firstRow, rowCount, null, false);
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
			final int firstRow, final int rowCount, final String distinct, final boolean all) {
		for (NormalFilterUnitModel unit : filterModel) {
			if (NormalFilterModel.ALL.equals(unit.getColumn())) {
				throw new UnsupportedOperationException("DLFilter is not able to filter by all columns at one time.");
			}
		}
		try {
			final Set<Object> values = new HashSet<Object>();

			int records = -firstRow;
			final List<T> output = new LinkedList<T>();

			for (T entity : list) {
				if (distinct != null && values.contains(getValue(entity, distinct))) {
					continue; // if distinct and value is in the values continue;
				}
				if (!filter(filterModel, entity)) {
					continue;
				}

				if (distinct != null) {
					values.add(getValue(entity, distinct));
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

	private static <T> boolean filter(final List<NormalFilterUnitModel> filterModel, final T entity)
			throws NoSuchMethodException {
		for (NormalFilterUnitModel unit : filterModel) {
			final FilterCompiler compiler = unit.getFilterCompiler() == null ? FilterSimpleCompiler.INSTANCE
					: unit.getFilterCompiler();
			if (unit.getOperator().getArity() >= 1 & unit.getValue(1) == null) {
				return false; // neprošlo konverzí
			}
			if (!(Boolean) compiler.compile(unit.getOperator(), unit.getColumn(), getValue(entity, unit.getColumn()),
					unit.getValue(1), unit.getValue(2))) {
				return false;
			}
		}
		return true;
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
		final Comparator<T> comparator = new Comparator<T>() {

			public int compare(final T o1, final T o2) {
				if (o1 == o2) {
					return 0;
				}

				for (DLSort sort : sorts) {
					if (DLSortType.NATURAL == sort.getSortType()) {
						continue;
					}
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
	 * Comapre two objects if the forst of
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
		return ((Comparable<Object>) o1).compareTo(o2);
		// this is overbloated in commons-collestions 2.1, in commons-collestions 3.0 and more may be used
		//return ComparableComparator.getInstance().compare(o1, o2);
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list. Index of first row starts at 0.
	 * Also can be defined number of result rows. It row count is 0 all records are returned. There is also return
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
		return filterAndCount(filterModel, list, firstRow, rowCount, new LinkedList<DLSort>());
	}

	/**
	 * Method filters list with entities. DLFilter criterias is defined in the list. Index of first row starts at 0.
	 * Also can be defined number of result rows. It row count is 0 all records are returned. There is also return
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
		final List<T> data = filter(filterModel, list, firstRow, rowCount, null, true);
		sort(sorts, data);

		if (data.size() > firstRow) {
			return new DLResponse<T>(data.subList(firstRow, Math.min(data.size(), firstRow + rowCount)), data.size());
		} else {
			final List<T> emptyList = Collections.emptyList(); // just to avoid type safety warning
			return new DLResponse<T>(emptyList, data.size());
		}
	}

	protected static Object getValue(final Object entity, final String address) throws NoSuchMethodException {
		return Fields.getByCompound(entity, address);
		//Object object = entity;
		//for (String key : address.split("\\.")) {
		//	object = Fields.get(object, key);
		//}
		//return object;
	}
}