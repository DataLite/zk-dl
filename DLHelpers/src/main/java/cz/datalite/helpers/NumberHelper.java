package cz.datalite.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Pomocné metody pro práci s číslem
 *
 * @author Jiri Bubnik
 */
public abstract class NumberHelper
{
    /**
     * Vraci cislo zformatovane jako Kč (defaultní formátování)
     * @param number hodnota v Kč
     * @return hodnota v Kč
     */
    public static String getCzechCurrency(Double number)
    {
        return number == null ? "" : NumberFormat.getCurrencyInstance(new Locale("cs", "CZ")).format(number);
    }

    public static String getCzechCurrency(Long number)
    {
        return number == null ? "" : NumberFormat.getCurrencyInstance(new Locale("cs", "CZ")).format(number);
    }

    public static String getCzechCurrency(BigDecimal number)
    {
        return number == null ? "" : NumberFormat.getCurrencyInstance(new Locale("cs", "CZ")).format(number);
    }

    public static Double round(Double cislo, int pocetMist ) {
        BigDecimal zaokrouhleno = new BigDecimal(cislo);
        zaokrouhleno = zaokrouhleno.setScale(pocetMist, RoundingMode.HALF_UP);
        return (Double) zaokrouhleno.doubleValue();
    }

    public static String toString(Double cislo) {
        return (cislo == null) ? "0" : cislo.toString() ;
    }

    public static Double nullToNula(Double cislo) {
        return (cislo == null) ? 0D : cislo ;
    }

    public static Long nullToNula(Long cislo) {
        return (cislo == null) ? 0L : cislo ;
    }
}
