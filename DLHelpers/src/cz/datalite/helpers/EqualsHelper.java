package cz.datalite.helpers;

/**
 * Pomocné metody pro porovnání dvou objektů, zda jsou stejné.
 *
 * @author Jiri Bubnik
 */
public abstract class EqualsHelper {

    /**
     * Porovná oba objekty. Pokud je alespoň jeden z nich NULL, vrací false.
     * Jinak jsou objekty porovnány metodou object1.equals(object2);
     *
     * @param object1 první objekt pro porovnání, na něm se volá equals
     * @param object2 druhý objekt
     * @return Pokud je alespoň jeden NULL, vrací false, jinak porovná přes a.equals(b);
     */
    public static boolean isEquals( final Object object1, final Object object2 ) {
        return object1 == null || object2 == null ? false : object1.equals( object2 );
    }

    /**
     * Porovná oba objekty. Pokud jsou oba null, vrací true, jinak porovná přes a.equals(b);
     *
     * @param object1 první objekt pro porovnání, na něm se volá equals
     * @param object2 druhý objekt
     * @return Pokud jsou oba null, vrací true, jinak porovná přes object1.equals(object2);
     */
    public static boolean isEqualsNull( final Object object1, final Object object2 ) {
        return object1 == null ? object2 == null : object1.equals( object2 );
    }

    /**
     * Porovná třídy dvou objektů a rozhodne, jestli jsou instancí stejné třídy
     * @param object1 první objekt k porovnání. Pokud jeden z parametrů je NULL, tak
     * návratová hodnota je automaticky false.
     * @param object2 druhý objekt k provnání
     * @return rozhodnutí, zda-li jsou třídy stejné
     */
    public static boolean isSameClass( final Object object1, final Object object2 ) {
        if ( object1 == null || object2 == null ) {
            return false;
        }
        return object1.getClass().equals( object2.getClass() );
    }
}
