package cz.datalite.service.impl;

import cz.datalite.service.SavepointCallerService;
import cz.datalite.service.SavepointOperation;
import cz.datalite.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@SuppressWarnings("UnusedDeclaration")
@Service
public class SavepointCallerServiceImpl implements SavepointCallerService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(SavepointCallerService.class) ;

    @Override
    @Transactional( propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class, timeout = 6000 )
    public void doExecute( @NotNull SavepointOperation operation )
    {
        checkException( operation ) ;
    }

    @Override
    @Transactional( propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class )
    public void doExecuteWithDefaultTimeout(@NotNull SavepointOperation operation)
    {
        checkException( operation ) ;
    }

    /**
     * Spuštění příkazu s odchicením vyjímky
     *
     * @param operation     spouštěná operace
     */
    private void checkException( @NotNull SavepointOperation operation ) {
        try {
            operation.doOperation() ;
        } catch ( Exception e ) {
            if ( LOGGER.isErrorEnabled() ) {
                LOGGER.error( "Chyba při zpracování transakce", e ) ;
            }
            throw new Error( e ) ;
        }
    }
}
