package cz.datalite.zk.components.list.filter.components;

/**
 * Interface extends the FilterComponent by new method which defines
 * that this component is cloneable. This behavior is required
 * for CloneFilterComponentFactory which creating the components from
 * the template using clone method.
 *
 * @author Karel Cemus
 */
public interface CloneableFilterComponent<T> extends FilterComponent<T> {

    /**
     * Returns new instance of this component. There is expected deep copy of
     * object or just creating new inicialized instance. This method is called
     * on filter component templates which are setted throught the controller.
     *
     * @return deep copy of the object
     */
    FilterComponent<T> cloneComponent();
}
