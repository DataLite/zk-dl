package cz.datalite.dao.plsql;

import cz.datalite.helpers.StringHelper;

public class FieldInfo
{
    private String fieldName ;
    private Class<?> type ;

    public FieldInfo(String fieldName, Class<?> type)
    {
        this.fieldName = fieldName;
        this.type = type;
    }

    public boolean isPrimitive()
    {
        return (     ( StringHelper.isEqualsIgnoreCase(fieldName, StoredProcedureInvoker.FIELD_BOOLEAN) )
                || ( StringHelper.isEqualsIgnoreCase( fieldName, StoredProcedureInvoker.FIELD_DATE ) )
                || ( StringHelper.isEqualsIgnoreCase( fieldName, StoredProcedureInvoker.FIELD_STRING ) )
                || ( StringHelper.isEqualsIgnoreCase( fieldName, StoredProcedureInvoker.FIELD_NUMERIC ) ) ) ;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Class<?> getType()
    {
        return type;
    }
}
