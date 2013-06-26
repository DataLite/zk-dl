package cz.datalite.zk.components.list.view;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;

/**
 * Component for managing listbox. There can be defined
 * which advanced components will be shown in the navigating
 * panel > listbox manager / quick filter.
 *
 * @author Michal Pavlusek
 */
public class DLListControl extends Div {

    // basic display settings
    private boolean qfilter = true;
    private boolean manager = true;

    // variables with components
    private final DLQuickFilter qFilterComponent;
    private final DLListboxManager managerComponent;

    /** Everyting added by ZUL page goes here (see appendChild). */
    private final Hbox additionalContent;

    /** Just constructing the component (to recognize own child components and user added components). */
    private boolean inConstruct = true;

    /** ZK-164 It says that the QuickFilter should use the Contains operator for quick filter base comparison */
    private Boolean wysiwyg = null;

    /**
     * Constructor creates all components.
     */
    public DLListControl() {
        super();

        setZclass("z-listcontrol");

        qFilterComponent = new DLQuickFilter();
        qFilterComponent.setParent( this );

        additionalContent = new Hbox();
        additionalContent.setParent(this);
        additionalContent.setZclass("z-listcontrol-aux-content");
        additionalContent.setAlign("center");

        managerComponent = new DLListboxManager();
        managerComponent.setParent(this);

        Div cleaner = new Div();
        cleaner.setZclass("z-dlzklib-clear");
        cleaner.setParent(this);

        inConstruct = false;
    }

    public void init() {
    }


    /**
     * Append child to additionalContent hbox in the center.
     * @param child child to add.
     * @return additionalContent.appendChild(child).
     */
    @Override
    public boolean appendChild(Component child) {
        if (inConstruct) {
            return super.appendChild(child);
        } else {
            return additionalContent.appendChild(child);
        }
    }

    @Override
    public boolean insertBefore(Component newChild, Component refChild) {
        if (inConstruct) {
            return super.insertBefore(newChild, refChild);
        } else {
            return additionalContent.insertBefore(newChild, refChild);
        }
    }


    /************************** SETTERS & GETTERS *****************************/

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

    public DLQuickFilter getQFilterComponent() {
        return qFilterComponent;
    }

    public DLListboxManager getManagerComponent() {
        return managerComponent;
    }


    public String getQFilterStyle() {
        return qFilterComponent.getStyle();
    }

    public void setQFilterStyle( final String qFilterStyle ) {
        this.qFilterComponent.setStyle(qFilterStyle);
    }

    public String getQFilterClass() {
        return qFilterComponent.getSclass();
    }

    public void setQFilterClass( final String qFilterClass ) {
        this.qFilterComponent.setSclass(qFilterClass);
    }

    public String getManagerStyle() {
        return this.managerComponent.getStyle();
    }

    public void setManagerStyle( final String managerStyle ) {
        this.managerComponent.setStyle(managerStyle);
    }

    public String getManagerClass() {
        return this.managerComponent.getSclass();
    }

    public void setManagerClass( final String managerClass ) {
        this.managerComponent.setSclass(managerClass);
    }

    public void setQuickFilterAll( final boolean quickFilterAll ) {
        this.qFilterComponent.setQuickFilterAll(quickFilterAll);
    }

    public void setQuickFilterDefault( final String quickFilterDefault ) {
        this.qFilterComponent.setQuickFilterDefault(quickFilterDefault);
    }

    public String getQuickFilterButton() {
        return qFilterComponent.getQuickFilterButton();
    }

    public void setQuickFilterButton(String quickFilterButton) {
        this.qFilterComponent.setQuickFilterButton(quickFilterButton);
    }

    public void setWysiwyg( boolean wysiwyg ) {
        this.wysiwyg = wysiwyg;
    }

    public Boolean isWysiwyg() {
        return wysiwyg;
    }

    public boolean isAutocomplete() {
        return qFilterComponent.isAutocomplete();
    }

    public void setAutocomplete(boolean autocomplete) {
        this.qFilterComponent.setAutocomplete(autocomplete);
    }
}
