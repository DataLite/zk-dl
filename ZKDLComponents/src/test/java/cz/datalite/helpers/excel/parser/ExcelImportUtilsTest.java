package cz.datalite.helpers.excel.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cz.datalite.helpers.excel.export.ExcelExportUtils;

public class ExcelImportUtilsTest {

	@Test
	public void testConvertStringIndexToInt() {
		assertEquals(0, ExcelImportUtils.convertStringIndexToInt("A"));
		assertEquals(1, ExcelImportUtils.convertStringIndexToInt("B"));
		assertEquals(25, ExcelImportUtils.convertStringIndexToInt("Z"));
		assertEquals(26, ExcelImportUtils.convertStringIndexToInt("AA"));
		assertEquals(27, ExcelExportUtils.convertStringIndexToInt("AB"));
		assertEquals(157, ExcelImportUtils.convertStringIndexToInt("FB"));
	}
	
	@Test
	public void convertIntIndexToString() {
		assertEquals("A", ExcelImportUtils.convertIntIndexToString(0));
		assertEquals("B", ExcelImportUtils.convertIntIndexToString(1));
		assertEquals("Z", ExcelImportUtils.convertIntIndexToString(25));
		assertEquals("AA", ExcelImportUtils.convertIntIndexToString(26));
		assertEquals("AB", ExcelExportUtils.convertIntIndexToString(27));
		assertEquals("FB", ExcelImportUtils.convertIntIndexToString(157));
	}

}
