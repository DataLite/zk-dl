package cz.datalite.time;

import org.joda.time.DateTime;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.*;

public class DateTimeUtilTest {

	private static final DateTime testDateTimeFull = new DateTime(2010, 12, 30, 13, 59, 59, 999);
	private static final DateTime testDateTime = new DateTime(2010, 12, 30, 13, 59, 59, 0);
	private static final DateTime testDate = new DateTime(2010, 12, 30, 0, 0, 0, 0);

	private static final String patternFull = "dd.MM.yyyy HH:mm:ss.SSS";

	@Test
	public void testFormatDuration() {
		try {
			DateTimeUtil.formatDuration(-1, true, true);
			fail();
		} catch (IllegalArgumentException expected) {
			// ok
		}
		assertEquals("0 miliseconds", DateTimeUtil.formatDuration(0, true, true));
		assertEquals("0 miliseconds", DateTimeUtil.formatDuration(0, true, false));
		assertEquals("0ms", DateTimeUtil.formatDuration(0, false, true));
		assertEquals("0ms", DateTimeUtil.formatDuration(0, false, false));
		assertEquals("0 days 10 hours 0 minutes 47 seconds 972 miliseconds", DateTimeUtil.formatDuration(36047972, true, false));
		assertEquals("10 hours 47 seconds 972 miliseconds", DateTimeUtil.formatDuration(36047972, true, true));
		assertEquals("0d 10h 0m 47s 972ms", DateTimeUtil.formatDuration(36047972, false, false));
		assertEquals("10h 47s 972ms", DateTimeUtil.formatDuration(36047972, false, true));
	}

	@Test
	public void testNow() {
		assertNotNull(DateTimeUtil.now());
		assertTrue(DateTimeUtil.now().getClass().equals(Date.class));
	}

	@Test
	public void testNowCalendar() {
		assertNotNull(DateTimeUtil.nowCalendar());
		assertTrue(DateTimeUtil.nowCalendar().getClass().equals(GregorianCalendar.class));
	}

	@Test
	public void testToday() {
		assertNotNull(DateTimeUtil.today());
		assertTrue(DateTimeUtil.today().getClass().equals(Date.class));
		assertEquals(new DateTime().withTimeAtStartOfDay().toDate(), DateTimeUtil.today());
	}

	@Test
	public void testTodayCalendar() {
		assertNotNull(DateTimeUtil.todayCalendar());
		assertTrue(DateTimeUtil.todayCalendar().getClass().equals(GregorianCalendar.class));
		assertEquals(new DateTime().withTimeAtStartOfDay().toGregorianCalendar(), DateTimeUtil.todayCalendar());
	}

	@Test
	public void testYesterday() {
		assertNotNull(DateTimeUtil.yesterday());
		assertTrue(DateTimeUtil.yesterday().getClass().equals(Date.class));
		assertEquals(new DateTime().withTimeAtStartOfDay().minusDays(1).toDate(), DateTimeUtil.yesterday());
	}

	@Test
	public void testYesterdayCalendar() {
		assertNotNull(DateTimeUtil.yesterdayCalendar());
		assertTrue(DateTimeUtil.yesterdayCalendar().getClass().equals(GregorianCalendar.class));
		assertEquals(new DateTime().withTimeAtStartOfDay().minusDays(1).toGregorianCalendar(), DateTimeUtil.yesterdayCalendar());
	}

