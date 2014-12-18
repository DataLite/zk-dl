package cz.datalite.collection;

import cz.datalite.collection.Pair;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test pro {@link cz.datalite.collection.Pair}
 */
public class PairTest {

	@Test
	public void testEquals() throws Exception {
		Pair<Long, String> p = new Pair<>(1L, "A");
		assertTrue(p.equals(p));
		assertTrue(new Pair<>(1L, "A").equals(new Pair<>(1L, "A")));
		assertFalse(new Pair<>(1L, "A").equals(new Pair<>(1L, "B")));
		assertFalse(new Pair<>(1L, "A").equals(new Pair<>(2L, "A")));
		assertFalse(new Pair<>(1L, "A").equals(null));
	}
}
