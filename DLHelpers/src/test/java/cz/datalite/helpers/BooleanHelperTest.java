package cz.datalite.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link BooleanHelper}
 */
public class BooleanHelperTest {

	@Test
	public void testIsTrueCharFalse() throws Exception {
		assertFalse(BooleanHelper.isTrue((Character) null));
		assertFalse(BooleanHelper.isTrue(' '));
		assertFalse(BooleanHelper.isTrue('0'));
		assertFalse(BooleanHelper.isTrue('1'));// ???
		assertFalse(BooleanHelper.isTrue('n'));
		assertFalse(BooleanHelper.isTrue('f'));
		assertFalse(BooleanHelper.isTrue('F'));
	}

	@Test
	public void testIsTrueCharTrue() throws Exception {
		assertTrue(BooleanHelper.isTrue('y'));
		assertTrue(BooleanHelper.isTrue('a'));
		assertTrue(BooleanHelper.isTrue('Y'));
		assertTrue(BooleanHelper.isTrue('A'));
	}

	@Test
	public void testIsTrueStringFalse() throws Exception {
		assertFalse(BooleanHelper.isTrue((Character) null));
		assertFalse(BooleanHelper.isTrue(" "));
		assertFalse(BooleanHelper.isTrue("0"));
		assertFalse(BooleanHelper.isTrue("n"));
		assertFalse(BooleanHelper.isTrue("f"));
		assertFalse(BooleanHelper.isTrue("F"));

		assertFalse(BooleanHelper.isTrue("foo"));
	}

	@Test
	public void testIsTrueStringTrue() throws Exception {
		assertTrue(BooleanHelper.isTrue("true"));
		assertTrue(BooleanHelper.isTrue("y"));
		assertTrue(BooleanHelper.isTrue("yes"));
		assertTrue(BooleanHelper.isTrue("a"));
		assertTrue(BooleanHelper.isTrue("ano"));
		assertTrue(BooleanHelper.isTrue("1"));

		assertTrue(BooleanHelper.isTrue("TRUE"));
		assertTrue(BooleanHelper.isTrue("Y"));
		assertTrue(BooleanHelper.isTrue("YES"));
		assertTrue(BooleanHelper.isTrue("A"));
		assertTrue(BooleanHelper.isTrue("ANO"));
	}

	@Test
	public void testIsFalseTrue() throws Exception {
		assertTrue(BooleanHelper.isFalse("false"));
		assertTrue(BooleanHelper.isFalse("ne"));
		assertTrue(BooleanHelper.isFalse("no"));
		assertTrue(BooleanHelper.isFalse("0"));

		assertTrue(BooleanHelper.isFalse("False"));
		assertTrue(BooleanHelper.isFalse("Ne"));
		assertTrue(BooleanHelper.isFalse("No"));
	}

	@Test
	public void testIsFalseFalse() throws Exception {
		assertFalse(BooleanHelper.isFalse("true"));
		assertFalse(BooleanHelper.isFalse("y"));
		assertFalse(BooleanHelper.isFalse("yes"));
		assertFalse(BooleanHelper.isFalse("1"));

		assertFalse(BooleanHelper.isFalse("TRUE"));
		assertFalse(BooleanHelper.isFalse("Y"));
		assertFalse(BooleanHelper.isFalse("1"));
		assertFalse(BooleanHelper.isFalse("foo"));
	}

	@Test
	public void testBooleanToString() throws Exception {
		assertEquals("Ano", BooleanHelper.booleanToString(true));
		assertEquals("Ne", BooleanHelper.booleanToString(false));

	}
}