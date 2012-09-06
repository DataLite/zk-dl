package cz.datalite.helpers;

import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Funkce pro praci se zdroji
 */
public abstract class ResourceHelper
{
    /**
     * Aktualni zdroje
     */
    private static ResourceBundle resource;


    /**
     * Funkce pro ziskani textu z resource
     *
     * @param key Pozdovany text
     * @return Hodnota z resoursou
     */
    public static String getString(String key)
    {
        if (resource != null)
        {
            try
            {
                String value;

                value = resource.getString(key);
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");

                return value;
            }
            catch (MissingResourceException e)
            {
                /* empty */
            }
            catch (UnsupportedEncodingException e)
            {
                /* empty */
            }
        }
        return null;
    }


    /**
     * Nastaveni zdroju
     *
     * @param resource Nove zdroje
     */
    public static void setResource(ResourceBundle resource)
    {
        ResourceHelper.resource = resource;
    }

    /**
     * @return Aktualni zdroje
     */
    public static ResourceBundle getResource()
    {
        return resource;
    }
}
