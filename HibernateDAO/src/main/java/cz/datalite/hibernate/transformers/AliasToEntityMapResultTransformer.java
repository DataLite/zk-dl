
package cz.datalite.hibernate.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.ResultTransformer;

/**
 * Transform result to HashMap.
 *
 * This class is the single incompatibility between Hibernate 3.2 and 3.5 (which means JPA 1.0 and JPA 2.0), because
 * in 3.5 is as singleton with private constructor. Duplicate implementation (copyed from 3.5) provided to solve this.
 *
 * @author Jiri Bubnik
 */
public class AliasToEntityMapResultTransformer implements ResultTransformer {

	public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();

	/**
	 * Disallow instantiation of AliasToEntityMapResultTransformer.
	 */
	private AliasToEntityMapResultTransformer() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Map result = new HashMap(tuple.length);
		for ( int i=0; i<tuple.length; i++ ) {
			String alias = aliases[i];
			if ( alias!=null ) {
				result.put( alias, tuple[i] );
			}
		}
		return result;
	}

	/**
	 * Serialization hook for ensuring singleton uniqueing.
	 *
	 * @return The singleton instance : {@link #INSTANCE}
	 */
	private Object readResolve() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	public List transformList(List list) {
            return list;
	}

}
