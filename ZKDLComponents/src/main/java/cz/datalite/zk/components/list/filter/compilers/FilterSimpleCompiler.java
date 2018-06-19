package cz.datalite.zk.components.list.filter.compilers;

import cz.datalite.helpers.TypeConverter;
import cz.datalite.zk.components.list.DLFilter;
import cz.datalite.zk.components.list.enums.DLFilterOperator;

/**
 * This class implements simple compiler which evaluates expressions just in time and place whithout another
 * compilation. These methods returns if the expression is true or false. For extension the set of operators have to be
 * extended compile method here on in its parent.
 * <p>
 *
 * @author Karel Cemus
 * @author xmedeko (new implementation which works with null values)
 */
public class FilterSimpleCompiler extends AbstractFilterCompiler {

	public static final FilterSimpleCompiler INSTANCE = new FilterSimpleCompiler();

	/**
	 *
	 * @param value
	 * @return Converts {@code null} to an empty String to simplyfi some operations.
	 */
	private static Object nullToEmptyString(Object value) {
		return value == null ? "" : value;
	}

	/**
	 * Evaluation according operator. First value is first operand, other operands are optional according to operand
	 * arity
	 *
	 * @param values
	 *            operands
	 * @return result of the evaluation
	 */
	@Override
	public Boolean compile(final DLFilterOperator operator, final String key, final Object... values) {
		return (Boolean) super.compile(operator, key, values);
	}

	protected Boolean compileOperatorEqual(final String key, final Object... values) {
            
            // value to compare with
            final Object val0 = values[0];
            // value to be compared
            final Object val1 = values[1];
            
            // filter out one null value as false and both as true
            if ( val0 == null ) return val0 == val1;
            
             // try to convert 2nd object to target class
            Object compareTo = val1;
            if ( compareTo instanceof String  ) {
                compareTo = TypeConverter.tryToConvertToSilent((String) compareTo, val0.getClass());
            }
            
            // compare instances
            return val0.equals(compareTo);
	}

	protected Boolean compileOperatorNotEqual(final String key, final Object... values) {
		return !this.compileOperatorEqual(key, values);
	}

	protected Boolean compileOperatorEmpty(final String key, final Object... values) {
		return values[0] == null || values[0].toString().length() == 0;
	}

	protected Boolean compileOperatorNotEmpty(final String key, final Object... values) {
		return !this.compileOperatorEmpty(key, values);
	}

	protected Boolean compileOperatorLike(final String key, final Object... values) {
		Object val0 = values[0], val1 = values[1];
		if (val0 == val1 || val1 == null) {
			return true;
		}
		val0 = nullToEmptyString(val0);
		val1 = nullToEmptyString(val1);
		return val0.toString().toLowerCase().contains(val1.toString().toLowerCase());
	}

	protected Boolean compileOperatorNotLike(final String key, final Object... values) {
		return !this.compileOperatorLike(key, values);
	}

	protected Boolean compileOperatorStartWith(final String key, final Object... values) {
		Object val0 = values[0], val1 = values[1];
		if (val0 == val1) {
			return true;
		}
		val0 = nullToEmptyString(val0);
		val1 = nullToEmptyString(val1);
		return val0.toString().toLowerCase().startsWith(val1.toString().toLowerCase());
	}

	protected Boolean compileOperatorEndWith(final String key, final Object... values) {
		Object val0 = values[0], val1 = values[1];
		if (val0 == val1) {
			return true;
		}
		val0 = nullToEmptyString(val0);
		val1 = nullToEmptyString(val1);
		return val0.toString().toLowerCase().endsWith(val1.toString().toLowerCase());
	}

	protected Boolean compileOperatorGreaterThan(final String key, final Object... values) {
		return DLFilter.compare(values[0], values[1]) > 0;
	}

	protected Boolean compileOperatorGreaterEqual(final String key, final Object... values) {
		return DLFilter.compare(values[0], values[1]) >= 0;
	}

	protected Boolean compileOperatorLesserThan(final String key, final Object... values) {
		return DLFilter.compare(values[0], values[1]) < 0;
	}

	protected Boolean compileOperatorLesserEqual(final String key, final Object... values) {
		return DLFilter.compare(values[0], values[1]) <= 0;
	}

	protected Boolean compileOperatorBetween(final String key, final Object... values) {
		return values[0] != null &&
				DLFilter.compare(values[0], values[1]) >= 0 && DLFilter.compare(values[0], values[2]) <= 0;
	}

}