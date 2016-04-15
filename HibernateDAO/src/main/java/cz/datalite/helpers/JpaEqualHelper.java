package cz.datalite.helpers;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Query;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Vector;

/**
 * Pomocne metody pro JPA
 *
 * @author Jiri Bubnik
 */
public abstract class JpaEqualHelper {

//    /**
//     * Vynulovani L2 Cache (Server cache)
//     *
//     * Server cache udrzuje vysledky predchozich dotazu a je platna pro aplikacni server.
//     * Pokud dojde ke zmene dat MIMO JPA (tedy mimo aplikaci), je potreba o tom server uvedomit
//     *
//     * Refresh je mozne provest na urovni objektu, dotazu nebo celkove
//     *
//     * Pozor! Jedna se pouze o cache L2 (server). Kazdy klient ma jeste svoji cache (L1), kde muze mit objekt take ulozen (attached Managed Bean)
//     *
//     * Vice viz. http://weblogs.java.net/blog/guruwons/archive/2006/09/understanding_t_1.html
//     */
//    public static void invalidateL2Cache()
//    {
//        ((EntityManagerImpl)JpaUtil.getEntityManager().getDelegate()).getServerSession().getIdentityMapAccessor().invalidateAll();
//    }
    /**
     * Funkce vykona predany dotaz a vysledek ocekava prave jeden radek a prave jeden sloupec typu int aka ( count(*) )
     * @param q dotaz
     * @return vysledek
     */
    public static int getNativeQueryCount( Query q ) {
        Object result = (( Vector ) q.getSingleResult()).get( 0 );
        return Integer.parseInt( result.toString() );
    }

    /**
     * <p>Vrací název entity převedený na tabulku.</p>
     *
     * <p>Pokud třída objektu o obsahuje anotaci javax.persistence.Table, vezme z ní hodnoty schema a name. Pokud je schema vyplněno,
     * vrací se název tabulky včetně schématu. Pokud není anotace Table přítomná, nebo nemá atribut name, převede se název třídy
     * na název entity pomocí org.hibernate.cfg.ImprovedNamingStrategy.</p>
     *
     * @param o objekt ze kterého se má název tabulky vzít.
     * @return název tabulky (např. "CIS_FIRMA" nebo "JIVIS.CIS_FIRMA")
     */
    public static String getEntityTableName( Object o ) {
        if ( o == null ) {
            return null;
        }

        Class clazz = Hibernate.getClass( o );

        String schema = null;

        // nejprve se pokusi najit primo anotaci TABLE
        javax.persistence.Table table = ( Table ) clazz.getAnnotation( javax.persistence.Table.class );
        if ( table != null ) {
            if ( !StringHelper.isNull( table.schema() ) ) {
                schema = table.schema().toUpperCase();
            }

            if ( !StringHelper.isNull( table.name() ) ) {
                return schema == null ? table.name().toUpperCase() : schema + "." + table.name().toUpperCase();
            }
        }

        // anotace tam neni, bere se podle nazvu entity
        String name = clazz.getSimpleName();

        // prevod na nazev tabulky
        org.hibernate.cfg.ImprovedNamingStrategy strategy = new org.hibernate.cfg.ImprovedNamingStrategy();
        name = strategy.classToTableName( name ).toUpperCase();

        return schema == null ? name : schema + "." + name;
    }

    /**
     * <p>Projde všechny pole třídy objektu o a první, pro které najde s anotací javax.persistence.Id, vrací jeho hodnotu.</p>
     *
     * <b>Nepodporuje všechny konstrukce pro Id v JPA.</b> Slouží zejména pro typické použití Id pro primární klíč ze sequence Long.
     *
     * @param o Objekt pro zjištění hodnoty.
     * @return hodnotu pole s anotací Id nebo null.
     */
    public static Object getEntityId( Object o ) {
        if ( o == null ) {
            return null;
        }

        if ( o instanceof HibernateProxy ) {
            Hibernate.initialize( o );
            HibernateProxy proxy = ( HibernateProxy ) o;
            Object impl = proxy.getHibernateLazyInitializer().getImplementation();

            return proxy.getHibernateLazyInitializer().getIdentifier();
        }

        Class clazz = o.getClass();
        for ( Field f : clazz.getDeclaredFields() ) {
            if ( f.isAnnotationPresent( javax.persistence.Id.class ) ) {
                Object value = null;
                f.setAccessible( true );
                try {
                    value = f.get( o );
                } catch ( IllegalArgumentException ex ) {
                    throw new AssertionError( ex );
                } catch ( IllegalAccessException ex ) {
                    throw new AssertionError( ex );
                }
                f.setAccessible( false );
                return value;
            }
        }

        // ID nenalzeno
        return null;
    }

    /**
     * Porovnání dvou entit, zda jsou stejné třídy.
     * Metoda je null safe, a proxy safe. Pokud je jedna z entit null, vrací false.
     * Při rozpoznávání třídy používá Hibernate.getClass() pro správné rozpoznání proxy.
     *
     * @param thisEntity první entita
     * @param thatEntity druhá entita
     * @return true, pokud jsou obě not null a reprezentují objekty stejné třídy.
     */
    public static boolean isEntitySameClass( Object thisEntity, Object thatEntity ) {
        if ( thisEntity == null || thatEntity == null ) {
            return false;
        } else if ( thisEntity.getClass() == thatEntity.getClass() ) {
            // same class
            return true;
        } else {
            // same class but hibernate proxy (must not be detached)
            return Hibernate.getClass( thisEntity ).equals( Hibernate.getClass( thatEntity ) );
        }
    }
}
