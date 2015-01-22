package cz.datalite.service;

/**
 * Spouštěná operace v samostatné transakci
 */
public interface SavepointOperation
{
    /**
     * Spouštěná operace
     */
    @SuppressWarnings("UnusedDeclaration")
    void doOperation() throws Exception ;
}
