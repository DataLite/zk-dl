package cz.datalite.zk.components.paging;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.impl.XulElement;

/**
 * Component like {@link org.zkoss.zul.Paging}. It's usable for model
 * when we have huge data.
 *
 * @author Karel Cemus
 */
public class DLPaging extends XulElement implements Pageable, Paginal {

    static {
        addClientEvent(DLPaging.class, ZulEvents.ON_PAGING, CE_IMPORTANT|CE_NON_DEFERRABLE);
    }

    // Controller
    protected DLPagingController controller;
    // Model
    protected DLPagingModel model;

    // Attributes
    protected Integer pageSize = DLPagingModel.NOT_PAGING;
    protected boolean autohide = false;
    protected boolean detailed = true;
    protected boolean countPages = true;
    protected int pageIncrement = 10;
    protected boolean showInfoText = true;


    public void setController( final DLPagingController controller ) {
        assert controller != null : "Controller cannot be null.";
        this.controller = controller;
    }

    public void setModel( final DLPagingModel model ) {
        this.model = model;
    }

    protected DLPagingModel getPagingModel() {
        if ( model == null )
            throw new UnsupportedOperationException( "Paging model is missing. Please check \"apply\" attribute with paging controller " +
                    "[compId=" + getId() + "]" +
                    "[spaceOwnerId=" + ((getSpaceOwner() instanceof Component) ? ((Component)getSpaceOwner()).getId() : "") + "]");
        return model;
    }

    public boolean isAutohide() {
        return this.autohide;
    }

    public void setAutohide( final boolean autohide ) {
        if (this.autohide != autohide) {
            this.autohide = autohide;
            smartUpdate("autohide", autohide);
        }
    }

    public int getTotalSize() {
        return getPagingModel().getTotalSize();
    }

    @Override
    public void setTotalSize(int size) throws WrongValueException {
        getPagingModel().setTotalSize(size, getPagingModel().getRowsOnPage());
    }

    @Override
    public int getPageIncrement() {
        return pageIncrement;
    }

    @Override
    public void setPageIncrement(int pginc) throws WrongValueException {
        this.pageIncrement = pginc;
    }

    public boolean isDetailed() {
        return this.detailed;
    }

    public void setDetailed( final boolean detailed ) {
        if (this.detailed != detailed) {
            this.detailed = detailed;
            smartUpdate("detailed", detailed);
        }
    }

    public boolean isCountPages() {
        return countPages;
    }

    public void setCountPages( final boolean countPages ) {
        if (this.countPages != countPages) {
            this.countPages = countPages;
            smartUpdate("countPages", countPages);
        }
    }

    public int getPageSize() {
        return model == null ? pageSize : getPagingModel().getPageSize();
    }

    /**
     * Event listener
     * @param size page size
     * @throws WrongValueException
     */
    public void setPageSize( final int size ) throws WrongValueException {
        if (this.pageSize != size) {
            this.pageSize = size;
            smartUpdate("pageSize", size);

            if (controller != null && getPagingModel().getPageSize() != size) {
                controller.onPageSize(size);
            }
        }
    }

    public int getPageCount() {
        return getPagingModel().getPageCount();
    }

    public int getActivePage() {
        return getPagingModel().getActualPage();
    }

    /**
     * Event listener
     * @param pg new page number
     * @throws WrongValueException
     */
    public void setActivePage( final int pg ) throws WrongValueException {
        int oldPage = getPagingModel().getActualPage();

        if ( controller != null && getPagingModel().getActualPage() != pg )
        {
            controller.onPaging( pg );

            // check special case - not known page count and empty data -> user entered actual page value
            // out of range. We do not know last page number, so return to the old page
            if (pg != 0 && !getPagingModel().isKnownPageCount() && getPagingModel().getRowsOnPage() == 0)
            {
                Clients.showNotification(Labels.getLabel("listbox.paging.outOfRange"), Clients.NOTIFICATION_TYPE_WARNING,
                        this, "before_start", 3000, true);
                controller.onPaging( oldPage );
                invalidate(); // rerender paging to refresh old paging value
            }
        }
    }

    protected String getInfoText() {

        if(!isShowInfoText()) {
            return "";
        }
        if (getPagingModel().getTotalSize() == 0)
            return "[ " + Labels.getLabel("listbox.paging.noData") + " ]";

        final int lastItem = ( getPagingModel().getActualPage() + 1 ) * getPagingModel().getPageSize();

        StringBuilder text = new StringBuilder("[ ");
        text.append(getPagingModel().getActualPage() * getPagingModel().getPageSize() + 1);

        text.append(" - ");

        if (!getPagingModel().isKnownPageCount() || getPagingModel().getTotalSize() > lastItem)
            text.append(lastItem);
        else
            text.append(getPagingModel().getTotalSize());

        text.append(" / ");

        if (getPagingModel().isKnownPageCount())
            text.append(getTotalSize());
        else
            text.append('?');

        text.append(" ]");

        return text.toString();
    }

    @Override
    public String getZclass() {
        final String added = "os".equals( getMold() ) ? "-os" : "";
        return _zclass == null ? "z-paging" + added : _zclass;
    }

    @Override
    public boolean isVisible() {
        return ( controller == null ) ? true : super.isVisible() && ( getPageCount() > 1 || !isAutohide() );
    }

    public boolean isKnownPageCount() {
        return getPagingModel().isKnownPageCount();
    }

    /**
     * Component read model and write changed like page count or active page
     */
    public void fireChanges() {
        smartUpdate("totalSize", getTotalSize());
        smartUpdate("infoText", getInfoText());
        smartUpdate("activePage", getPagingModel().getPageCount() == 0 ? 0 : getActivePage());
        smartUpdate("pageCount", getPageCount());
        smartUpdate("knownPageCount", isKnownPageCount());
        smartUpdate("showInfoText", isShowInfoText());
    }

    // super
    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);

        renderer.render("totalSize", getTotalSize());
        renderer.render("pageSize", getPageSize());
        renderer.render("pageCount", getPageCount());
        renderer.render("knownPageCount", isKnownPageCount());

        renderer.render("infoText", getInfoText());
        renderer.render("activePage", getPagingModel().getPageCount() == 0 ? 0 : getActivePage());

        render(renderer, "detailed", isDetailed());
        render(renderer, "autohide", isAutohide());
        render(renderer, "showInfoText", isShowInfoText());
    }

    // set active page on paging event directly
    public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
        final String name = request.getCommand();
        if (name.equals(ZulEvents.ON_PAGING)) {
            // create paging event directly instead of static factory method because of pageCount problem if total size not known
            int pgi = AuRequests.getInt(request.getData(), "", 0);
            PagingEvent evt = new PagingEvent(ZulEvents.ON_PAGING, request.getComponent(), pgi);
            setActivePage(evt.getActivePage());
            Events.postEvent(evt);
        } else
            super.service(request, everError);
    }

    public void setShowInfoText(boolean showInfoText) {
        this.showInfoText = showInfoText;
    }

    public boolean isShowInfoText() {
        return showInfoText;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void setDisabled(boolean b) {
        // Nepoužíváno
    }
}
