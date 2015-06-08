package cz.datalite.zk.converter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link cz.datalite.makro.wapl.web.converter.DateOnlyConverter}
 */
public class DateOnlyConverterTest {

	@Test
	public void testCoerceToUi() throws Exception {
		assertNull(new DateOnlyConverter().coerceToUi(null, null, null));
		Date date = new Date();
		assertEquals(date, new DateOnlyConverter().coerceToUi(date, null, null));
	}

	@Test
	public void testCoerceToBean() throws Exception {
		assertNull(new DateOnlyConverter().coerceToBean(null, null, null));
		Date date = new Date();
		Date truncated = new DateOnlyConverter().coerceToBean(date, null, null);
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
				.appendHourOfDay(2)
				.appendLiteral(":")
				.appendMinuteOfHour(2)
				.appendLiteral(":")
				.appendSecondOfMinute(2)
				.appendLiteral(".")
				.appendMillisOfSecond(3).toFormatter();

		assertEquals("00:00:00.000", fmt.print(new DateTime(truncated)));

	}
}