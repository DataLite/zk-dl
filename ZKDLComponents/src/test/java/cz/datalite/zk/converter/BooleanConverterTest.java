package cz.datalite.zk.converter;

import org.junit.Test;
import org.zkoss.bind.BindContextMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link cz.datalite.zk.converter.BooleanConverter}
 */
public class BooleanConverterTest {

	@Test
	public void testCoerceToUi() throws Exception {
		BindContextMock bindContext = new BindContextMock();
		assertNull(new BooleanConverter().coerceToUi(null, null, bindContext));
		assertEquals("True", new BooleanConverter().coerceToUi(Boolean.TRUE, null, bindContext));
		assertEquals("False", new BooleanConverter().coerceToUi(Boolean.FALSE, null, bindContext));
		assertEquals("False", new BooleanConverter().coerceToUi(new Object(), null, bindContext));
		bindContext.putConverterArgs("yesLabel", "Yes");
		bindContext.putConverterArgs("noLabel", "No");
		bindContext.putConverterArgs("nilLabel", "None");
		assertEquals("Yes", new BooleanConverter().coerceToUi(Boolean.TRUE, null, bindContext));
		assertEquals("No", new BooleanConverter().coerceToUi(Boolean.FALSE, null, bindContext));
		assertEquals("None", new BooleanConverter().coerceToUi(null, null, bindContext));
	}

	@Test
	public void testCoerceToBean() throws Exception {
		assertNull(new BooleanConverter().coerceToBean(null, null, null));
		assertNull(new BooleanConverter().coerceToBean("yes", null, null));
	}
}