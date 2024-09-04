package cz.datalite.service;

/**
 * Služba pro spuštění kodu v samostatné transakci
 */
@SuppressWarnings("UnusedDeclaration")
public interface SavepointCallerService
{
    /**
     * @param operation     spouštěná operace
     * @return výsledek operace
     */
    @SuppressWarnings("unchecked")
    void doExecute(SavepointOperation operation ) ;

    /**
     * @param operation     spouštěná operace
     * @return výsledek operace
     */
    @SuppressWarnings("unchecked")
    void doExecuteWithDefaultTimeout( SavepointOperation operation ) ;
}
