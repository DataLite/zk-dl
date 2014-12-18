package cz.datalite.config;

import cz.datalite.config.ConfigurationKey;
import cz.datalite.config.ConfigurationSource;
import cz.datalite.config.ConfigurationValueType;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class ConfigurationSourceTest {

	@Test
	public void testTypes() throws SecurityException, NoSuchMethodException {

		for (ConfigurationValueType type : ConfigurationValueType.values()) {
			switch (type) {
			case BIGDECIMAL:
				ConfigurationSource.class.getMethod("getBigDecimal", ConfigurationKey.class);
				break;
			case DATETIME:
				ConfigurationSource.class.getMethod("getDate", ConfigurationKey.class);
				break;
			case BOOLEAN:
				ConfigurationSource.class.getMethod("getBoolean", ConfigurationKey.class);
				break;
			case INTEGER:
				ConfigurationSource.class.getMethod("getInteger", ConfigurationKey.class);
				break;
			case LONG:
				ConfigurationSource.class.getMethod("getLong", ConfigurationKey.class);
				break;
			case STRING:
				ConfigurationSource.class.getMethod("getString", ConfigurationKey.class);
				break;
			default:
				break;
			}
		}

		if (ConfigurationValueType.values().length != 6)
			Assert.fail("Overit konzistenci ConfigurationValueType a ConfigurationSource");
	}

}
