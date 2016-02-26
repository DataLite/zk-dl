package cz.datalite.dao.plsql;

/**
 * Converter which do nothing. Used just as default converter, because java anootation {@code default null} is not possible.
 */
public class NoopConverter implements Converter<Object, Object> {
	@Override
	public Object fromDb(Object value) {
		return value;
	}

}
