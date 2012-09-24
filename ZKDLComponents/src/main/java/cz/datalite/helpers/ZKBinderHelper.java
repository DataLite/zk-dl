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
 * @since ZK-DL 1.4.0 replaced by {@link cz.datalite.zk.bind.ZKBinderHelper}
 */
@Deprecated
public abstract class ZKBinderHelper {

    /**
     * Vraci odkaz na binder
     * @param comp komponenta podle ktere ho urci
     * @return odkaz na binder
     */
    public static DataBinder getBinder( Component comp ) {
        return ( DataBinder ) comp.getAttributeOrFellow( "binder", true); 
    }

    /**
     * Vraci odkaz na binder
     * @param page stranka podle ktere ho urci
     * @return odkaz na binder
     */
    public static DataBinder getBinder( Page page ) {
        return ( DataBinder ) page.getAttributeOrFellow( "binder", true );
    }

    /**
     * Returns binding annotation
     *
     * @param comp annotated component
     * @param attribute name of component's attribute
     * @param annotation annotation name (searched in group "default-bind")
     * @return annotation if exists otherwise null
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
     * Returns annotation from group "default"
     *
     * @param comp annotated component
     * @param attribute name of component's attribute
     * @param annotation annotation name (searched in group "default")
     * @return annotation if exists otherwise null
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
     * @author Karel Cemus
     */
    public static void registerAnnotation( final org.zkoss.zk.ui.AbstractComponent component, final String property, final String annotName, final String value ) {
        final java.util.Map<String, String[]> map = new java.util.HashMap<String, String[]>();
        map.put( annotName, new String[]{ value } );
        component.addAnnotation( property, "default", map );
    }

    /**
     * For UI collections (Grid, Listbox) it finds the sibling in the context of
     * current row, listitem, etc. based on the inner ID. Internally finds the
     * parent item at first and then it looks for the component ending with
     * given #id.
     *
     * @param comp component containing the list of children loaded by
     * databinding eg.: &lt;listitem self="@{each=lst}"&gt;
     * @param id identifier of ZK component to be found
     * @return found component or null
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
