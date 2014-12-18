package cz.datalite.helpers.excel.export.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.AMedia;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>Metoda usnadňuje exportování dat do excelu, soubor XLS. Využívá ke své práci
 * knihovnu Apache POI. Umožňuje uivateli využít zcela automatický exportSimple stejně jako
 * použít pouze pomocné metody a celkový postup si udělat dle sebe.</p>
 *
 * @author Martin Peterka
 */
public final class POIExcelExportUtils {

	/**
	 * Nelze vytvořit instanci třídy
	 */
	private POIExcelExportUtils() {
	}


	/**
	 * <p>Uzavře sešit a vráti AMedia objekt</p>
	 *
	 * @param workbook   sešit, který chceme uzavřít
	 * @param os         stream, do kterého se zapisovala data
	 * @param exportName název souboru pro exportSimple
	 * @return vygenerovaný AMedia připravený ke stažení
	 * @throws java.io.IOException
	 */
	public static AMedia getAMediaOutput(final Workbook workbook, final ByteArrayOutputStream os, final String exportName) throws IOException {
		if (os == null) {
			return null;
		}

		workbook.write(os);
		final InputStream is = new ByteArrayInputStream(os.toByteArray());
		return new AMedia(exportName + ".xlsx", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
	}


	/**
	 * <p>Metoda usnadňuje exportování dat do excelu, soubor XLS.
	 * Jako výsledek vrací hotový {@link org.zkoss.util.media.AMedia},
	 * který lze například zpřístupnit uživateli přes
	 * {@link org.zkoss.zul.Filedownload}.</p>
	 *
	 * @param reportName název souboru který se generuje
	 * @param sheetName  název listu
	 * @param cells      seznam řádků se sloupci
	 * @return hotový AMedia připravený ke stažení
	 * @throws java.io.IOException
	 */
	public static AMedia exportSimple(final String reportName, final String sheetName, final List<List<POICell>> cells) throws IOException {

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Workbook workbook = createWorkbook();
		return exportSimple(reportName, sheetName, cells, os, workbook);
	}

	public static AMedia exportSimple(String reportName, String sheetName, List<List<POICell>> cells, ByteArrayOutputStream os, Workbook workbook) throws IOException {
		final Sheet sheet = insertSheet(workbook, sheetName);

		for (int rowNum = 0; rowNum < cells.size(); rowNum++) {
			Row row = sheet.createRow(rowNum);
			List<POICell> cellRow = cells.get(rowNum);
			for (int colNum = 0; colNum < cellRow.size(); colNum++) {
				POICell poiCell = cellRow.get(colNum);
				createCell(row, poiCell, colNum, workbook);
			}
		}

		// ukončení práce se sešitem
		return getAMediaOutput(workbook, os, reportName);
	}

	/**
	 * Create cell, setup  right format...
	 *
	 * @param row
	 * @param poiCell
	 * @param colNum
	 * @param workbook
	 * @return
	 */
	public static Cell createCell(Row row, POICell poiCell, int colNum, Workbook workbook) {
		Cell cell = row.createCell(colNum, poiCell.getType());

		if (poiCell.getValue() == null) {
			cell.setCellType(poiCell.getType());
			return cell;
		}

		if (poiCell.getValue() instanceof Boolean) {
			cell.setCellValue((Boolean) poiCell.getValue());
			cell.setCellType(poiCell.getType());
			return cell;
		}
		if (poiCell.getValue() instanceof Double) {
			cell.setCellValue((Double) poiCell.getValue());
			cell.setCellType(poiCell.getType());
			return cell;
		}
		if (poiCell.getValue() instanceof Date) {
			cell.setCellValue((Date) poiCell.getValue());
			cell.setCellStyle(POICellStyles.dateTime(workbook));
			return cell;
		}
		if (poiCell.getValue() instanceof Calendar) {
			cell.setCellValue((Calendar) poiCell.getValue());
			return cell;
		}
		if (poiCell.getValue() instanceof String) {
			cell.setCellValue((String) poiCell.getValue());
			cell.setCellType(poiCell.getType());
			return cell;
		}

		cell.setCellValue(poiCell.getValue().toString());

		return cell;
	}

	/**
	 * Autosize all columns
	 *
	 * @param sheet actual sheet
	 * @param cols  number of columns
	 * @see Sheet#autoSizeColumn(int)
	 */
	public static void autoSizeColumns(Sheet sheet, int cols) {
		for (int i = 0; i <= cols; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * Inserts sheet into workbook
	 *
	 * @param workbook
	 * @param sheetName
	 * @return
	 */
	public static Sheet insertSheet(Workbook workbook, String sheetName) {
		return workbook.createSheet(sheetName);
	}

	/**
	 * Creates workbook
	 *
	 * @return
	 */
	public static Workbook createWorkbook() {
		return new XSSFWorkbook();
	}


}
