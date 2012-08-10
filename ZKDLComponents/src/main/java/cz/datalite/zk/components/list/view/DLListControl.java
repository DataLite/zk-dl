package cz.datalite.zk.components.list.view;

import cz.datalite.zk.components.paging.DLPaging;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;

/**
 * Component for managing listbox. There can be defined
 * which advanced components will be shown in the navigating
 * panel > listbox manager / paging / quick filter.
 *
 * @author Michal Pavlusek
 */
public class DLListControl extends Hbox
{
    // constants
    private static final int CONST_PAGE_SIZE = 100;
    
    // basic display settings
    private boolean paging = false;
    private boolean qfilter = true;
    private boolean manager = true;
    private int pageSize = CONST_PAGE_SIZE;
    private boolean countPages = true;
    private boolean autohide = false;
    private boolean quickFilterAll = true;
    private String quickFilterDefault;
    private String quickFilterButton;

    // style settings
    private String qFilterStyle;
    private String qFilterClass;
    private String managerStyle;
    private String managerClass;

    // variables with components
    private final DLQuickFilter qFilterComponent;
    private final DLListboxManager managerComponent;
    private final DLPaging pagingComponent;

    /** Everyting added by ZUL page goes here (see appendChild). */
    private final Hbox additionalContent;

    /** First separator of content. */
    private final Separator separator1;

    /** Just constructing the component
     * (to recognize own child components and user added components). */
    private boolean inConstruct = true;

    /** ZK-164 It says that the QuickFilter should use the Contains operator for quick filter base comparison */
    private boolean quickFilterContainsOnly;

    /**
     * Constructor creates all components.
     */
    public DLListControl()
    {
        super();
        setClass("z-paging");
        setWidth("100%");
        setHeight("28px");

        qFilterComponent = new DLQuickFilter();
        qFilterComponent.setParent( this );

        separator1 = new Separator();
        separator1.setHflex("1");
        separator1.setParent(this);

        additionalContent = new Hbox();
        additionalContent.setParent(this);

        Separator separator = new Separator();
        separator.setWidth("5px");
        separator.setParent(this);

        managerComponent = new DLListboxManager();
        managerComponent.setParent( this );

        separator = new Separator();
        separator.setParent(this);

        pagingComponent = new DLPaging();
        pagingComponent.setParent( this );
        pagingComponent.setVisible(paging);

        separator = new Separator();
        separator.setParent(this);

        inConstruct = false;
    }

    public void init()
    {
        initPaging();
        initQuickFilter();
    }

    private void initQuickFilter()
    {
        qFilterComponent.setQuickFilterAll( quickFilterAll );
        qFilterComponent.setQuickFilterDefault( quickFilterDefault );
        qFilterComponent.setQuickFilterButton(quickFilterButton);
        qFilterComponent.setQuickFilterUseContainsOnly( quickFilterContainsOnly );
    }

    private void initPaging()
    {
        pagingComponent.setPageSize( pageSize );
        pagingComponent.setAutohide( autohide );
        pagingComponent.setCountPages( countPages );
    }

    /**
     * Append child to additionalContent hbox in the center.
     * @param child child to add.
     * @return additionalContent.appendChild(child).
     */
    @Override
    public boolean appendChild(Component child)
    {
        if (inConstruct)
            return super.appendChild(child);
        else
            return additionalContent.appendChild(child);
    }

    @Override
    public boolean insertBefore(Component newChild, Component refChild)
    {
        if (inConstruct)
        {
            return super.insertBefore(newChild, refChild);
        }
        else
        {
            additionalContent.setHflex("1");
            separator1.setHflex("0");
            return additionalContent.insertBefore(newChild, refChild);
        }
    }


    /************************** SETTERS & GETTERS *****************************/

    public boolean isPaging() {
        return paging;
    }

    public void setPaging( final boolean paging ) {
        this.paging = paging;
        pagingComponent.setVisible(paging);
    }

    public boolean isQfilter() {
        return qfilter;
    }

    public void setQfilter( final boolean qfilter ) {
        this.qfilter = qfilter;
        qFilterComponent.setVisible(qfilter);
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager( final boolean manager ) {
        this.manager = manager;
        managerComponent.setVisible(manager);
    }

    public void setPageSize( final int pageSize ) {
        this.pageSize = pageSize;
        pagingComponent.setPageSize( pageSize );
    }

    public void setAutohide( final boolean autohide ) {
        this.autohide = autohide;
    }

    public void setCountPages( final boolean countPages ) {
        this.countPages = countPages;
    }

    public DLQuickFilter getQFilterComponent() {
        return qFilterComponent;
    }

    public DLListboxManager getManagerComponent() {
        return managerComponent;
    }

    public DLPaging getPagingComponent() {
        return pagingComponent;
    }

    public String getQFilterStyle() {
        return qFilterStyle;
    }

    public void setQFilterStyle( final String qFilterStyle ) {
        this.qFilterStyle = qFilterStyle;
    }

    public String getQFilterClass() {
        return qFilterClass;
    }

    public void setQFilterClass( final String qFilterClass ) {
        this.qFilterClass = qFilterClass;
    }

    public String getManagerStyle() {
        return managerStyle;
    }

    public void setManagerStyle( final String managerStyle ) {
        this.managerStyle = managerStyle;
    }

    public String getManagerClass() {
        return managerClass;
    }

    public void setManagerClass( final String managerClass ) {
        this.managerClass = managerClass;
    }

    public void setQuickFilterAll( final boolean quickFilterAll ) {
        this.quickFilterAll = quickFilterAll;
    }

    public void setQuickFilterDefault( final String quickFilterDefault ) {
        this.quickFilterDefault = quickFilterDefault;
    }

    public String getQuickFilterButton() {
        return quickFilterButton;
    }

    public void setQuickFilterButton(String quickFilterButton) {
        this.quickFilterButton = quickFilterButton;
    }

    public void setQuickFilterContainsOnly( boolean quickFilterContainsOnly ) {
        this.quickFilterContainsOnly = quickFilterContainsOnly;
    }
}
