package cz.datalite.zk.components.list.filter.components;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This implementation of FilterComponentFactory is used when the component
 * is defined by class name. This class is reproduced by its constructor without
 * parameters. 
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class InstanceFilterComponentFactory implements FilterComponentFactory {

    protected final Class<? extends FilterComponent> componentClass;

    public InstanceFilterComponentFactory( final String className ) {
        try {
            componentClass = ( Class<? extends FilterComponent> ) Class.forName( className );
        } catch ( ClassNotFoundException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public FilterComponent createFilterComponent() {
        try {
            return componentClass.newInstance();
        } catch ( Exception ex ) {
            throw new IllegalArgumentException( "Filter component class is malformed. The instance couldn't be created.", ex );
        }
    }

    public Class<? extends FilterComponent> getComponentClass() {
        return componentClass;
    }
}
