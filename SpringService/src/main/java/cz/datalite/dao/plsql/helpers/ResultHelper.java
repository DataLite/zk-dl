package cz.datalite.dao.plsql.helpers;

import cz.datalite.helpers.StringHelper;

public final class ResultHelper
{
    private ResultHelper()
    {
    }

    /**
     * @param result    výsledek funkce
     * @return true pokud je vysledek OK
     */
    public static boolean isOk( String  result )
    {
        return ( ( StringHelper.isEqualsIgnoreCase(result, "Ok") ) || ( result != null ) && ( result.toUpperCase().startsWith( "OK -" ) ) ) ;
    }
}
