package cz.datalite.localization;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test pro {@link cz.datalite.localization.LocalizationUtil}
 */
public class LocalizationUtilTest {

	@Test
	public void testGetEnumLocalizationKey() throws Exception {
		Assert.assertEquals("cz.datalite.localization.TestEnumPlain.FOO", LocalizationUtil.getEnumLocalizationKey(TestEnumPlain.FOO));
		try {
			LocalizationUtil.getEnumLocalizationKey(null);
			fail("Should fail on NPE");
		} catch (NullPointerException expexted) {
			//expected
		}
		Assert.assertEquals("foo.bar", LocalizationUtil.getEnumLocalizationKey(TestEnum.FOO));
	}
}
