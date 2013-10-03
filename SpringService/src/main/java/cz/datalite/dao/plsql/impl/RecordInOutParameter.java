package cz.datalite.dao.plsql.impl;

import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;

import java.sql.Types;

/**
 * Speciální vstupně-výstupní parametr (PL/SQL record)
 */
class RecordInOutParameter<T> extends SqlInOutParameter implements RecordParameter<T>
{
    /**
     * Cílová entita
     */
    Class<T> targetEntity ;

    /**
     * Zdrojová proměnna
     */
    String variableName ;

    /**
     * Jméno PL/SQL recordu
     */
    String databaseType ;

    /**
     * Příznak zda se jedná o pole
     */
    boolean array ;

    public RecordInOutParameter(String name, Class<T> targetEntity, String variableName, String databaseType )
    {
        this(name, targetEntity, variableName, databaseType, false ) ;
    }

    public RecordInOutParameter(String name, Class<T> targetEntity, String variableName, String databaseType, boolean array )
    {
        super(name, Types.NUMERIC ) ; //TRICK: Tento parametr se nahradí před spuštěnim tak můžu zvolit libovolný typ

        this.targetEntity = targetEntity;
        this.variableName = variableName;
        this.databaseType = databaseType ;
        this.array = array ;
    }

    @Override
    public Class<T> getTargetEntity()
    {
        return targetEntity;
    }

    @Override
    public String getVariableName()
    {
        return variableName;
    }

    @Override
    public String getDatabaseType()
    {
        return databaseType;
    }

    @Override
    public boolean isInput()
    {
        return true ;
    }

    @Override
    public boolean isOutput()
    {
        return true ;
    }

    @Override
    public boolean isArray()
    {
        return array;
    }
}
