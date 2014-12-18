package cz.datalite.helpers.excel.export.poi;

import org.junit.Ignore;
import org.junit.Test;
import org.zkoss.util.media.AMedia;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link cz.datalite.helpers.excel.export.poi.POIExcelExportUtils}
 */
@Ignore
public class POIExcelExportUtilsTest {

	@Test
	public void testExportSimple() throws Exception {

		List<List<POICell>> cells = new LinkedList<>();
		cells.add(Arrays.asList(new POICell("A number"), new POICell("B date"), new POICell("C text")));
		cells.add(Arrays.asList(new POICell(123456), new POICell(new Date()), new POICell("Value C1")));
		cells.add(Arrays.asList(new POICell(123456L), new POICell(null), new POICell("")));
		cells.add(Arrays.asList(new POICell("loooooong"), new POICell(800195258L), new POICell("-1245654")));
		AMedia aMedia = POIExcelExportUtils.exportSimple("report", "sheet", cells);
		File file = File.createTempFile("test", ".xlsx");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(aMedia.getByteData());
		System.out.println("Test Excel file is here: " + file.getAbsoluteFile());
	}
}