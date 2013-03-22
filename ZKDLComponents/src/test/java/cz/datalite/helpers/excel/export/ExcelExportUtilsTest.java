package cz.datalite.helpers.excel.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExcelExportUtilsTest {

	@Test
	public void testConvertStringIndexToInt() {
		assertEquals(0, ExcelExportUtils.convertStringIndexToInt("A"));
		assertEquals(1, ExcelExportUtils.convertStringIndexToInt("B"));
		assertEquals(25, ExcelExportUtils.convertStringIndexToInt("Z"));
		assertEquals(26, ExcelExportUtils.convertStringIndexToInt("AA"));
		assertEquals(27, ExcelExportUtils.convertStringIndexToInt("AB"));
		assertEquals(157, ExcelExportUtils.convertStringIndexToInt("FB"));
	}
	
	@Test
	public void convertIntIndexToString() {
		assertEquals("A", ExcelExportUtils.convertIntIndexToString(0));
		assertEquals("B", ExcelExportUtils.convertIntIndexToString(1));
		assertEquals("Z", ExcelExportUtils.convertIntIndexToString(25));
		assertEquals("AA", ExcelExportUtils.convertIntIndexToString(26));
		assertEquals("AB", ExcelExportUtils.convertIntIndexToString(27));
		assertEquals("FB", ExcelExportUtils.convertIntIndexToString(157));
	}

}
