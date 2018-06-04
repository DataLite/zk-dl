package cz.datalite.helpers.excel.export.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

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
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		cellStyle.setFont(font);
		cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}
}
