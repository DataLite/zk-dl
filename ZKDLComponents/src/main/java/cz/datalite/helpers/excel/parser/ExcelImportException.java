package cz.datalite.helpers.excel.parser;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ExcelImportException extends Exception {

    public ExcelImportException( final Throwable cause ) {
        super( cause );
    }

    public ExcelImportException( final String message ) {
        super( message );
    }

    public ExcelImportException( final String message, final Throwable cause ) {
        super( message, cause );
    }
}
