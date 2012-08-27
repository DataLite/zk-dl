package cz.datalite.zk.components.combo;

import cz.datalite.zk.components.cascade.CascadableExt;
import java.util.List;
import org.zkoss.zk.ui.util.Composer;

/**
 * Extended controller for the combobox
 * @param <T> řídící entita controlleru
 * @author Karel Čemus <cemus@datalite.cz>
 * @deprecated since 1.4.0, ZK 6, databinding 2. This component is not usable anymore
 */
@Deprecated
public interface DLComboboxExtController<T> extends Composer, DLComboboxController<T>, CascadableExt<T> {

    /**
     * Reacts on the onOpen event
     */
    void onOpen();

    /**
     * Test if the entity is in the model. If entity isn't in the model,
     * it must be added else it can be selected.
     * @param entity tested entity
     * @return existence entity v modelu
     */
    boolean isInModel( T entity );

    /**
     * Adds entity into model.
     * @param entity entity to adding
     */
    void add( T entity );

    /**
     * Returns model - list of the entities which are actually loaded
     * @return model list of entities
     */
    List<T> getModel();
}
