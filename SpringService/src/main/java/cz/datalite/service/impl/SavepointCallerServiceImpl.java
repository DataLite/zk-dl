package cz.datalite.service.impl;

import cz.datalite.service.SavepointCallerService;
import cz.datalite.service.SavepointOperation;
import cz.datalite.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@SuppressWarnings("UnusedDeclaration")
@Service
public class SavepointCallerServiceImpl implements SavepointCallerService
{
    @SuppressWarnings("unchecked")
    @Override
    @Transactional( propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class )
    public void doExecute( @NotNull SavepointOperation operation )
    {
        checkException( operation ) ;
    }

    /**
     * Spuštění příkazu s odchicením vyjímky
     *
     * @param operation     spouštěná operace
     */
    private void checkException( @NotNull SavepointOperation operation )
    {
        try
        {
            operation.doOperation() ;
        }
        catch ( Exception e )
        {
            throw new Error( e ) ;
        }
    }
}
