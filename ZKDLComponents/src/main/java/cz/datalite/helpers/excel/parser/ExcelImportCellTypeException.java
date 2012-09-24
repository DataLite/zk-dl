package cz.datalite.helpers.excel.parser;

/**
 *
 * @author Karel Cemus
 */
public class ExcelImportCellTypeException extends ExcelImportException {

    public ExcelImportCellTypeException( final String message ) {
        super( message );
    }

    public ExcelImportCellTypeException( final Throwable cause ) {
        super( cause );
    }
}
