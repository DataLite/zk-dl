package cz.datalite.zk.converter;


import org.junit.ComparisonFailure;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test for {@link BigDecimalConverter}
 */
public class BigDecimalConverterTest {

	@Test
	public void testCoerceToUi() throws Exception {
		assertNull(new BigDecimalConverter().coerceToUi(null, null, null));
		decimalEquals("0.00", new BigDecimalConverter().coerceToUi(BigDecimal.ZERO, null, null));
		decimalEquals("1.00", new BigDecimalConverter().coerceToUi(BigDecimal.ONE, null, null));
		decimalEquals("10.00", new BigDecimalConverter().coerceToUi(BigDecimal.TEN, null, null));
		decimalEquals("11.25", new BigDecimalConverter().coerceToUi(new BigDecimal(11.25), null, null));
	}

	private void decimalEquals(String expected, String actual) {
		final String dotted = actual.replace(',', '.');
		if (!dotted.equals(expected)) {
			throw new ComparisonFailure("Not equals", expected, dotted);
		}
	}

	@Test
	public void testCoerceToBean() throws Exception {
		assertNull(new BigDecimalConverter().coerceToBean(null, null, null));
		assertNull(new BigDecimalConverter().coerceToBean("", null, null));
		assertEquals(new BigDecimal(1), new BigDecimalConverter().coerceToBean("1", null, null));

		assertEquals(new BigDecimal(1.5),
				new BigDecimalConverter().coerceToBean("1" + DecimalFormatSymbols.getInstance().getDecimalSeparator() + "5", null, null));

		try {
			new BigDecimalConverter().coerceToBean("Tetragonia tetragonioides", null, null);
			fail("Should fail - parse error");
		} catch (Exception ignore) {
			// expected
		}
	}
}