package cz.datalite.zk.composer;

import cz.datalite.zk.annotation.ZkBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zkplus.databind.DataBinder;

/**
 * Load/Save binding on a component.
 *
 * @author Jiri Bubnik
 */
public class DLZkBindingCommand {

    private Component component;
    private boolean loadRefresh;
    private boolean saveRefresh;

    /**
     * Create command according to @ZkBinding annotation.
     *
     * @param binding binding anntoation
     * @param component master component.
     */
    public DLZkBindingCommand( final ZkBinding binding, final Component component ) {
        super();
        try {
            this.component = binding.component().length() == 0 ? component : component.getFellow( binding.component() );
            loadRefresh = binding.loadAfter();
            saveRefresh = binding.saveBefore();
        } catch ( ComponentNotFoundException ex ) {
            throw new ComponentNotFoundException( "ZkBinding could not be registered on component \"" + binding.component() + "\" because component wasn\'t found.", ex );
        }
    }

    /**
     * Load binding.
     */
    public void load() {
        if ( loadRefresh ) {
            getBinder( component ).loadComponent( component );
        }
    }

    /**
     * Save binding.
     */
    public void save() {
        if ( saveRefresh ) {
            getBinder( component ).saveComponent( component );
        }
    }

    /**
     * Vraci odkaz na binder
     * @param comp komponenta podle ktere ho urci
     * @return odkaz na binder
     */
    private static DataBinder getBinder( final Component comp ) {
        return ( DataBinder ) comp.getAttribute( "binder");
    }
}
