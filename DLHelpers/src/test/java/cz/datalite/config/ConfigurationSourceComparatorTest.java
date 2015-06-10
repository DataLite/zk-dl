package cz.datalite.config;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Test komparatoru zdroju konfiguraci.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class ConfigurationSourceComparatorTest {

	@Test
	public void testSorting() {

		ConfigurationSource s1 = new ConfigurationSource() {

			@Override
			public void update() {
				// empty
			}

			@Override
			public String getString(ConfigurationKey key) {
				return null;
			}

			@Override
			public int getPrecedence() {
				return 10;
			}

			@Override
			public Integer getInteger(ConfigurationKey key) {
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(ConfigurationKey key) {
				return null;
			}

			@Override
			public Long getLong(ConfigurationKey key) {
				return null;
			}

			@Override
			public String[] getStringArray(ConfigurationKey key) {
				return null;
			}

			@Override
			public Map<String, Object> getItems() {
				return null;
			}

			@Override
			public Boolean getBoolean(ConfigurationKey key) {
				return null;
			}

			@Override
			public Date getDate(ConfigurationKey key) {
				return null;
			}

		};

		ConfigurationSource s2 = new ConfigurationSource() {

			@Override
			public void update() {
				// empty
			}

			@Override
			public String getString(ConfigurationKey key) {
				return null;
			}

			@Override
			public int getPrecedence() {
				return 0;
			}

			@Override
			public Integer getInteger(ConfigurationKey key) {
				return null;
			}

			@Override
			public BigDecimal getBigDecimal(ConfigurationKey key) {
				return null;
			}

			@Override
			public Long getLong(ConfigurationKey key) {
				return null;
			}

			@Override
			public String[] getStringArray(ConfigurationKey key) {
				return null;
			}

			@Override
			public Map<String, Object> getItems() {
				return null;
			}

			@Override
			public Boolean getBoolean(ConfigurationKey key) {
				return null;
			}

			@Override
			public Date getDate(ConfigurationKey key) {
				return null;
			}

		};

		List<ConfigurationSource> sources = new ArrayList<>();
		sources.add(s1);
		sources.add(s2);

		ConfigurationSourceComparator csc = new ConfigurationSourceComparator();
		Collections.sort(sources, csc);

		Assert.assertEquals(s1, sources.get(0));

		Assert.assertEquals(-1, csc.compare(s1, s2));// (s1 > s2)
	}

}