	@Test
	public void testAddTime() {
		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(testDateTimeFull.toDate(), patternFull));
		assertTrue(Date.class.equals(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MILLISECOND, 0).getClass()));
		assertEquals(DateTimeUtil.formatDate(new Date(), false),
				DateTimeUtil.formatDate(DateTimeUtil.addTime(null, Calendar.YEAR, 0), false));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MILLISECOND, 0), patternFull));
		assertEquals("30.12.2010 14:00:00.000",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MILLISECOND, 1), patternFull));
		assertEquals("30.12.2010 13:59:59.998",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MILLISECOND, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.SECOND, 0), patternFull));
		assertEquals("30.12.2010 14:00:00.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.SECOND, 1), patternFull));
		assertEquals("30.12.2010 13:59:58.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.SECOND, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MINUTE, 0), patternFull));
		assertEquals("30.12.2010 14:00:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MINUTE, 1), patternFull));
		assertEquals("30.12.2010 13:58:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MINUTE, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR_OF_DAY, 0), patternFull));
		assertEquals("30.12.2010 14:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR_OF_DAY, 1), patternFull));
		assertEquals("30.12.2010 12:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR_OF_DAY, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, 0), patternFull));
		assertEquals("30.12.2010 14:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, 1), patternFull));
		assertEquals("30.12.2010 12:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, 0), patternFull));
		assertEquals("30.12.2010 14:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, 1), patternFull));
		assertEquals("30.12.2010 12:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.HOUR, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DAY_OF_MONTH, 0), patternFull));
		assertEquals("01.01.2011 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DAY_OF_MONTH, 2), patternFull));
		assertEquals("29.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DAY_OF_MONTH, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DATE, 0), patternFull));
		assertEquals("01.01.2011 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DATE, 2), patternFull));
		assertEquals("29.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DATE, -1), patternFull));
		assertEquals("19.01.2011 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.DATE, 20), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MONTH, 0), patternFull));
		assertEquals("28.02.2011 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MONTH, 2), patternFull));
		assertEquals("30.11.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.MONTH, -1), patternFull));

		assertEquals("30.12.2010 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.YEAR, 0), patternFull));
		assertEquals("30.12.2011 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.YEAR, 1), patternFull));
		assertEquals("30.12.2009 13:59:59.999",
				DateTimeUtil.formatDate(DateTimeUtil.addTime(testDateTimeFull.toDate(), Calendar.YEAR, -1), patternFull));
	}

	@Test
	public void testAddTimeCalendar() {
		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(testDateTimeFull.toDate(), patternFull));
		assertTrue(GregorianCalendar.class.equals(DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(),
				Calendar.MILLISECOND, 0).getClass()));
		assertEquals(DateTimeUtil.formatDate(new Date(), false),
				DateTimeUtil.formatDate(DateTimeUtil.addTimeCalendar(null, Calendar.YEAR, 0).getTime(), false));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MILLISECOND, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:00:00.000", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MILLISECOND, 1).getTime(), patternFull));
		assertEquals("30.12.2010 13:59:59.998", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MILLISECOND, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.SECOND, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:00:00.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.SECOND, 1).getTime(), patternFull));
		assertEquals("30.12.2010 13:59:58.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.SECOND, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MINUTE, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:00:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MINUTE, 1).getTime(), patternFull));
		assertEquals("30.12.2010 13:58:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MINUTE, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR_OF_DAY, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR_OF_DAY, 1).getTime(), patternFull));
		assertEquals("30.12.2010 12:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR_OF_DAY, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, 1).getTime(), patternFull));
		assertEquals("30.12.2010 12:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, 0).getTime(), patternFull));
		assertEquals("30.12.2010 14:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, 1).getTime(), patternFull));
		assertEquals("30.12.2010 12:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.HOUR, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DAY_OF_MONTH, 0).getTime(), patternFull));
		assertEquals("01.01.2011 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DAY_OF_MONTH, 2).getTime(), patternFull));
		assertEquals("29.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DAY_OF_MONTH, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DATE, 0).getTime(), patternFull));
		assertEquals("01.01.2011 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DATE, 2).getTime(), patternFull));
		assertEquals("29.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.DATE, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MONTH, 0).getTime(), patternFull));
		assertEquals("28.02.2011 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MONTH, 2).getTime(), patternFull));
		assertEquals("30.11.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.MONTH, -1).getTime(), patternFull));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.YEAR, 0).getTime(), patternFull));
		assertEquals("30.12.2011 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.YEAR, 1).getTime(), patternFull));
		assertEquals("30.12.2009 13:59:59.999", DateTimeUtil.formatDate(
				DateTimeUtil.addTimeCalendar(testDateTimeFull.toGregorianCalendar(), Calendar.YEAR, -1).getTime(), patternFull));
	}

	@Test
	public void testDate2calendar() {
		assertNull(DateTimeUtil.date2calendar(null));
		Date date = new Date();
		assertEquals(date, DateTimeUtil.date2calendar(date).getTime());
	}

	@Test
	public void testParseDateStringBoolean() {
		assertNull(DateTimeUtil.parseDate(null, true));
		assertNull(DateTimeUtil.parseDate(null, true));

		try {
			DateTimeUtil.parseDate("30.12.2010 13:59:59.999", true);
			fail();
		} catch (IllegalArgumentException expected) {
			// ok
		}

		assertEquals(testDateTime.getMillis(), DateTimeUtil.parseDate("30.12.2010 13:59:59", true).getTimeInMillis());
		assertEquals(testDate.getMillis(), DateTimeUtil.parseDate("30.12.2010", false).getTimeInMillis());
	}

	@Test
	public void testParseDateStringString() {
		assertNull(DateTimeUtil.parseDate(null, DateTimeUtil.PATTERN_CZ_DATE));
		assertNull(DateTimeUtil.parseDate("", null));

		try {
			DateTimeUtil.parseDate("30.12.2010 13:59:59.999", DateTimeUtil.PATTERN_CZ_DATETIME);
			fail();
		} catch (IllegalArgumentException expected) {
			// ok
		}

		assertEquals(testDateTime.getMillis(), DateTimeUtil.parseDate("30.12.2010 13:59:59", DateTimeUtil.PATTERN_CZ_DATETIME)
				.getTimeInMillis());
		assertEquals(testDate.getMillis(), DateTimeUtil.parseDate("30.12.2010", DateTimeUtil.PATTERN_CZ_DATE).getTimeInMillis());

	}

	@Test
	public void testFormatDateDateBoolean() {
		assertNull(DateTimeUtil.formatDate(null, true));
		assertNull(DateTimeUtil.formatDate(null, false));

		assertEquals("30.12.2010 13:59:59", DateTimeUtil.formatDate(testDateTimeFull.toDate(), true));
		assertEquals("30.12.2010 13:59:59", DateTimeUtil.formatDate(testDateTime.toDate(), true));
		assertEquals("30.12.2010 00:00:00", DateTimeUtil.formatDate(testDate.toDate(), true));

		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDateTimeFull.toDate(), false));
		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDateTime.toDate(), false));
		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDate.toDate(), false));
	}

	@Test
	public void testFormatDateDateString() {
		assertNull(DateTimeUtil.formatDate(null, DateTimeUtil.PATTERN_CZ_DATE));
		assertNull(DateTimeUtil.formatDate(new Date(), null));
		assertNull(DateTimeUtil.formatDate(new Date(), ""));
		assertNull(DateTimeUtil.formatDate(new Date(), "    \t   \n   "));

		try {
			DateTimeUtil.formatDate(new Date(), "sasdfasdfg");
			fail();
		} catch (IllegalArgumentException expected) {
			// ok
		}

		assertEquals("30.12.2010 13:59:59", DateTimeUtil.formatDate(testDateTimeFull.toDate(), DateTimeUtil.PATTERN_CZ_DATETIME));
		assertEquals("30.12.2010 13:59:59", DateTimeUtil.formatDate(testDateTime.toDate(), DateTimeUtil.PATTERN_CZ_DATETIME));
		assertEquals("30.12.2010 00:00:00", DateTimeUtil.formatDate(testDate.toDate(), DateTimeUtil.PATTERN_CZ_DATETIME));

		assertEquals("30.12.2010 13:59:59.999", DateTimeUtil.formatDate(testDateTimeFull.toDate(), patternFull));
		assertEquals("30.12.2010 13:59:59.000", DateTimeUtil.formatDate(testDateTime.toDate(), patternFull));
		assertEquals("30.12.2010 00:00:00.000", DateTimeUtil.formatDate(testDate.toDate(), patternFull));

		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDateTimeFull.toDate(), DateTimeUtil.PATTERN_CZ_DATE));
		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDateTime.toDate(), DateTimeUtil.PATTERN_CZ_DATE));
		assertEquals("30.12.2010", DateTimeUtil.formatDate(testDate.toDate(), DateTimeUtil.PATTERN_CZ_DATE));
	}

	@Test
	public void testFormatDateByLocale() {
		String dateENUS = DateTimeUtil.formatDateByLocale(new Date(), new Locale("en"), DateFormat.LONG);
		String dateCZ = DateTimeUtil.formatDateByLocale(new Date(), new Locale("cs"), DateFormat.LONG);

		assertNotNull(dateENUS);
		assertNotNull(dateCZ);

		assertNull(DateTimeUtil.formatDateByLocale(null, new Locale("cs"), DateFormat.LONG));
	}

	@Test
	public void testFormatDateTimeByLocale() {
		String dateENUS = DateTimeUtil.formatDateTimeByLocale(new Date(), new Locale("en"), DateFormat.LONG);
		String dateCZ = DateTimeUtil.formatDateTimeByLocale(new Date(), new Locale("cs"), DateFormat.LONG);

		assertNotNull(dateENUS);
		assertNotNull(dateCZ);
		assertNull(DateTimeUtil.formatDateTimeByLocale(null, Locale.ENGLISH, DateFormat.LONG));
	}

	@Test
	public void testExtractYear() {
		try {
			DateTimeUtil.extractYear(null);
			fail();
		} catch (IllegalArgumentException expected) {
			// ok
		}

		assertEquals(2010, DateTimeUtil.extractYear(testDateTimeFull.toDate()));
		assertEquals(2027, DateTimeUtil.extractYear(testDateTime.plusYears(17).toDate()));
		assertEquals(1981, DateTimeUtil.extractYear(testDate.minusYears(29).toDate()));
	}

	/**
	 * 
	 */
	@Test
	public void testFirstDayOfWeek() {
		assertEquals(Calendar.MONDAY, DateTimeUtil.getFirstDayOfWeek(new Locale("cs", "CZ")));
		assertEquals(Calendar.SUNDAY, DateTimeUtil.getFirstDayOfWeek(new Locale("en", "US")));
	}

	/**
	 * 
	 */
	@Test
	public void testISODateTime() {
		Calendar calendar = DateTimeUtil.parseISODateTime("2011-05-17T08:47:00.000");
		assertEquals(2011, calendar.get(Calendar.YEAR));
		assertEquals(8, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, calendar.get(Calendar.SECOND));
	}

	@Test
	public void testGetMidnight() {
		Date mid = DateTimeUtil.getMidnight(new Date(100000000000L));
		assertEquals(99961200000L, mid.getTime());
	}

	@Test
	public void testInPast() {
		 assertTrue(DateTimeUtil.inPast(DateTimeUtil.parseISODateTime("1945-05-17T08:47:00.000").getTime()));
		 assertFalse(DateTimeUtil.inPast(DateTimeUtil.parseISODateTime("2945-05-17T08:47:00.000").getTime()));

		try {
			DateTimeUtil.inPast(null);
			fail("DateTimeUtil.inPast(null) should fail on NPE");
		} catch (NullPointerException expected) {
			//expected
		}
	}
}
