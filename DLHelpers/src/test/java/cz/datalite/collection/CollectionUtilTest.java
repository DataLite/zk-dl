package cz.datalite.collection;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testy pro {@link cz.datalite.collection.CollectionUtil}
 */
public class CollectionUtilTest {

	@Test
	public void testContainsNotNull() throws Exception {
		ArrayList<String> list = new ArrayList<>();
		assertFalse(CollectionUtil.containsNotNull(list));
		list.add(null);
		assertFalse(CollectionUtil.containsNotNull(list));
		list.add(null);
		assertFalse(CollectionUtil.containsNotNull(list));

		list.add("foo");
		assertTrue(CollectionUtil.containsNotNull(list));
	}
}
