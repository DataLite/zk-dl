package cz.datalite.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link StringHelper}
 */
public class StringHelperTest {

	@Test
	public void testIsNull() throws Exception {
		assertTrue(StringHelper.isNull(null));
		assertTrue(StringHelper.isNull(""));
		assertFalse(StringHelper.isNull(" "));
		assertFalse(StringHelper.isNull("foo"));
	}

	@Test
	public void testIsEquals() throws Exception {
		assertTrue(StringHelper.isEquals(null, null));

		assertFalse(StringHelper.isEquals("a", null));
		assertFalse(StringHelper.isEquals(null, "b"));
		assertFalse(StringHelper.isEquals("a", "b"));
		assertFalse(StringHelper.isEquals("a", "A"));

		assertTrue(StringHelper.isEquals("a", "a"));
	}

	@Test
	public void testIsEqualsIgnoreCase() throws Exception {
		assertTrue(StringHelper.isEqualsIgnoreCase(null, null));

		assertFalse(StringHelper.isEqualsIgnoreCase("a", null));
		assertFalse(StringHelper.isEqualsIgnoreCase(null, "b"));
		assertFalse(StringHelper.isEqualsIgnoreCase("a", "b"));

		assertTrue(StringHelper.isEqualsIgnoreCase("a", "A"));
		assertTrue(StringHelper.isEqualsIgnoreCase("a", "a"));
		assertTrue(StringHelper.isEqualsIgnoreCase("1", "1"));
	}

	@Test
	public void testIsNumeric() throws Exception {
		try {
			assertFalse(StringHelper.isNumeric(null));
		} catch (NullPointerException ignore) {/*expected*/}
		assertTrue(StringHelper.isNumeric("1"));
		assertTrue(StringHelper.isNumeric("3.14"));
		assertTrue(StringHelper.isNumeric("-3.14"));
		assertFalse(StringHelper.isNumeric("3,14"));
		assertFalse(StringHelper.isNumeric("-3,14"));
		assertTrue(StringHelper.isNumeric("1e10"));
		assertFalse(StringHelper.isNumeric("foo"));

	}

	@Test
	public void testFromObject() throws Exception {
		assertNull(StringHelper.fromObject(null));
		assertEquals("", StringHelper.fromObject(""));
		assertEquals("foo", StringHelper.fromObject("foo"));
		assertEquals("1", StringHelper.fromObject(1));
	}

	@Test
	public void testSplitString() throws Exception {
		assertNull(StringHelper.splitString(null, ";"));
		assertEquals(0, StringHelper.splitString("", ";").size());//!
		assertEquals(2, StringHelper.splitString("a;b", ";").size());
		assertEquals("a", StringHelper.splitString("a;b", ";").get(0));
		assertEquals(1, StringHelper.splitString("a;b", "-").size());
	}

	@Test
	public void testToAN() throws Exception {
		assertEquals('A', StringHelper.toAN("A", "A", "N"));
		assertEquals('N', StringHelper.toAN("N", "A", "N"));
		assertEquals('\0', StringHelper.toAN("B", "A", "N"));
	}

	@Test
	public void testToInt() throws Exception {
		try {
			StringHelper.toInt(null);
			fail();
		} catch (NumberFormatException ignore) {/*ignore*/}
		try {
			StringHelper.toInt("1.5");
		} catch (NumberFormatException ignore) {/*ignore*/}

		assertEquals(1, StringHelper.toInt("1"));
		assertEquals(-1, StringHelper.toInt("-1"));
	}

	@Test
	public void testNotNullConcat() throws Exception {
		assertEquals("a;b;c", StringHelper.notNullConcat(";", new String[]{"a", "b", "c"}));
		assertEquals("a;b;c", StringHelper.notNullConcat(";", new String[]{"a", "b", "c", null}));
		assertEquals("abc", StringHelper.notNullConcat("", new String[]{"a", "b", "c", null}));
		assertEquals("", StringHelper.notNullConcat(";", new String[]{}));
	}

	@Test
	public void testNotNullConcatValue2() throws Exception {
		assertEquals("a;c", StringHelper.notNullConcat(";", new String[]{"a", null, "c", null}));
	}

	@Test
	public void testNvl() throws Exception {
		assertEquals("a", StringHelper.nvl("a", "b"));
		assertEquals("b", StringHelper.nvl("", "b"));
		assertEquals("b", StringHelper.nvl(null, "b"));
		assertNull(StringHelper.nvl(null, null));
	}

}