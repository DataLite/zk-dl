package cz.datalite.helpers;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Funkce pro praci s retezci
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class StringHelper
{
    /**
     * Standardni trim rozsireny o tvrdou mezeru a podobne divne mezery (http://www.unicode.org/charts/PDF/U2000.pdf).
     * <p>
     * podle Character.isWhitespace neni 160 mezera, proto pouzijeme isSpaceChar
     * <p>
     * \u0020 isSpaceChar=true isWhitespace=true
     * \u00a0 isSpaceChar=true isWhitespace=false
     * \u1680 isSpaceChar=true isWhitespace=true
     * \u180e isSpaceChar=true isWhitespace=true
     * \u2000 isSpaceChar=true isWhitespace=true
     * \u2001 isSpaceChar=true isWhitespace=true
     * \u2002 isSpaceChar=true isWhitespace=true
     * \u2003 isSpaceChar=true isWhitespace=true
     * \u2004 isSpaceChar=true isWhitespace=true
     * \u2005 isSpaceChar=true isWhitespace=true
     * \u2006 isSpaceChar=true isWhitespace=true
     * \u2007 isSpaceChar=true isWhitespace=false
     * \u2008 isSpaceChar=true isWhitespace=true
     * \u2009 isSpaceChar=true isWhitespace=true
     * \u200a isSpaceChar=true isWhitespace=true
     * \u202f isSpaceChar=true isWhitespace=false
     * \u205f isSpaceChar=true isWhitespace=true
     * \u3000 isSpaceChar=true isWhitespace=true
     * <p>
     *
     * @param value     hodnota
     * @return hodnota s odstranenimy mezerami
     */
    public static String trim( String value ) {
        if(value == null) return null;
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();    /* avoid getfield opcode */

        // podle isWhitespace neni 160 mezera, proto isSpaceChar
        while ((st < len) && (val[st] <= ' ' || Character.isSpaceChar(val[st]))) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ' || Character.isSpaceChar(val[len - 1]))) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }
    /**
     * Vrací true, pokud je string null nebo prázdný
     *
     * @param value Testovany objekt
     * @return true pokud je objekt NULL prázdný
     */
    public static boolean isNull(String value)
    {
        return ((value == null) || ("".equals(value)));
    }

    /**
     * @param key1 Prvni retezec
     * @param key2 Druhy retezec
     * @return true        pokud jsou retezce shodne
     */
    public static boolean isEquals(String key1, String key2)
    {
        return ((isNull(key1) && isNull(key2)) ||
                (
                        (!isNull(key1))
                                && (!isNull(key2))
                                && (key1.equals(key2))));
    }

    /**
     * @param key1 Prvni retezec
     * @param key2 Druhy retezec
     * @return true        pokud jsou retezce shodne
     */
    public static boolean isEqualsIgnoreCase(String key1, String key2)
    {
        return ((isNull(key1) && isNull(key2)) ||
                (
                        (!isNull(key1))
                                && (!isNull(key2))
                                && (key1.equalsIgnoreCase(key2))));
    }

    /**
     * Zjistí, zda je v stringu uložená číselná hodnota (pokusí se jej převést na BigDecimal).
     * 
     * @param attributeValue Testovana hodnota
     * @return true     POkud se jedna o ciselnou hodnotu
     */
    public static boolean isNumeric(String attributeValue)
    {
        try
        {
            new BigDecimal(attributeValue);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Prevod objektu na retezec
     *
     * @param value prevadena hodnota
     * @return prevedena hodnota
     */
    public static String fromObject(Object value)
    {
        return (value != null) ? value.toString() : null;
    }

    /**
     * Prevod retezce na datum
     *
     * @param date Prevadene datum
     * @return prevedene datum
     */
    public static String fromTimestamp(Timestamp date)
    {
        if (date != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.ENGLISH);

            return sdf.format(date);
        }
        return null;
    }


    /**
     * Rozdeli vyraz expr podle separatoru a vraci list hodnot
     *
     * @param expr      retezec k rozdeleni
     * @param separator separator
     * @return seznam hodnot
     */
    public static List<String> splitString( String expr, String separator )
    {
        if ( expr == null )
        {
            return null ;
        }

        List results = new ArrayList(6) ;
        String[] array = expr.split( separator ) ;

        for( String item : array )
        {
            String v = item.trim() ;

            if ( v.length() > 0 )
            {
                results.add( v ) ;
            }
        }

        return results ;
    }


    /**
     * Prevod retezce na A/N
     *
     * @param value      Prevadena hodnota
     * @param trueValue  Hodnota pro A
     * @param falseValue Hodnota pro N
     * @return prevedena hodnota
     */
    public static char toAN(String value, String trueValue, String falseValue)
    {
        if (StringHelper.isEqualsIgnoreCase(trueValue, value))
        {
            return 'A';
        }
        else if (StringHelper.isEqualsIgnoreCase(falseValue, value))
        {
            return 'N';
        }
        return '\0';
    }

    /**
     * Prevod retezec na cislo
     *
     * @param value prevadena hodnota
     * @return prevedena hodnota
     */
    public static int toInt(String value)
    {
        return Integer.parseInt(value);
    }

    /**
     * Spojeni retezcu pokud nejsou null
     *
     * @param separator Oddelovac
     * @param value1    Prvni hodnota
     * @param value2    Druha hodnota
     * @return spojeny retezec
     */
    public static String notNullConcat(String separator, String value1, String value2)
    {
        return notNullConcat(separator, new String[]{value1, value2});
    }

    /**
     * Spojeni retezcu pokud nejsou null
     *
     * @param separator Oddelovac
     * @param values    Spojovane hodnoty
     * @return spojeny retezec
     */
    public static String notNullConcat(String separator, String[] values)
    {
        StringBuffer b = new StringBuffer();

        for (String value : values)
        {
            if (!StringHelper.isNull(value))
            {
                if ((b.length() > 0) && (!StringHelper.isNull(separator)))
                {
                    b.append(separator);
                }
                b.append(value);
            }
        }

        return b.toString();
    }

    /**
     * Prevod na cislo
     *
     * @param value prevadeny retezec
     * @return prevedeny retezec
     */
    public static BigDecimal toBigDecimal(String value)
    {
        if (StringHelper.isNull(value))
        {
            return null;
        }

        return new BigDecimal(value);
    }

    /**
     * Funkce vraci prvni ne NULL vyraz
     *
     * @param key1 prvni vyraz
     * @param key2 druhy vyraz
     * @return vyraz
     */
    public static String nvl(String key1, String key2)
    {
        return (StringHelper.isNull(key1)) ? key2 : key1;
    }

	public static String abbreviate(String text) {
		return StringUtils.abbreviate(text, 0, 20);
	}
}
