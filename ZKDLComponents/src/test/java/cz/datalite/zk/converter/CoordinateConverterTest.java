package cz.datalite.zk.converter;

import org.junit.Assert;
import org.junit.Test;

public class CoordinateConverterTest {

	@Test
	public void testConvertDToDMS() throws Exception {
		Assert.assertEquals("49"+CoordinateConverter.SYMBOL_DEGREE+"55.41445'", CoordinateConverter.convertDToDMS(49.9235742, CoordinateConverter.STYLE_DM));
		Assert.assertEquals("49"+CoordinateConverter.SYMBOL_DEGREE+"55'24.867\"", CoordinateConverter.convertDToDMS(49.9235742, CoordinateConverter.STYLE_DMS));
	}

	@Test
	public void testConvertDMSToD() throws Exception {
		Assert.assertEquals(49.9235742, CoordinateConverter.convertDMSToD("49"+CoordinateConverter.SYMBOL_DEGREE+"55.41445'", CoordinateConverter.STYLE_DM), 0.5);
		Assert.assertEquals(49.9235742, CoordinateConverter.convertDMSToD("49"+CoordinateConverter.SYMBOL_DEGREE+"55'24.867\"", CoordinateConverter.STYLE_DMS), 0.5);
	}
}