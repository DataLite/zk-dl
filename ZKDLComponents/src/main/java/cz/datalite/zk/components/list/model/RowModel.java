package cz.datalite.zk.components.list.model;

import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.components.CloneFilterComponentFactory;
import cz.datalite.zk.components.list.filter.components.FilterComponentFactory;
import cz.datalite.zk.components.list.filter.components.InstanceFilterComponentFactory;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Space;

/**
 *
 * @author Karel Cemus
 */
public class RowModel {

    protected final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RowModel.class);
    protected NormalFilterUnitModel model;
    protected Component position1;
    protected Component position2;
    protected boolean rerender;
    protected int renderedArity;

    public RowModel( final NormalFilterUnitModel model ) {
        this.model = model;
        position1 = new Space();
        position2 = new Space();
    }

    public void setTemplate( final NormalFilterUnitModel template ) {
        if ( template == model.getTemplate() ) {
            return;
        }
        LOGGER.debug( "Column template was changed for property '{}'.", template.getLabel() );
        final FilterComponentFactory oldFactory = model.getFilterComponentFactory();
        final FilterComponentFactory newFactory = template.getFilterComponentFactory();

        // decision if the components have to be rerendered
        if ( (oldFactory instanceof CloneFilterComponentFactory) || (newFactory instanceof CloneFilterComponentFactory) ) {
            rerender |= oldFactory != newFactory;
        } else if ( (oldFactory instanceof InstanceFilterComponentFactory) || (newFactory instanceof InstanceFilterComponentFactory) ) {
            rerender |= !oldFactory.getComponentClass().equals( newFactory.getComponentClass() );
        } else {
            rerender = true;
        }
        if ( rerender ) {
            model.setValue( 1, null );
            model.setValue( 2, null );
        }

        model.update( template );
    }

    public NormalFilterUnitModel getModel() {
        return model;
    }

    public Component getPosition( final int index ) {
        if ( index == 1 ) {
            return position1;
        } else {
            return position2;
        }
    }

    public void setPosition( final int index, final Component component ) {
        if ( index == 1 ) {
            position1 = component;
        } else {
            position2 = component;
        }
    }

    public NormalFilterUnitModel getTemplate() {
        return model.getTemplate();
    }

    public boolean isTemplateChanged() {
        return rerender;
    }

    public int getRenderedArity() {
        return renderedArity;
    }

    public boolean isRerender() {
        return rerender;
    }

    public void rendered( final int arity ) {
        renderedArity = arity;
        rerender = false;
    }

    public void setRerender() {
        rerender = true;
    }
}
