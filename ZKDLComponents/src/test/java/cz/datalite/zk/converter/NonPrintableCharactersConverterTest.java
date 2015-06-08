package cz.datalite.zk.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link NonPrintableCharactersConverter}
 */
public class NonPrintableCharactersConverterTest {

	@Test
	public void testClean() throws Exception {
		assertNull(NonPrintableCharactersConverter.clean(null));
		assertEquals("", NonPrintableCharactersConverter.clean(""));
		assertEquals("a b c", NonPrintableCharactersConverter.clean("a b c"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("abc"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("ab\tc"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("ab\t\nc"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("ab\t\r\nc"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("a\tb\t\r\nc"));
		assertEquals("abc", NonPrintableCharactersConverter.clean("a\t\t\tbc"));
	}

	@Test
	public void testCleanStika() {
		// MAKRO-203
		assertEquals("Treba tenhle: ", NonPrintableCharactersConverter.clean("Treba tenhle: \u200B"));
	}

}