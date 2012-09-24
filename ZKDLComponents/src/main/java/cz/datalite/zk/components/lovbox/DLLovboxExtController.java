package cz.datalite.zk.components.lovbox;

import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import org.zkoss.zk.ui.util.Composer;

/**
 * Private public api which is used in this package to programming
 * against interface. It defines method which is called from other classes
 * but shouldn't be call by the user.
 * @param <T>  Main entity in the bandbox
 * @author Karel Cemus
 */
public interface DLLovboxExtController<T> extends Composer, DLLovboxController<T>, CascadableExt<T> {

    /**
     * Returns controller for the listbox, paging and quickfilter.
     * @return multifunctional controller
     */
    DLListboxExtController<T> getListboxExtController();

    /**
     * Returns lovbox model.
     * @return lovbox model.
     */
    DLLovboxModel<T> getModel();

}
