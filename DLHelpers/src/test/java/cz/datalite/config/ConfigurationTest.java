package cz.datalite.config;

import java.util.Date;

import cz.datalite.exception.ProblemException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test pro {@link cz.datalite.config.Configuration}.
 *
 * @see cz.datalite.config.DefaultConfigurationSource
 *
 * @author mpeterka
 */
public class ConfigurationTest {

	@BeforeClass
	public static void resetConfiguration() {
		// Vsechny testy pouzivaji stejnou instanci
		Configuration.instance().clearSources();
	}

	@AfterClass
	public static void resetConfigurationAfter() {
		// Vsechny testy pouzivaji stejnou instanci
		Configuration.instance().clearSources();
	}

	/**
	 * Testuje, zda se dava prednost konfiguracni polozce ze zdroje s vyssi prioritou
	 */
	@Test
	public void testPrecedence() {
		Configuration configuration = Configuration.instance();

		ConfigurationSource csLowPrecedence = new DefaultConfigurationSource() {

			@Override
			public String getString(ConfigurationKey key) {
				return "lowPrecedence";
			}

			@Override
			public int getPrecedence() {
				return 2;
			}
		};
		configuration.addSource(csLowPrecedence);

		ConfigurationSource csHighPrecedence = new DefaultConfigurationSource() {

			@Override
			public String getString(ConfigurationKey key) {
				return "highPrecedence";
			}

			@Override
			public int getPrecedence() {
				return 10;
			}
		};
		configuration.addSource(csHighPrecedence);

		ConfigurationSource csLowesPrecedence = new DefaultConfigurationSource() {

			@Override
			public String getString(ConfigurationKey key) {
				return "lowestPrecedence";
			}

			@Override
			public int getPrecedence() {
				return 0;
			}
		};
		configuration.addSource(csLowesPrecedence);

		Assert.assertEquals("highPrecedence", configuration.getString("test"));

	}

	/**
	 * Testuje Date.clone()
	 */
	@Test
	public void testGetLastUpdate() {

		Configuration configuration = Configuration.instance();

		Assert.assertNull("New configuration without any configurationSource can not have lastUpdate set", configuration.getLastUpdate());

		configuration.addSource(new DefaultConfigurationSource());

		Date lastUpdate = configuration.getLastUpdate();
		Assert.assertNotNull("Configuration with configurationSource must have lastUpdate set", lastUpdate);

		long originalTime = lastUpdate.getTime();
		// Pokus o editaci hodnoty, ktera ma byt jen pro cteni
		lastUpdate.setTime(111);

		Assert.assertTrue("Configuration exposed internal representation of lastUpdate property", originalTime == configuration
				.getLastUpdate().getTime());

	}

	@Test
	public void testMandatory() {
		Configuration configuration = Configuration.instance();
		configuration.addSource(new DefaultConfigurationSource());

		assertEquals((Object) 1L, configuration.getLong(new VirtualConfigurationKey("foo")));
		try {
			configuration.getLong(new VirtualConfigurationKey("neni"));
			fail("musi padnout - konf. polozka neexistuje");
		} catch (ProblemException e) {
			// expected
		}
		assertNull(configuration.getLong(null));
	}

	@Test
	public void testDefaultValue() {
		Configuration configuration = Configuration.instance();
		configuration.addSource(new DefaultConfigurationSource());

		assertEquals((Object) 1L, configuration.getLong(new VirtualConfigurationKey("foo"), 33L));
		assertEquals((Object) 33L, configuration.getLong(new VirtualConfigurationKey("neni"), 33L));
		assertNull(configuration.getLong(new VirtualConfigurationKey("neni"), null));

	}
}
