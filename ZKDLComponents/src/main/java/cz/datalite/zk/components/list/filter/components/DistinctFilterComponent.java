package cz.datalite.zk.components.list.filter.components;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.DataLoader;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import cz.datalite.zk.components.lovbox.Box;
import cz.datalite.zk.components.lovbox.DLDistinctLovbox;
import java.util.List;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * This filter component servers to provide user the list of values which are
 * available for this column. Usually this model is filled from database. As
 * a connection provider there are DataLoader which is defined in window controller.
 * @author Karel Cemus
 */
public class DistinctFilterComponent extends AbstractFilterComponent<DLDistinctLovbox> implements RequireController, RequireColumnModel {

    protected DLListboxExtController controller;
    protected DLColumnUnitModel unit;

    public DistinctFilterComponent() {
        super( new DLDistinctLovbox() );
        component.setLoader( new DataLoader() {

            public DLResponse<String> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
                return DistinctFilterComponent.this.loadData( filter, firstRow, rowCount, sorts );
            }
        } );
    }

    @Override
    public Object getValue() {
        return component.getController().getSelectedItem().value;
    }

    @Override
    public void setValue( final Object value ) {
        component.setItem( new Box( ( String ) value ) );
    }

    public FilterComponent cloneComponent() {
        return new DistinctFilterComponent();
    }

    public void setController( final DLListboxExtController controller ) {
        this.controller = controller;
    }

    public void setColumnModel( final DLColumnUnitModel unit ) {
        this.unit = unit;
    }

    protected DLResponse<String> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
        String filterValue = null;
        if ( filter.size() == 1 ) {
            filterValue = ( String ) filter.get( 0 ).getValue( 1 );
        }
        return controller.loadDistinctValues( unit.getColumn(), filterValue, firstRow, rowCount );
    }

    @Override
    public void addOnChangeEventListener( final EventListener listener ) {
        component.addEventListener( Events.ON_SELECT, listener );
    }
}
