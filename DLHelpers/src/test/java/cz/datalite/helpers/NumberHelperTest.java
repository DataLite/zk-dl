package cz.datalite.helpers;

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for {@link NumberHelper}
 */
public class NumberHelperTest {

	private static final double DELTA = 0.001;

	@Test
	public void testGetCzechCurrencyBigDecimal() throws Exception {
		assertEquals("", NumberHelper.getCzechCurrency((BigDecimal) null));
		assertEquals("1 Kč", NumberHelper.getCzechCurrency(BigDecimal.valueOf(1)));
		assertEquals("1 000 Kč", NumberHelper.getCzechCurrency(BigDecimal.valueOf(1000)));
		assertEquals("33,5 Kč", NumberHelper.getCzechCurrency(BigDecimal.valueOf(33.5)));
		assertEquals("-33,5 Kč", NumberHelper.getCzechCurrency(BigDecimal.valueOf(-33.5)));
	}

	@Test
	public void testGetCzechCurrencyDouble() throws Exception {
		assertEquals("", NumberHelper.getCzechCurrency((Double) null));
		assertEquals("1 Kč", NumberHelper.getCzechCurrency((double) 1));
		assertEquals("1 000 Kč", NumberHelper.getCzechCurrency((double) 1000));
		assertEquals("33,5 Kč", NumberHelper.getCzechCurrency(33.5));
		assertEquals("-33,5 Kč", NumberHelper.getCzechCurrency(-33.5));
	}

	@Test
	public void testGetCzechCurrencyLong() throws Exception {
		assertEquals("", NumberHelper.getCzechCurrency((Long) null));
		assertEquals("1 Kč", NumberHelper.getCzechCurrency((long) 1));
		assertEquals("1 000 Kč", NumberHelper.getCzechCurrency((long) 1000));
		assertEquals("-1 000 Kč", NumberHelper.getCzechCurrency((long) -1000));
	}

	@Test
	public void testRound() throws Exception {
		try {
			NumberHelper.round(null, 2);
			fail();
		} catch (NullPointerException ignore) {/*expected*/}
		assertEquals(1.0, NumberHelper.round((double) 1, 2), DELTA);
		assertEquals(1.330, NumberHelper.round(1.333, 2), DELTA);
		assertEquals(2.0, NumberHelper.round(1.999, 2), DELTA);
		assertEquals(0.0, NumberHelper.round(1.222, -2), DELTA);
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("0", NumberHelper.toString(null));
		assertEquals(Double.valueOf(0.0).toString(), NumberHelper.toString(0.0));
	}

	@Test
	public void testNullToNulaDouble() throws Exception {
		assertEquals(0.0, NumberHelper.nullToNula((Double) null), DELTA);

	}

	@Test
	public void testNullToNulaLong() throws Exception {
		assertEquals(0, NumberHelper.nullToNula((Long) null), DELTA);

	}
}