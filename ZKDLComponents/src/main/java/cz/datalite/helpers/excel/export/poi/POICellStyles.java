package cz.datalite.helpers.excel.export.poi;

import org.apache.poi.ss.usermodel.*;

/**
 *
 */
public class POICellStyles {

	public static CellStyle dateTime(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		CreationHelper creationHelper = wb.getCreationHelper();
		DataFormat dataFormat = creationHelper.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat("d.M.yyyy HH:mm"));
		return cellStyle;
	}

	public static CellStyle date(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		CreationHelper creationHelper = wb.getCreationHelper();
		DataFormat dataFormat = creationHelper.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat("d.M.yyyy"));
		return cellStyle;
	}

	public static CellStyle headerCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeight((short) 700);
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFont(font);
		cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}
}
