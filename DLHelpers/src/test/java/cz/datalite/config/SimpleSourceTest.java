package cz.datalite.config;

import cz.datalite.time.DateTimeUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author <a href="mailto:mkouba@itsys.cz">mkouba</a>
 */
public class SimpleSourceTest {

	@BeforeClass
	public static void resetConfiguration() {
		// Vsechny testy pouzivaji stejnou instanci
		Configuration.instance().clearSources();
	}

	@Test
	public void testSimpleSource() {

		ConfigurationKey k1 = new VirtualConfigurationKey("rada");
		ConfigurationKey k2 = new VirtualConfigurationKey("datumText");
		ConfigurationKey k3 = new VirtualConfigurationKey("datumDate");
		ConfigurationKey k4 = new VirtualConfigurationKey("longText");
		ConfigurationKey k5 = new VirtualConfigurationKey("longLong");
		ConfigurationKey k6 = new VirtualConfigurationKey("booleanText");
		ConfigurationKey k7 = new VirtualConfigurationKey("booleanBoolean");

		Date testDate1 = DateTimeUtil.now();
		Date testDate2 = DateTimeUtil.parseISODateTime("2011-05-17T08:47:00").getTime();

		SimpleSource simpleSource = new SimpleSource();
		simpleSource.putValue(k1, "jedna,dva,tri");
		simpleSource.putValue(k2, "2011-05-17T08:47:00");
		simpleSource.putValue(k3, testDate1);
		simpleSource.putValue(k4, "4");
		simpleSource.putValue(k5, Long.valueOf(4));
		simpleSource.putValue(k6, "false");
		simpleSource.putValue(k7, Boolean.FALSE);

		Configuration.instance().addSource(simpleSource);

		assertEquals("dva", Configuration.instance().getStringArray(k1)[1]);
		assertEquals(testDate1, Configuration.instance().getDate(k3));
		assertEquals(testDate2, Configuration.instance().getDate(k2));
		assertEquals(Configuration.instance().getLong(k4), Configuration.instance().getLong(k5));
		assertEquals(Configuration.instance().getBoolean(k6), Configuration.instance().getBoolean(k7));
	}

	@AfterClass
	public static void resetConfigurationAfter() {
		// Vsechny testy pouzivaji stejnou instanci
		Configuration.instance().clearSources();
	}

}
