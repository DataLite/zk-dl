package cz.datalite.check;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testy pro tridu {@link cz.datalite.check.Checker}
 * 
 * @author pmarek
 */
public class CheckerTest {

	@Test
	public void testIsNullOrEmptyArray() {
		assertTrue(Checker.isNullOrEmpty((Object[]) null));
		assertTrue(Checker.isNullOrEmpty(new Object[] {}));
		assertFalse(Checker.isNullOrEmpty(new String[] { null }));
		assertFalse(Checker.isNullOrEmpty(new String[] { "" }));
		assertFalse(Checker.isNullOrEmpty(new String[] { "Test1" }));
		assertFalse(Checker.isNullOrEmpty(new String[] { "Test1", "Test2" }));
	}

	@Test
	public void testIsNullOrEmptyCollection() {
		assertTrue(Checker.isNullOrEmpty((List<?>) null));
		assertTrue(Checker.isNullOrEmpty(new ArrayList<>()));
		assertTrue(Checker.isNullOrEmpty(new HashSet<>()));
		assertTrue(Checker.isNullOrEmpty(new ArrayList<>(1)));
		assertFalse(Checker.isNullOrEmpty(Arrays.asList("")));
		assertFalse(Checker.isNullOrEmpty(Arrays.asList("Test1")));
		assertFalse(Checker.isNullOrEmpty(Arrays.asList(1, 2)));
	}

	@Test
	public void testIsNullOrEmptyMap() {
		assertTrue(Checker.isNullOrEmpty((Map<?, ?>) null));
		assertTrue(Checker.isNullOrEmpty(new HashMap<Object, Void>()));
		Map<String, Integer> m = new HashMap<>(2);
		assertTrue(Checker.isNullOrEmpty(m));
		m.put(null, null);
		assertFalse(Checker.isNullOrEmpty(m));
		m.remove(null);
		assertTrue(Checker.isNullOrEmpty(m));
		m.put("123", 456);
		assertFalse(Checker.isNullOrEmpty(m));
	}

	@Test
	public void testIsNullOrEmptyString() {
		assertTrue(Checker.isNullOrEmpty((String) null));
		assertTrue(Checker.isNullOrEmpty(""));
		assertFalse(Checker.isNullOrEmpty(" "));
		assertFalse(Checker.isNullOrEmpty("\\t\\n \\r\\l"));
		assertFalse(Checker.isNullOrEmpty("test"));
	}

	@Test
	public void testIsBlank() {
		assertTrue(Checker.isBlank(null));
		assertTrue(Checker.isBlank(""));
		assertTrue(Checker.isBlank(" "));
		assertTrue(Checker.isBlank("                 "));
		assertTrue(Checker.isBlank("  \n  \t  \r  \f  "));
		assertFalse(Checker.isBlank("test"));
	}

}
