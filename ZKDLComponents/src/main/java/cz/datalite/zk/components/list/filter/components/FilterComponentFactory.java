package cz.datalite.zk.components.list.filter.components;

/**
 * This factory produces the components which are shown in a normal filter.
 * These components are derived from the class name or existing instance.
 *
 * @author Karel Cemus
 */
public interface FilterComponentFactory {

    /**
     * Creates an instance of filter component to be shown in the filter manager.
     *
     * @return new instance of filter component
     */
    FilterComponent createFilterComponent();

    /**
     * Returns the class of defined filter component
     *
     * @return class of filter component.
     */
    Class<? extends FilterComponent> getComponentClass();
}
