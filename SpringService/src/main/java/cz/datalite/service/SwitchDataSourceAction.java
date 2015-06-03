package cz.datalite.service;

/**
 * Akce, která se má spustit po se změnou datového zdroje
 *
 * @param <ResultType>  typ výsledku akce
 */
public interface SwitchDataSourceAction<ResultType>
{
    /**
     * @return výsledek spuštění
     */
    ResultType execute() ;
}
