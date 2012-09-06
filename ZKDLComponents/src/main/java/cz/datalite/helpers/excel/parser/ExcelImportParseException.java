package cz.datalite.helpers.excel.parser;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class ExcelImportParseException extends ExcelImportException {

    public ExcelImportParseException( final String message ) {
        super( message );
    }

    public ExcelImportParseException( final Throwable cause ) {
        super( cause );
    }
}
