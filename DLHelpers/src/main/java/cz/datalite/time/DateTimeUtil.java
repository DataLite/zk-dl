package cz.datalite.time;

import cz.datalite.check.Checker;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility pro praci s casem. Metody by mely byt optimalizovane, nicmene davejte pozor na to, ze {@link SimpleDateFormat} neni
 * synchronizovany! Neni spravne pouzivat jakykoli druh cache bez synchronizace...
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 * @author pmarek
 */
public final class DateTimeUtil {

	/** Cesky format datumu bez casu. */
	public static final String PATTERN_CZ_DATE = "dd.MM.yyyy";

	/** Cesky format datumu s casem. */
	public static final String PATTERN_CZ_DATETIME = "dd.MM.yyyy HH:mm:ss";

	/** Cesky format casu. */
	public static final String PATTERN_CZ_TIME = "HH:mm:ss";

	/**
	 * Format pouzitelny napr. do nazvu souboru
	 */
	public static final String PATTERN_LOG = "yyyyMMddHHmmss";

	/**
	 * Knihovni trida
	 */
	private DateTimeUtil() {
		// knihovni trida
	}

	/**
	 * Vraci dobu trvani naformatovanou jako text:
	 * 
	 * <pre>
	 *  longFormat & !trimZeros =&gt; "0 days 10 hours 0 minutes 47 seconds 972 miliseconds"
	 *  longFormat &  trimZeros =&gt; "10 hours 47 seconds 972 miliseconds"
	 * !longFormat & !trimZeros =&gt; "0d 10h 0m 47s 972ms"
	 * !longFormat &  trimZeros =&gt; "10h 47s 972ms"
	 * </pre>
	 * 
	 * @param duration
	 *        doba trvani
	 * @param longFormat
	 *        zda pouzit ukecany nebo usporny format vypisu
	 * @param trimZeros
	 *        zda zobrazovat hodnoty ktere jsou nulove
	 * 
	 * @return naformatovany retezec
	 * 
	 * @throws IllegalArgumentException
	 *         pokud je predany parametr zaporny
	 */
	public static String formatDuration(final long duration, final boolean longFormat, final boolean trimZeros) {
		if (duration < 0)
			throw new IllegalArgumentException("Duration must be greater than or equal to zero.");

		if (duration == 0L)
			return longFormat ? "0 miliseconds" : "0ms";

		long tm = duration;
		int days = (int) (tm / 86400000);
		tm = tm - (days * 86400000L);
		int hours = (int) (tm / 3600000);
		tm = tm - (hours * 3600000L);
		int mins = (int) (tm / 60000);
		tm = tm - (mins * 60000L);
		int secs = (int) (tm / 1000);
		tm = tm - (secs * 1000L);
		int msecs = (int) tm;

		StringBuilder ret = new StringBuilder();
		if (days > 0 || !trimZeros)
			ret.append(days).append(longFormat ? " days " : "d ");
		if (hours > 0 || !trimZeros)
			ret.append(hours).append(longFormat ? " hours " : "h ");
		if (mins > 0 || !trimZeros)
			ret.append(mins).append(longFormat ? " minutes " : "m ");
		if (secs > 0 || !trimZeros)
			ret.append(secs).append(longFormat ? " seconds " : "s ");
		if (msecs > 0 || !trimZeros)
			ret.append(msecs).append(longFormat ? " miliseconds" : "ms");

		return ret.toString();
	}

	/**
	 * Vraci aktualni datum a cas. Tato metoda neni synchronizovana.
	 * 
	 * @return aktualni datum a cas
	 * 
	 * @see #nowCalendar()
	 * @see #today()
	 * @see #todayCalendar()
	 * @see #yesterday()
	 * @see #yesterdayCalendar()
	 */
	public static Date now() {
		return new DateTime().toDate();
	}

