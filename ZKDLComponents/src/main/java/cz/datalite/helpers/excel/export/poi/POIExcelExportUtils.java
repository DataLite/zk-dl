package cz.datalite.helpers.excel.export.poi;

import cz.datalite.helpers.excel.export.ExportedFile;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.list.window.controller.ListboxExportManagerController;
import cz.datalite.zk.converter.ZkConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Metoda usnadňuje exportování dat do excelu, soubor XLS. Využívá ke své práci
 * knihovnu Apache POI. Umožňuje uivateli využít zcela automatický exportSimple stejně jako
 * použít pouze pomocné metody a celkový postup si udělat dle sebe.</p>
 *
 * @author Martin Peterka
 */
@SuppressWarnings("Duplicates")
public final class POIExcelExportUtils {

	protected static final Logger LOGGER = LoggerFactory.getLogger(POIExcelExportUtils.class);

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
	public static ExportedFile getExportedFile(final Workbook workbook, final ByteArrayOutputStream os, final String exportName) throws IOException {
		if (os == null) {
			return null;
		}

		workbook.write(os);
		final InputStream is = new ByteArrayInputStream(os.toByteArray());
		return new ExportedFile(exportName + ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", is);
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
	public static ExportedFile exportSimple(final String reportName, final String sheetName, final List<List<POICell>> cells) throws IOException {

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final Workbook workbook = createWorkbook();
		return exportSimple(reportName, sheetName, cells, os, workbook);
	}

	public static ExportedFile exportSimple(String reportName, String sheetName, List<List<POICell>> cells, ByteArrayOutputStream os, Workbook workbook) throws IOException {
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
		return getExportedFile(workbook, os, reportName);
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


	private static void headerStyle(List<POICell> row, Workbook workbook)
	{
		CellStyle cellStyle = POICellStyles.headerCellStyle(workbook);
		for (POICell cell : row)
		{
			cell.setStyle(cellStyle);
		}
	}

	/**
	 * Generovani dat pro export
	 *
	 * @param model                 sloupce
	 * @param rows                  počet řádků
	 * @param masterController      kontroler
	 * @return data pro export
	 */
	private static List<List<POICell>> prepareSource(List<Map<String, Object>> model, int rows, DLListboxExtController masterController, Integer[] exportedRows  )
	{

		List<List<POICell>> result = new LinkedList<>();

		final List<POICell> heads = new ArrayList<>();

		// list of columns that need to be visible only for the purpose of export
		// (listbox controller may skip hidden columns for performance reasons, so we need to make them "visible" and hide them back in the end of export)
		final List<DLColumnUnitModel> hideOnFinish = new LinkedList<>();


		for (Map<String, Object> unit : model)
		{
			//noinspection unchecked
			heads.add(new POICell(unit.get("label")));
		}

		// load data
		List data;
		try
		{
			// ensure, that column is visible in the model (is hidden if the user has added it only for export)
			for (Map<String, Object> unit : model)
			{
				DLColumnUnitModel columnUnitModel = masterController.getModel().getColumnModel().getColumnModel((Integer) unit.get("index") + 1);
				if (!columnUnitModel.isVisible())
				{
					columnUnitModel.setVisible(true);
					hideOnFinish.add(columnUnitModel);
				}
			}

			// and load data
			int exportMaxRows = ListboxExportManagerController.exportMaxRows;

			data = masterController.loadData( Math.min(  ( (rows == 0) ?  exportMaxRows : Math.min(rows, exportMaxRows) ), 1048575 ) ).getData() ;
		}
		finally
		{
			// after processing restore previous state
			for (DLColumnUnitModel hide : hideOnFinish)
			{
				hide.setVisible(false);
			}
		}

		if ( ( exportedRows != null ) && (exportedRows.length >= 1 ) )
		{
			exportedRows[0] = data.size();
		}

		if ( masterController.getListbox().getAttribute("disableExcelExportHeader") == null)
		{
			result.add(heads);
		}

		for (Object entity : data)
		{
			final List<POICell> row = new LinkedList<>();
			for (Map<String, Object> unit : model)
			{
				try
				{
					final String columnName = (String) unit.get("column");

					Object value;

					if (entity instanceof Map)
					{
						value = ((Map) entity).get(columnName);
					}
					else
					{
						value = (Strings.isEmpty(columnName)) ? entity : Fields.getByCompound(entity, columnName);
					}

					Class columnType = (Class) unit.get("columnType");
					if (value != null && columnType != null)
					{
						try
						{
							value = Classes.coerce(columnType, value);
						}
						catch (ClassCastException e)
						{
							LOGGER.trace("Unable to convert export value {} to columnType {} - {}.", value, columnType, e);
						}
					}

					if ((Boolean) unit.get("isConverter"))
					{
						ZkConverter converter = (ZkConverter) unit.get("converter");
						value = converter.convertToView(value);
					}

					//noinspection unchecked
					row.add(new POICell(value));
				}
				catch (Exception ex)
				{ // ignore
					LOGGER.warn("Error occured during exporting column '{}'.", unit.get("column"), ex);
				}
			}
			result.add(row);

		}
		return result;
	}
}
