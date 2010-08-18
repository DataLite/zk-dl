package cz.datalite.helpers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Queue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zkplus.databind.Binding;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.impl.XulElement;

/**
 * Pomocne metody pro praci s ZK plus bindingem
 *
 * @author Jiri Bubnik
 */
public abstract class ZKBinderHelper {

    /**
     * Vraci odkaz na binder
     * @param comp komponenta podle ktere ho urci
     * @return odkaz na binder
     */
    public static DataBinder getBinder( Component comp ) {
        return ( DataBinder ) comp.getVariable( "binder", false );
    }

    /**
     * Vraci odkaz na binder
     * @param page stranka podle ktere ho urci
     * @return odkaz na binder
     */
    public static DataBinder getBinder( Page page ) {
        return ( DataBinder ) page.getVariable( "binder" );
    }

    /**
     * Vraci anotaci pro binding
     *
     * @param comp komponenta pro kterou se zjistuje
     * @param attribute nazev atributu komponenty
     * @param annotation nazev anotace (zjistuje se v skupine "default-bind")
     * @return anotaci pokud najde nebo null
     */
    public static String getBindingAnnotation( Component comp, String attribute, String annotation ) {
        if ( !(comp instanceof XulElement) ) {
            return null;
        }

        org.zkoss.zk.ui.metainfo.Annotation annot = (( XulElement ) comp).getAnnotation( attribute, "default-bind" );
        if ( annot != null ) {
            return annot.getAttribute( annotation );
        } else {
            return null;
        }
    }

    /**
     * Vraci anotaci pro default
     *
     * @param comp komponenta pro kterou se zjistuje
     * @param attribute nazev atributu komponenty
     * @param annotation nazev anotace (zjistuje se v skupine "default")
     * @return anotaci pokud najde nebo null
     */
    public static String getDefaultAnnotation( Component comp, String attribute, String annotation ) {
        if ( !(comp instanceof XulElement) ) {
            return null;
        }

        org.zkoss.zk.ui.metainfo.Annotation annot = (( XulElement ) comp).getAnnotation( attribute, "default" );
        if ( annot != null ) {
            return annot.getAttribute( annotation );
        } else {
            return null;
        }
    }

    /**
     * Nahraje vsechny atributy z komponenty
     *
     * @param comp komponenta k nastaveni
     */
    public static void loadComponent( Component comp ) {
        getBinder( comp ).loadComponent( comp );
    }

    /**
     * Nahraje atribut z komponenty
     *
     * @param comp      komponenta k nastaveni
     * @param attribute pozadovany atribut
     */
    public static void loadComponentAttribute( Component comp, String attribute ) {
        Binding bind = getBinder( comp ).getBinding( comp, attribute );
        if ( bind != null ) {
            bind.loadAttribute( comp );
        }

    }

    /**
     * Ulozi komponentu pres binding
     * @param comp
     */
    public static void saveComponent( Component comp ) {
        getBinder( comp ).saveComponent( comp );
    }

    /**
     * Vlozi atribut do komponenty
     *
     * @param comp          komponenta k nastaveni
     * @param attribute     pozadovany atribut
     *
     **/
    public static void saveComponentAttribute( Component comp, String attribute ) {
        if ( getBinder( comp ) != null ) {
            Binding bind = getBinder( comp ).getBinding( comp, attribute );
            if ( bind != null ) {
                bind.saveAttribute( comp );
            }
        }
    }

    /**
     *
     * Nahraje atributy z komponenty
     * @param comp komponenta k nastaveni
     * @param attributes seznam pozadovanych atributu
     */
    public static void loadComponentAttributes( Component comp, String[] attributes ) {
        for ( String attr : attributes ) {
            loadComponentAttribute( comp, attr );
        }
    }

    /**
     * Pro komponentu nastavi default anotaci pro hodnotu
     * (volani napr. setValueAnnotation(comp, "model", "aaa") odpovida nastaveni model="@{aaa}")
     * @param comp pro kterou komponentu
     * @param propName nazev vlastnosti (atributu)
     * @param annot anotace
     */
    public static void setValueAnnotation( XulElement comp, String propName, String annot ) {
        DataBinder binder = getBinder( comp );

        // odebrani puvodniho pokud existuje
        if ( binder.existBinding( comp, propName ) ) {
            binder.removeBinding( comp, propName );
        }

        // pridani noveho
        Map attrs = new HashMap();
        attrs.put( "value", annot );
        binder.addBinding( comp, propName, annot );

        // pokud je prvni bean fellow, tak ho pro jistotu zaregistruji (pokud nebyl pouzit jinde, tak neni zaregistrovany)
        String bean = annot;
        if ( bean.contains( "." ) ) {
            bean = bean.split( "\\." )[0];
        }
        Component fellowBean = comp.getFellowIfAny( bean );
        if ( fellowBean != null ) {
            binder.bindBean( bean, fellowBean );
        }

        // a hned nastavime hodnotu
        loadComponentAttribute( comp, propName );
    }

    /**
     * Přidá na komponentu anotaci do klíče "default"
     * struktura bude comp -> property -> "default" -> annotName -> value
     * @param component
     * @param property
     * @param annotName
     * @param value
     * @author Karel Čemus
     */
    public static void registerAnnotation( final org.zkoss.zk.ui.AbstractComponent component, final String property, final String annotName, final String value ) {
        final java.util.Map<String, String> map = new java.util.HashMap<String, String>();
        map.put( annotName, value );
        component.addAnnotation( property, "default", map );
    }

    /**
     * Pro UI kolekci (Grid, Listbox) nalezne souseda v rámci řádku (row, listitem) podle vestavěného ID.
     * Interně nejprve najde nadrizeny listitem nebo row a od nej potom dolu hleda komponentu, ktera konci na #ID
     *
     * @param comp komponenta se seznamem dětí loadovaných přes binding např. &lt;listitem self="@{each=lst}"&gt;
     * @param id ZK id komponenty pro nalezení
     * @return nalezenou komponentu nebo null
     */
    public static Component getBindingSibling( Component comp, String id ) {
        String suffix = "#" + id;

        Component parent = comp;
        while ( parent != null && !((parent instanceof Listitem)
                || (parent instanceof Row)) ) {
            parent = parent.getParent();
        }

        if ( parent == null ) {
            throw new UiException( "Method getBindingSibling is supported only for component under Listitem." );
        }

        Queue<Component> comps = new LinkedList();
        comps.add( parent );

        while ( !comps.isEmpty() ) {
            Component compare = comps.poll();
            if ( compare.getId().endsWith( suffix ) ) {
                return compare;
            }
            for ( Component child : ( List<Component> ) compare.getChildren() ) {
                comps.add( child );
            }
        }

        return null;
    }
}
