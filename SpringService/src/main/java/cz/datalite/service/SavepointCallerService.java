package cz.datalite.service;

import javax.validation.constraints.NotNull;

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
    void doExecute( @NotNull SavepointOperation operation ) ;

    /**
     * @param operation     spouštěná operace
     * @return výsledek operace
     */
    @SuppressWarnings("unchecked")
    void doExecuteWithDefaultTimeout( @NotNull SavepointOperation operation ) ;
}
