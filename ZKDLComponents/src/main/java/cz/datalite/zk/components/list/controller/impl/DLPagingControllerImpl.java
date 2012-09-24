package cz.datalite.zk.components.list.controller.impl;

import cz.datalite.zk.components.list.RowCount;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.paging.DLPaging;
import cz.datalite.zk.components.paging.DLPagingController;
import cz.datalite.zk.components.paging.DLPagingModel;

/**
 * Implementation of the cotntroller for the paging component.
 * @author Karel Cemus
 */
public class DLPagingControllerImpl implements DLPagingController {

    // master controller
    protected final DLListboxExtController masterController;
    // model
    protected DLPagingModel model;
    // view
    protected DLPaging paging;

    public DLPagingControllerImpl( final DLListboxExtController masterController, final DLPagingModel model, final DLPaging paging ) {
        this.masterController = masterController;
        this.model = model;
        this.paging = paging;
        model.setPageSize( paging.getPageSize() );
        model.setCountPages( paging.isCountPages() );
        paging.setModel( model );
        paging.setController( this );
    }

    public void onPaging( final Integer page ) {
        if ( masterController.isLocked() ) {
            return;
        }

        if ( model.isKnownPageCount() ) {
            model.setActualPage( Math.min( page, model.getPageCount() - 1 ) );
        } else {
            model.setActualPage( page );
        }

        if ( model.getActualPage() < 0 ) {
            model.setActualPage( 0 );
        }
        masterController.onPagingModelChange();
    }

    public void onPageSize( final Integer pageSize ) {
        if ( masterController.isLocked() ) {
            return;
        }
        model.setPageSize( pageSize );
        masterController.onPagingModelChange();
    }

    public void fireChanges() {
        paging.fireChanges();
    }

    public DLPaging getPaging() {
        return paging;
    }

    public String getUuid() {
        return paging.getUuid();
    }

    public boolean supportsRowCount()
    {
        return (masterController instanceof RowCount);
    }

    public int getRowCount()
    {
        if (!supportsRowCount())
            throw new IllegalStateException("Listbox controller does not support rowcount.");

        return ((RowCount)masterController).rowCount(masterController.getNormalFilterModel());

    }
}
