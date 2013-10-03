package cz.datalite.dao.plsql;

import java.util.HashMap;
import java.util.Map;

/**
 * Definice výsledku uložené procedur
 */
public class StoredProcedureResult extends HashMap<String, Object>
{
    public StoredProcedureResult(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public StoredProcedureResult(int initialCapacity)
    {
        super(initialCapacity);
    }

    public StoredProcedureResult()
    {
    }

    public StoredProcedureResult(Map<? extends String, ?> m)
    {
        super(m);
    }
}
