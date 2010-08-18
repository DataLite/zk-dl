package cz.datalite.helpers.excel.parser;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ExcelImportCellTypeException extends ExcelImportException {

    public ExcelImportCellTypeException( final String message ) {
        super( message );
    }

    public ExcelImportCellTypeException( final Throwable cause ) {
        super( cause );
    }
}
