package cz.datalite.zk.components.lovbox;

import cz.datalite.zk.components.list.DataLoader;
import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.DLListboxSimpleController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

/**
 *
 * @author Karel Cemus
 */
public class DLDistinctLovbox extends DLLovbox<Box> {

    protected DataLoader loader;
    protected Box notCtl;

    public DLDistinctLovbox() {
        super();
        setPageSize( 10 );
        setRows( 10 );
        setListWidth( "400px" );
        setLabelProperty( "value" );
    }

    public void setLoader( final DataLoader loader ) {
        this.loader = loader;
    }

    @Override
    public void setParent( final Component parent ) {
        super.setParent( parent );
        this.afterCompose();
        this.setController( new DLLovboxGeneralController<Box>( new DLListboxSimpleController<Box>( "" ) {

            @Override
            protected DLResponse<Box> loadData( final List<NormalFilterUnitModel> filter, final int firstRow, final int rowCount, final List<DLSort> sorts ) {
                final DLResponse<String> response = loader.loadData( filter, firstRow, rowCount, sorts );
                final List<Box> boxes = new ArrayList<Box>( response.getData().size() );
                for ( String string : response.getData() ) {
                    boxes.add( new Box( string ) );
                }
                return new DLResponse<Box>( boxes, response.getRows() );
            }
        } ) );
        (new AnnotateDataBinder( this )).loadAll();
        if ( notCtl != null ) {
            setItem( notCtl );
            notCtl = null;
        }

    }

    public void setItem( final Box item ) {
        if ( getController() == null ) {
            notCtl = item;
        } else {
            setSelectedItem( item );
        }
    }
}
