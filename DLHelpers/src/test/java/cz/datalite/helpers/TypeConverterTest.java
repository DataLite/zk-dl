package cz.datalite.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link cz.datalite.helpers.TypeConverter}
 */
public class TypeConverterTest {

	@Test
	public void testTryToConvertToNull() throws Exception {
		assertNull(TypeConverter.tryToConvertTo(null, Object.class));
		assertNull(TypeConverter.tryToConvertTo("", Object.class));
	}

	@Test
	public void testTryToConvertToInteger() throws Exception {
		assertEquals(Integer.valueOf(0), TypeConverter.tryToConvertTo("0", Integer.class));
		assertEquals(Integer.valueOf(1), TypeConverter.tryToConvertTo("1", Integer.class));
		assertEquals(Integer.valueOf(123456), TypeConverter.tryToConvertTo("123456", Integer.class));
		assertEquals(Integer.valueOf(-123456), TypeConverter.tryToConvertTo("-123456", Integer.class));

		try {
			TypeConverter.tryToConvertTo("nan", Integer.class);
			fail();
		} catch (NumberFormatException expected) {
			// expected
		}
	}

	@Test
	public void testTryToConvertToLong() throws Exception {
		assertEquals(Long.valueOf(0), TypeConverter.tryToConvertTo("0", Long.class));
		assertEquals(Long.valueOf(1), TypeConverter.tryToConvertTo("1", Long.class));
		assertEquals(Long.valueOf(123456), TypeConverter.tryToConvertTo("123456", Long.class));
		assertEquals(Long.valueOf(-123456), TypeConverter.tryToConvertTo("-123456", Long.class));

		try {
			TypeConverter.tryToConvertTo("nan", Long.class);
			fail();
		} catch (NumberFormatException expected) {
			// expected
		}
	}

	@Test
	public void testTryToConvertToDouble() throws Exception {
		assertEquals(Double.valueOf(0), TypeConverter.tryToConvertTo("0", Double.class));
		assertEquals(Double.valueOf(1), TypeConverter.tryToConvertTo("1", Double.class));
		assertEquals(Double.valueOf(123456), TypeConverter.tryToConvertTo("123456", Double.class));
		assertEquals(Double.valueOf(-123456), TypeConverter.tryToConvertTo("-123456", Double.class));
		assertEquals(Double.valueOf(3.14), TypeConverter.tryToConvertTo("3.14", Double.class));

		try {
			TypeConverter.tryToConvertTo("nan", Double.class);
			fail();
		} catch (NumberFormatException expected) {
			// expected
		}
	}

	@Test
	public void testTryToConvertToString() throws Exception {
		assertEquals("0", TypeConverter.tryToConvertTo("0", String.class));
		assertEquals("1", TypeConverter.tryToConvertTo("1", String.class));
		assertEquals("123456", TypeConverter.tryToConvertTo("123456", String.class));
		assertEquals("abcd e", TypeConverter.tryToConvertTo("abcd e", String.class));
	}

	@Test
	public void testTryToConvertToByte() throws Exception {
		assertEquals(Byte.valueOf((byte) 0), TypeConverter.tryToConvertTo("0", Byte.class));
		assertEquals(Byte.valueOf((byte) 1), TypeConverter.tryToConvertTo("1", Byte.class));
		assertEquals(Byte.MAX_VALUE, TypeConverter.tryToConvertTo("127", Byte.class));
		assertEquals(Byte.MIN_VALUE, TypeConverter.tryToConvertTo("-128", Byte.class));

		try {
			TypeConverter.tryToConvertTo("nan", Byte.class);
			fail();
		} catch (NumberFormatException expected) {
			// expected
		}


		try {
			TypeConverter.tryToConvertTo("129", Byte.class);
			fail();
		} catch (NumberFormatException expected) {
			// expected
		}

	}
}