package cz.datalite.zk.components.paging;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginated;

/**
 * Component like {@link org.zkoss.zul.Paging}. It's usable for model
 * when we have huge data.
 *
 * @author Karel Cemus
 */
public class DLPaging extends Hbox implements Pageable {

    // Controller
    protected DLPagingController controller;
    // Model
    protected DLPagingModel model;
    // Attributes
    protected Integer pageSize = DLPagingModel.NOT_PAGING;
    protected boolean autohide = true;
    protected boolean detailed = true;
    protected boolean countPages = true;

    // view
    protected Intbox pageNumber;
    protected Label totalPages = new Label();
    protected Label detailInfo = new Label();

    // buttons
    protected final Button first = new Button();
    protected final Button previous = new Button();
    protected final Button next = new Button();
    protected final Button last = new Button();
    // parents for buttons, because buttons need parent with css button class.
    private final Div firstButton = new Div();
    private final Div previousButton = new Div();
    private final Div nextButton = new Div();
    private final Div lastButton = new Div();

    // everyting added by ZUL page goes here (see appendChild)
    private final Hbox additionalContent = new Hbox();

    public DLPaging() {
        super();

        setClass("z-paging");
        setWidth("100%");

        first.setZclass("z-paging-first");
        first.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception
            {
                setActivePage(0);
            }
        });
        previous.setZclass("z-paging-prev");
        previous.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception
            {
                setActivePage(getActivePage()-1);
            }
        });
        next.setZclass("z-paging-next");
        next.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception
            {
                setActivePage(getActivePage()+1);
            }
        });
        last.setZclass("z-paging-last");
        last.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception
            {
                setActivePage(getPageCount());
            }
        });

        first.setParent(firstButton);
        previous.setParent(previousButton);

        // place button parent
        firstButton.setParent(this);
        previousButton.setParent(this);

        pageNumber = new Intbox();
        pageNumber.setWidth( "3em" );
        pageNumber.setParent( this );
        pageNumber.addEventListener( Events.ON_CHANGE, new org.zkoss.zk.ui.event.EventListener() {

            public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
                setActivePage( pageNumber.getValue() - 1 );
            }
        } );

        pageNumber.addEventListener( Events.ON_OK, new org.zkoss.zk.ui.event.EventListener() {

            public void onEvent( final org.zkoss.zk.ui.event.Event event ) {
                setActivePage( pageNumber.getValue() - 1 );
            }
        } );

        new Label("/").setParent(this);

        totalPages.setParent(this);

        // place button parent
        nextButton.setParent(this);
        lastButton.setParent(this);

        next.setParent(nextButton);
        last.setParent(lastButton);

        Space space = new Space();
        space.setHflex("1");
        space.setParent(this);

        additionalContent.setParent(this);

        detailInfo.setStyle("align: right;");
        detailInfo.setParent(this);

        space = new Space();
        space.setParent(this);

        // events onMouseOver and onMouseOut for all buttons
        next.addEventListener( Events.ON_MOUSE_OVER, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                nextButton.setClass("z-paging-btn z-paging-btn-over");
            }
        } );

        next.addEventListener( Events.ON_MOUSE_OUT, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                if(! next.isDisabled())
                {
                    nextButton.setClass("z-paging-btn");
                }
                else
                {
                    nextButton.setClass("z-paging-btn z-paging-btn-disd");
                }
            }
        } );

        first.addEventListener( Events.ON_MOUSE_OVER, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                firstButton.setClass("z-paging-btn z-paging-btn-over");
            }
        } );

        first.addEventListener( Events.ON_MOUSE_OUT, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                if(! first.isDisabled())
                {
                    firstButton.setClass("z-paging-btn");
                }
                else
                {
                    firstButton.setClass("z-paging-btn z-paging-btn-disd");
                }
            }
        } );

        previous.addEventListener( Events.ON_MOUSE_OVER, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                previousButton.setClass("z-paging-btn z-paging-btn-over");
            }
        } );

        previous.addEventListener( Events.ON_MOUSE_OUT, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                if(! previous.isDisabled())
                {
                    previousButton.setClass("z-paging-btn");
                }
                else
                {
                    previousButton.setClass("z-paging-btn z-paging-btn-disd");
                }
            }
        } );

        last.addEventListener( Events.ON_MOUSE_OVER, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                lastButton.setClass("z-paging-btn z-paging-btn-over");
            }
        } );

        last.addEventListener( Events.ON_MOUSE_OUT, new org.zkoss.zk.ui.event.EventListener()
        {
            public void onEvent( final org.zkoss.zk.ui.event.Event event )
            {
                if(! last.isDisabled())
                {
                    lastButton.setClass("z-paging-btn");
                }
                else
                {
                    lastButton.setClass("z-paging-btn z-paging-btn-disd");
                }
            }
        } );
    }

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
        this.autohide = autohide;
    }

    public Intbox getPageNumber() {
        return pageNumber;
    }

    public int getTotalSize() {
        return getPagingModel().getTotalSize();
    }

    /**
     * This metod has to be called only on model through controller.
     * This metod always fails.
     * @param size
     * @throws WrongValueException
     */
    public void setTotalSize( final int size ) throws WrongValueException {
        throw new SecurityException( "You have to use paging model." );
    }

    public int getPageIncrement() {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void setPageIncrement( final int pginc ) throws WrongValueException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public boolean isDetailed() {
        return this.detailed;
    }

    public void setDetailed( final boolean detailed ) {
        this.detailed = detailed;
    }

    public boolean isCountPages() {
        return countPages;
    }

    public void setCountPages( final boolean countPages ) {
        this.countPages = countPages;
    }

    public int getPageSize() {
        return model == null ? pageSize : getPagingModel().getPageSize();
    }

    /**
     * Usable only for set page size from ZUL file.
     * @param size page size
     * @throws WrongValueException
     */
    public void setPageSize( final int size ) throws WrongValueException {
        pageSize = size;
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
        if ( controller != null && getPagingModel().getActualPage() != pg )
            controller.onPaging( pg );
    }

    protected String getInfoText() {

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

    /**
     * Component read model and write changed like page count or active page
     */
    public void fireChanges() {
        if ( isDetailed() )
        {
            detailInfo.setVisible(true);
            detailInfo.setValue( getInfoText() );
        }
        else
        {
            detailInfo.setVisible(false);
        }

        int newPageNumber = getPagingModel().getPageCount() == 0 ? 0 : getActivePage() + 1;
        pageNumber.setValue( newPageNumber );
        if ( newPageNumber > getPagingModel().getPageCount() )
            pageNumber.setStyle( "border: 2px red solid; background-image: none; background-color: pink; " );
        else pageNumber.setStyle( "" );

        if (!isKnownPageCount())
            totalPages.setValue("?");
        else
           totalPages.setValue("" + getPageCount());

        if (getActivePage() == 0)
        {
            firstButton.setClass("z-paging-btn z-paging-btn-disd");
            previousButton.setClass("z-paging-btn z-paging-btn-disd");
            first.setDisabled(true);
            previous.setDisabled(true);
        }
        else
        {
            firstButton.setClass("z-paging-btn");
            previousButton.setClass("z-paging-btn");
            first.setDisabled(false);
            previous.setDisabled(false);
        }

        if (getActivePage() == getPageCount()-1)
        {
            nextButton.setClass("z-paging-btn z-paging-btn-disd");
            lastButton.setClass("z-paging-btn z-paging-btn-disd");
            next.setDisabled(true);
            last.setDisabled(true);
        }
        else
        {
            nextButton.setClass("z-paging-btn");
            lastButton.setClass("z-paging-btn");
            next.setDisabled(false);
            last.setDisabled(false);
        }


        if (!isKnownPageCount())
        {
            nextButton.setClass("z-paging-btn");
            lastButton.setClass("z-paging-btn z-paging-btn-disd");
            next.setDisabled(false);
            last.setDisabled(true);
        }

    }

    public String getInfoTags() {
        if ( getPagingModel().getTotalSize() == 0 )
            return "";
        final StringBuffer sb = new StringBuffer( 512 );
        sb.append( "<div id=\"" ).append( getUuid() ).append( "!info\" class=\"" ).append( getZclass() ).append( "-info\">" ).append( getInfoText() ).append( "</div>" );
        return sb.toString();
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

    protected boolean isBothPaging() {
        final Component parent = getParent();
        if ( parent instanceof Paginated )
            return "both".equals( ( (Paginated) parent ).getPagingPosition() );
        return false;
    }

    public boolean isKnownPageCount() {
        return getPagingModel().isKnownPageCount();
    }
}
