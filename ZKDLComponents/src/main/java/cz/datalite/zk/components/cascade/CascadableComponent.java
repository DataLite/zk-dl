package cz.datalite.zk.components.cascade;

import org.zkoss.zk.ui.Component;

/**
 * Cascadable component has to implement these methods to set up cascade relations.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface CascadableComponent extends Component {

    /**
     * Returns component ID of the parent component
     */
    String getParentCascadeId();

    /**
     * Returns property name that should be used to filter values.
     * This is the property of this component entity and while filtering is compared to parent selected item.
     *
     * @return entity property name to apply filter on
     */
    String getParentCascadeColumn();

    /**
     * Returns the controller.
     * 
     * @return the controller
     */
    CascadableExt getCascadableController();
}
