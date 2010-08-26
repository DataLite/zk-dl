package cz.datalite.zk.components.list.filter.components;

/**
 * Implementation of Filter Component Factory which is used when the existing
 * instance of Filter component is given at the beginning. The reproduction of
 * the component is based on cloneComponent method which has to be implemented
 * in the template component.
 * 
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class CloneFilterComponentFactory implements FilterComponentFactory {

    /** Template of component */
    protected final CloneableFilterComponent template;

    /**
     * Creates the factory with this template
     *
     * @param template filter component template which is cloned
     */
    public CloneFilterComponentFactory( final CloneableFilterComponent template ) {
        this.template = template;
    }

    public FilterComponent createFilterComponent() {
        return template.cloneComponent();
    }

    public Class<? extends FilterComponent> getComponentClass() {
        return template.getClass();
    }
}
