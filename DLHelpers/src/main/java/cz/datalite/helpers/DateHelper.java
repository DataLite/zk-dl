package cz.datalite.helpers;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Funkce pro praci s datem
 */
public class DateHelper
{
    private static final Integer offset = TimeZone.getDefault().getRawOffset();

    /**
     * Detaultni format "d.M.yyyy"
     */
    public static final String DEFAULT_DATE_FORMAT = "d.M.yyyy";
    public static final String DEFAULT_TIME_FORMAT = "H:m";
    /**
     * Den v milisekundach
     */
    public static final int DAY_IN_MS = 86400000;

    /**
     * Prevod retezce na datum
     *
     * @param date Prevadene datum
     * @return prevedene datum
     */
    public static Timestamp fromString(String date)
    {
        if ((date != null) && (!"".equalsIgnoreCase(date)))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.ENGLISH);

            try
            {
                return new Timestamp(sdf.parse(date).getTime());
            }
            catch (ParseException e)
            {
                /* empty */
            }
        }
        return null;
    }

    /**
     * @return Aktualni cas
     */
    public static Timestamp now()
    {
        return new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * Vrací absolutní hodnotu rozdílu
     * @param datum1
     * @param datum2
     * @return the difference
     */
    public static Long dateDiff(Date datum1, Date datum2) {
        if (datum1 == null || datum2 == null) {
            return null;
        }
        return Math.abs(datum1.getTime() - datum2.getTime());
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * vraci hodnotu SE ZNAMENKEM
     * @param datum1
     * @param datum2
     * @return the difference
     */
    public static Long dateDiffSigned(Date datum1, Date datum2) {
        if (datum1 == null || datum2 == null) {
            return null;
        }
        return datum1.getTime() - datum2.getTime();
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * Vrací absolutní hodnotu rozdílu
     * @param datum1
     * @param diff ms
     * @return the difference
     */
    public static Long dateDiffUnsigned(Date datum1, Integer diff) {
        return dateDiffUnsigned(datum1, diff.longValue());
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * Vrací absolutní hodnotu rozdílu
     * @param datum1
     * @param diff ms
     * @return the difference
     */
    public static Long dateDiffUnsigned(Date datum1, long diff) {
        if (datum1 == null) {
            return null;
        }
        return Math.abs(datum1.getTime() - diff);
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * Vrací absolutní hodnotu rozdílu
     * @param datum1
     * @param diff
     * @return the difference
     * @deprecated
     */
    @Deprecated
    public static Long dateDiff(Date datum1, Integer diff) {
        return dateDiffUnsigned(datum1, diff);
    }

    /**
     * Spocita rozdil dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * Vrací absolutní hodnotu rozdílu
     * @param datum1
     * @param diff ms
     * @return the difference
     * @deprecated
     */
    @Deprecated
    public static Long dateDiff(Date datum1, long diff) {
        return dateDiffUnsigned(datum1, diff);
    }

    /**
     * Spocita soucet dvou dat, vysledek v milisekundach
     * Bere ohled na casove pasmo
     * @param datum1
     * @param datum2
     * @return the difference
     */
    public static Long dateSum(Date datum1, Date datum2) {
        if (datum1 == null || datum2 == null) {
            return null;
        }
        return datum1.getTime() + datum2.getTime() + offset;
    }

    /**
     * K datu pricte dany pocet milisekund
     * Bere ohled na casove pasmo
     * @param datum
     * @param ms milisekundy
     * @return the result
     */
    public static Long dateSum(Date datum, Integer ms) {
        return dateSum(datum, ms.longValue());
    }

    /**
     * K datu pricte dany pocet milisekund
     * Bere ohled na casove pasmo
     * @param datum
     * @param ms milisekundy
     * @return the result
     */
    public static Long dateSum(Date datum, long ms) {
        if (datum == null) {
            return null;
        }
        return datum.getTime() + ms;
    }

    /**
     * Prevede datum na String ve vychozim formatu dle promenne DEFAULT_DATE_FOTMAT
     * @param dt Datum
     * @return the result
     */
    public static String dateToString(Date dt) {
        return dateToString(dt, DEFAULT_DATE_FORMAT);
    }

    /**
     * Prevede datum na String ve formatu v promenne format
     * @param dt
     * @param format
     * @return the result
     * @throws java.lang.IllegalArgumentException
     */
    public static String dateToString(Date dt, String format) throws IllegalArgumentException {
        if (dt == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(dt);
    }

    /**
     * Ziska z data pouze casovou slozku, zbytek odstrani (vynuluje)
     * Zachovava cas ve vychozi presnosti dle promenne DEFAULT_TIME_FORMAT
     * @param time cas
     * @return the result
     * @throws java.text.ParseException
     */
    public static Date getTime(Date time) throws ParseException {
        return getTime(time, DEFAULT_TIME_FORMAT);
    }

    /**
     * Ziska z data pouze casovou slozku, zbytek odstrani (vynuluje)
     * Presnost je dana formatem v promenne format. (Napr.: 'H:m:s')
     * @param time
     * @param format
     * @return the result
     * @throws java.text.ParseException
     */
    public static Date getTime(Date time, String format) throws ParseException {
        if (time == null) {
            return null;
        }
        return new SimpleDateFormat(format).parse(new SimpleDateFormat(format).format(time));
    }

    /**
     * Vraci cislo dne od date(0);
     * Zohlednuje casovou zonu
     * Vypocet dle poctu ms
     * @param date
     * @return the result
     */
    public static Integer getDay(Date date) {
        if (date == null) {
            return null;
        }
        //return Integer.parseInt(new SimpleDateFormat("d").format(date));
        return (Double.valueOf((date.getTime()+offset)/86400000)).intValue();
    }

    /**
     * Vymaze casovou slozku v datu (vynuluje)
     * @param date
     * @return the result
     */
    public static Date getDateWithoutTime(Date date) throws ParseException {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date));
    }

    /**
     * Prevede datum na retezec ve vychzim formatu dle DEFAULT_DATE_FORMAT
     * @param str
     * @return the result
     * @throws java.text.ParseException
     */
    public static Date stringToDate(String str) throws ParseException {
        return stringToDate(str, DEFAULT_DATE_FORMAT);
    }

    /**
     * Prevede datum na retezec v pozadovanem formatu
     * @param str
     * @param format
     * @return the result
     * @throws java.text.ParseException
     */
    public static Date stringToDate(String str, String format) throws ParseException {
        if (str == null) {
            return null;
        }
        return new SimpleDateFormat(format).parse(str);
    }

    /**
     * Slouci casovou slozku z promenne time s datovou slozkou promenne date
     * @param date
     * @param time
     * @return the result
     * @throws java.text.ParseException
     */
    public static Date mixDate(Date date, Date time) throws ParseException {
        if (time == null || date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd H:mm").parse(new SimpleDateFormat("yyyy-MM-dd").format(date) + " " + new SimpleDateFormat("H:mm").format(time));
    }

    /**
     * Zformatuje vysledek z databaze (Timestamp) na JavaDate
     * @param ts Hodnota z databáze
     * @return the result
     */
    public static Date timestampToDate(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return new Date(ts.getTime());
    }

    /**
     * Zformatuje datum na string ve formatu 'yyyy-MM-dd H:mm:ss' pouzivanem v databazi
     * @param dt Datum
     * @return the result
     */
    public static String dateToDBString(Date dt) {
        final String format = "yyyy-MM-dd H:mm:ss";
        if (dt == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(dt);
    }

    /**
     * Převede hodiny na milisekundy
     * @param hours time in hours
     * @return the result
     */
    public static long hoursToMs(Double hours) {
        return Double.valueOf(hours * 3600000).longValue();
    }

    /**
     * Převede minuty na milisekundy
     * @param minutes time in minutes
     * @return the result
     */
    public static long MinutesToMs(Double minutes) {
        return Double.valueOf(minutes * 60000).longValue();
    }
}