	/**
	 *  Vraci aktualni datum a cas, kdy sekundy a milisekundy jsou nastaveny na 0
	 *
	 * @return aktualni datum a cas bez sekund
	 */
	public static Date nowZeroSec() {
		Calendar cal = new DateTime().toGregorianCalendar();
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	/**
	 * Vraci aktualni datum a cas. Tato metoda neni synchronizovana.
	 * 
	 * @return aktualni datum a cas
	 * 
	 * @see #now()
	 * @see #today()
	 * @see #todayCalendar()
	 * @see #yesterday()
	 * @see #yesterdayCalendar()
	 */
	public static Calendar nowCalendar() {
		return new DateTime().toGregorianCalendar();
	}

	/**
	 * Vraci aktualni datum s casem orizlym na pulnoc.
	 * 
	 * @return dnesni datum
	 * 
	 * @see #todayCalendar()
	 * @see #now()
	 * @see #nowCalendar()
	 * @see #yesterday()
	 * @see #yesterdayCalendar()
	 */
	public static Date today() {
		return new DateMidnight().toDate();
	}

	/**
	 * Vraci aktualni datum s casem orizlym na pulnoc.
	 * 
	 * @return dnesni datum
	 * 
	 * @see #today()
	 * @see #now()
	 * @see #nowCalendar()
	 * @see #yesterday()
	 * @see #yesterdayCalendar()
	 */
	public static Calendar todayCalendar() {
		return new DateMidnight().toGregorianCalendar();
	}

	/**
	 * Vraci vcerejsi datum s casem orizlym na pulnoc.
	 * 
	 * @return vcerejsi datum
	 * 
	 * @see #today()
	 * @see #now()
	 * @see #nowCalendar()
	 * @see #yesterdayCalendar()
	 */
	public static Date yesterday() {
		return new DateMidnight().minusDays(1).toDate();
	}

	/**
	 * Vraci vcerejsi datum s casem orizlym na pulnoc.
	 * 
	 * @return vcerejsi datum
	 * 
	 * @see #today()
	 * @see #now()
	 * @see #nowCalendar()
	 * @see #yesterday()
	 */
	public static Calendar yesterdayCalendar() {
		return new DateMidnight().minusDays(1).toGregorianCalendar();
	}

	/**
	 * Prida dobu k zadanemu casu.
	 * 
	 * @param date
	 *        cas, ke kteremu je pricitano. Pokud je null, je pouzit aktualni cas (now)
	 * @param period
	 *        typ periody definovany ve tride {@link Calendar} (napr. {@link Calendar#HOUR})
	 * @param amount
	 *        pocet pricitanych period (kladny nebo zaporny)
	 * 
	 * @return cas s prictenym mnozstvim jednotek
	 * 
	 * @see #addTimeCalendar(Calendar, int, int)
	 */
	public static Date addTime(final Date date, final int period, final int amount) {
		return addTimeCalendar(date2calendar(date), period, amount).getTime();
	}

	/**
	 * Prida dobu k zadanemu casu.
	 * 
	 * @param calendar
	 *        cas, ke kteremu je pricitano. Pokud je null, je pouzit aktualni cas (now)
	 * @param period
	 *        typ periody definovany ve tride {@link Calendar} (napr. {@link Calendar#HOUR})
	 * @param amount
	 *        pocet pricitanych period (kladny nebo zaporny)
	 * 
	 * @return cas s prictenym mnozstvim jednotek
	 * 
	 * @see #addTime(Date, int, int)
	 */
	public static Calendar addTimeCalendar(final Calendar calendar, final int period, final int amount) {
		Calendar result = (calendar == null ? nowCalendar() : (Calendar) calendar.clone());
		result.add(period, amount);
		return result;
	}

	/**
	 * Prevede {@link Date} na {@link Calendar}.
	 * 
	 * @param date
	 *        datum
	 * 
	 * @return datum jako {@link Calendar}
	 */
	public static Calendar date2calendar(final Date date) {
		if (date == null)
			return null;

		return new DateTime(date).toGregorianCalendar();
	}

	/**
	 * Prevede retezec na datum podle masky pro ceske formatovani.
	 * 
	 * @param date
	 *        datum jako retezec
	 * @param withTime
	 *        zda pracovat s casovou slozkou
	 * 
	 * @return prevedene datum
	 * 
	 * @throws IllegalArgumentException
	 *         pokud retezec neodpovida masce
	 * 
	 * @see #PATTERN_CZ_DATE
	 * @see #PATTERN_CZ_DATETIME
	 * @see #parseDate(String, String)
	 */
	public static Calendar parseDate(final String date, final boolean withTime) {
		return parseDate(date, withTime ? PATTERN_CZ_DATETIME : PATTERN_CZ_DATE);
	}

	/**
	 * Prevede retezec na datum podle zadane masky.
	 * 
	 * @param date
	 *        datum jako retezec
	 * @param pattern
	 *        maska datumu
	 * 
	 * @return prevedene datum
	 * 
	 * @throws IllegalArgumentException
	 *         pokud retezec neodpovida masce
	 * 
	 * @see #parseDate(String, boolean)
	 */
	public static Calendar parseDate(final String date, final String pattern) {
		if (Checker.isBlank(pattern) || Checker.isBlank(date))
			return null;

		return DateTimeFormat.forPattern(pattern).parseDateTime(date).toGregorianCalendar();
	}

	/**
	 * Prevede retezec ve formatu ISO8601 - yyyy-MM-dd'T'HH:mm:ssZZ - na datum.
	 * 
	 * Defaultne UTC casova zona.
	 * 
	 * @param dateTime
	 * @return prevedene datum
	 */
	public static Calendar parseISODateTime(final String dateTime) {
		return ISODateTimeFormat.localDateOptionalTimeParser().parseDateTime(dateTime).toGregorianCalendar();
	}

	/**
	 * Prevede retezec ve formatu ISO8601 - yyyy-MM-dd'T'HH:mm:ssZZ - na datum.
	 * 
	 * Defaultne lokalni casova zona.
	 * 
	 * @param dateTime
	 * @return prevedene datum
	 */
	public static Calendar parseDefaultISODateTime(final String dateTime) {
		return ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(dateTime).toGregorianCalendar();
	}

	/**
	 * Prevede datum na retezec podle masky pro ceske formatovani.
	 * 
	 * @param date
	 *        datum k prevodu
	 * @param withTime
	 *        zda pracovat s casovou slozkou
	 * 
	 * @return datum jako retezec
	 * 
	 * @see #PATTERN_CZ_DATE
	 * @see #PATTERN_CZ_DATETIME
	 * @see #formatDate(Date, String)
	 */
	public static String formatDate(final Date date, final boolean withTime) {
		return formatDate(date, withTime ? PATTERN_CZ_DATETIME : PATTERN_CZ_DATE);
	}

	/**
	 * Prevede datum na retezec podle zadaneho formatu.
	 * 
	 * @param date
	 *        datum
	 * @param pattern
	 *        format pro prevod datumu
	 * 
	 * @return datum jako retezec
	 * 
	 * @throws IllegalArgumentException
	 *         pokud je maska chybna
	 * 
	 * @see #formatDate(Date, boolean)
	 */
	public static String formatDate(final Date date, final String pattern) {

		if (Checker.isBlank(pattern) || date == null)
			return null;

		return DateTimeFormat.forPattern(pattern).print(new DateTime(date));
	}

	/**
	 * Prevede datum na retezec podle locale
	 * 
	 * @param date
	 * @param locale
	 * @return
	 */
	public static String formatDateByLocale(final Date date, Locale locale, int style) {
		if (date == null)
			return null;

		DateFormat df = DateFormat.getDateInstance(style, locale);
		return df.format(date);

		// tohle nefunguje spravne - podle locale se nastavi format, ale ne jazyk
		/*
		 * String pattern = DateTimeFormat.patternForStyle(dateFormatStyle, locale);
		 * return DateTimeFormat.forPattern(pattern).print(new DateTime(date));
		 */
	}

	/**
	 * Prevede datum a cas na retezec podle locale
	 * 
	 * @param date
	 * @param locale
	 * @return
	 */
	public static String formatDateTimeByLocale(final Date date, Locale locale, int style) {
		if (date == null)
			return null;

		DateFormat df = DateFormat.getDateTimeInstance(style, style, locale);
		return df.format(date);
	}

	/**
	 * Vrati z datumu pouze rok.
	 * 
	 * @param date
	 *        datum
	 * 
	 * @return rok
	 * 
	 * @throws IllegalArgumentException
	 *         pokud je datum null
	 */
	public static int extractYear(final Date date) {
		if (date == null)
			throw new IllegalArgumentException("No date specified");

		return new DateTime(date).getYear();
	}

	/**
	 * Vrati prvni den v tydnu pro dane {@link Locale}. Hodnoty jsou konstanty z kalendare, napr. {@link Calendar#MONDAY}.
	 * 
	 * @param locale
	 * @return prvni den v tydnu
	 */
	public static int getFirstDayOfWeek(Locale locale) {
		return Calendar.getInstance(locale).getFirstDayOfWeek();
	}

	/**
	 * @param time
	 * @return zda je zadany cas v minulosti
	 */
	public static boolean inPast(Date time) {
		return new Date().after(time);
	}

	/**
	 * @param time
	 * @return rano zadaneho data
	 */
	public static Date getMidnight(Date time) {
		if (time == null)
			return null;

		return new DateMidnight(time).toDate();
	}
}
