package cz.datalite.zk.components.list.controller.impl;

import java.io.IOException;
import java.util.List;

import org.zkoss.util.media.AMedia;

import cz.datalite.zk.components.list.controller.DLManagerController;

/**
 * Default implementation of controller. It is used because of
 * back compatibility.
 * @author Karel Cemus
 */
public class DLDefaultManagerControllerImpl implements DLManagerController {

	@Override
    public void onColumnManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void onSortManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void onFilterManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void onExportManager() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void onResetFilters() throws InterruptedException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void onResetAll() throws InterruptedException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public List<String> getFilters() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void fireChanges() {
    }

    @Override
    public void exportCurrentView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public AMedia directExportCurrentView() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
