package cz.datalite.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Funkce pro praci s dotazy
 */
public abstract class QueryHelper
{
    /**
     * Prevod vysledku nativniho dotazu na seznam retezcu
     *
     * @param queryResult Vysledek nativniho dotazu
     * @return prevedeny objekt
     */
    public static List<String> convertToStringList(List queryResult)
    {

        if (queryResult == null)
        {
            return null;
        }

        List<String> result = new ArrayList<String>();

        if (queryResult.isEmpty())
        {
            return result;
        }

        for (Vector row : (List<Vector>) queryResult)
        {
            result.add((String) row.get(0));
        }

        return result;
    }
}
