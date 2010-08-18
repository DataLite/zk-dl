package cz.datalite.helpers.excel.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ExcelImportUtilsTest {

    @Test
    public void testConvertStringIndexToInt() {
        assertEquals( "Nesprávná konverze", 0, ExcelImportUtils.convertStringIndexToInt( "A" ) );
        assertEquals( "Nesprávná konverze", 1, ExcelImportUtils.convertStringIndexToInt( "B" ) );
        assertEquals( "Nesprávná konverze", 26, ExcelImportUtils.convertStringIndexToInt( "AA" ) );
        assertEquals( "Nesprávná konverze", 53, ExcelImportUtils.convertStringIndexToInt( "BB" ) );
    }

    @Test
    public void testConvertIntIndexToString() {
        assertEquals( "Nesprávná konverze", "A", ExcelImportUtils.convertIntIndexToString( 0 ) );
        assertEquals( "Nesprávná konverze", "B", ExcelImportUtils.convertIntIndexToString( 1 ) );
        assertEquals( "Nesprávná konverze", "AA", ExcelImportUtils.convertIntIndexToString( 26 ) );
        assertEquals( "Nesprávná konverze", "BB", ExcelImportUtils.convertIntIndexToString( 53 ) );
    }
}
