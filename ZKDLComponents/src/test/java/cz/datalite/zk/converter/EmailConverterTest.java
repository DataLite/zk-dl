package cz.datalite.zk.converter;

import org.junit.Test;
import org.zkoss.zk.ui.WrongValueException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Unit test for {@link EmailConverter}
 */
public class EmailConverterTest {

	@Test
	public void testCoerceToUi() throws Exception {
		EmailConverter converter = new EmailConverter();
		assertNull(converter.coerceToUi(null, null, null));
		assertEquals("", converter.coerceToUi("", null, null));
		assertEquals("a@b.cz", converter.coerceToUi("a@b.cz", null, null));
		assertEquals("a@b.cz;de@f.com", converter.coerceToUi("a@b.cz;de@f.com", null, null));
		assertEquals("a@b.cz;de@f.com\nfoo@bar.cz", converter.coerceToUi("a@b.cz;de@f.com\n" +
				"foo@bar.cz", null, null));
	}

	@Test
	public void testCoerceToBean() throws Exception {
		assertNull(EmailConverter.coerceToBean(null, null));
		assertEquals("", EmailConverter.coerceToBean("", null));
		assertEquals("a@b.cz", EmailConverter.coerceToBean("a@b.cz", null));
		assertEquals("a@b.cz;d@e.fz", EmailConverter.coerceToBean("a@b.cz;d@e.fz", null));
		assertEquals("a@b.cz;d@e.fz", EmailConverter.coerceToBean("a@b.cz,d@e.fz", null));
		assertEquals("A@B.CZ;D@E.FZ", EmailConverter.coerceToBean("A@B.CZ;D@E.FZ", null));
		assertEquals("a@b.cz;d@e.fz", EmailConverter.coerceToBean("a@b.cz\r\nd@e.fz", null));
		assertEquals("a@b.cz;d@e.fz", EmailConverter.coerceToBean("a@b.cz\nd@e.fz", null));
		assertEquals("a@b.cz;d@e.fz", EmailConverter.coerceToBean("a@b.cz\rd@e.fz", null));
		assertEquals("a@b.cz;d@e.fz;g@h.cz", EmailConverter.coerceToBean("a@b.cz;d@e.fz;g@h.cz", null));

		assertEquals("a@b.cz", EmailConverter.coerceToBean("a@b.cz ", null));
		assertEquals("A@B.CZ", EmailConverter.coerceToBean("A@B.CZ ", null));
		assertEquals("a@b.cz", EmailConverter.coerceToBean("a@b.cz \n", null));
		assertEquals("a@b.cz", EmailConverter.coerceToBean("a@b.cz \r\n", null));
		assertEquals("a@b.cz;f@c.cz", EmailConverter.coerceToBean("a@b.cz \r\n f@c.cz", null));


	}

	@Test
	public void testCoerceToBeanValidation() throws Exception {
		try {
			EmailConverter.coerceToBean("a-b-cz", null);
			fail();
		} catch (WrongValueException ignored) {}

		try {
			EmailConverter.coerceToBean("a-b-cz;a@b.cz", null);
			fail();
		} catch (WrongValueException ignored) {}
		try {
			EmailConverter.coerceToBean("a-b-cz\na@b.cz", null);
			fail();
		} catch (WrongValueException ignored) {}
	}
}