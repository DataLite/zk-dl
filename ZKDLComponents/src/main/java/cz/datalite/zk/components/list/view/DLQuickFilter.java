package cz.datalite.zk.components.list.view;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.impl.InputElement;

/**
 * Component for tool which allows user to quickly filter in the listbox.
 * @author Karel Cemus
 * @author Jiri Bubnik
 */
public class DLQuickFilter extends InputElement {

	/**
	 * Quick filter search value will be cleared after search {@link DLColumnUnitModel column} (property) change. (If {@link Library#getProperty(String) library property} is set to <code>true</code>.)
	 */
	public static final String LIBRARY_CLEAR_VALUE_AFTER_COLUMN_CHANGE = "zk-dl.quickFilter.clearValueAfterColumnChange";

    static {
        addClientEvent(DLQuickFilter.class, "onOpenPopup", CE_IMPORTANT|CE_NON_DEFERRABLE);
    }

    /** Include "All" option in column selection. */
    protected boolean quickFilterAll = true;
    /** Default column for filtering */
    protected String quickFilterDefault;
    /** If set, show button with this label instead of magnifier glass icon. */
    protected String quickFilterButton;
    /** If set, show button with custom css class. */
    protected String quickFilterButtonClass;
    /** Should the filter run for onchanging event (use only for fast queries) */
    protected boolean autocomplete = false;

    // Controller
    protected DLQuickFilterController controller;

    // Model
    protected List<Entry<DLColumnUnitModel, String>> model;

    // Search label
    private String label;

    // View
    protected final Menupopup popup;

    // sync value from client
    EventListener valueListener = new EventListener<Event>() {
        public void onEvent(Event event) throws Exception {
            if (controller != null)
                controller.getModel().setValue(getValue());
        }
    };

    // do search (registered for ON_OK and if autocomplete than on ON_CHANGING)
    EventListener searchListener = new EventListener<Event>() {
        public void onEvent(Event event) throws Exception {
            if (event instanceof InputEvent) // synchonize ON_CHANING event
                controller.getModel().setValue(((InputEvent) event).getValue());
            onQuickFilter();
        }
    };


    // create popup and register events
    public DLQuickFilter() {
    	super();

        popup = new Menupopup();
        popup.setSclass("z-quickfilter-popup");
        popup.setStyle("z-index: 100000 !important;");

        addEventListener(Events.ON_CHANGE, valueListener);
        addEventListener(Events.ON_OK, searchListener);
    }

    /**
     * Set quick filter controller - propagate model changes to the controller model.
     * @param controller controller
     */
    public void setController( final DLQuickFilterController controller ) {
    	this.controller = controller;
    }

    public void fireChanges() {
        // recreate popup menu according to the model
        popup.getChildren().clear();
		if (quickFilterAll) {
			popup.appendChild(new Menuitem(Labels.getLabel("quickFilter.menu.all")) {
				{
					setValue(cz.datalite.zk.components.list.filter.QuickFilterModel.CONST_ALL);
					addEventListener(Events.ON_CLICK, new SelectMenuItemEventListener(this));
				}
			});
		}

		if (model == null) {
			return;
		}

        for ( final Entry<DLColumnUnitModel, String> entry : model ) {
        	// ZK-197 - include even invisible columns, use quickFilter="false" to hide a column

			popup.appendChild(new Menuitem(entry.getValue()) {
				private static final long serialVersionUID = -2228715092126157753L;
				{
					setAttribute("model", entry.getKey());
					setValue(entry.getKey().getColumn());
					addEventListener(Events.ON_CLICK, new SelectMenuItemEventListener(this));
				}
			});
		}

        this.setActiveFilter();
        this.setValue(controller.getModel().getValue());
    }

    // Actual button selection
    protected void setActiveFilter() {

        final DLColumnUnitModel unit = controller.getModel().getModel();
        String column = unit != null ? unit.getColumn() : getQuickFilterDefault();
        
		int index = quickFilterAll ? 1 : 0;
		for (Entry<DLColumnUnitModel, String> entry : model) {

			if (EqualsHelper.isEqualsNull(entry.getKey().getColumn(), column)) {
				setActiveFilter((Menuitem) popup.getChildren().get(index));
				return;
			}

            index++;
		}
		// model wasn't found in the menu list
		if (popup.getChildren().size() > 0) {
			setActiveFilter((Menuitem) popup.getFirstChild());
			setVisible(true);
		} else {
			setVisible(false);
		}
    }

    protected void setActiveFilter( final Menuitem item ) {
        setLabel(item.getLabel());
        controller.getModel().setKey(item.getValue());
        controller.getModel().setModel((DLColumnUnitModel) item.getAttribute("model"));
        setFocus(true);
    }

    // Event onOK or onChaning - do search
    public void onQuickFilter() {
        if (!controller.validateQuickFilter()) {
            throw new WrongValueException(this, Labels.getLabel("quickFilter.validation.failed"));
        }

        controller.onQuickFilter();
    }

    public void setModel( final List<Entry<DLColumnUnitModel, String>> model ) {
		for (final Iterator<Entry<DLColumnUnitModel, String>> it = model.iterator(); it.hasNext();) {
			final Entry<DLColumnUnitModel, String> entry = it.next();
			if (entry.getValue() == null || Strings.isEmpty(entry.getValue())) {
				it.remove();
			}
		}
		this.model = model;
    }

	public void setQuickFilterAll(final boolean quickFilterAll) {
		this.quickFilterAll = quickFilterAll;
	}

	public void setQuickFilterDefault(final String quickFilterDefault) {
		this.quickFilterDefault = quickFilterDefault;
	}

	public String getQuickFilterDefault() {
		return this.quickFilterDefault;
	}

	class SelectMenuItemEventListener implements EventListener<Event> {

		protected final Menuitem item;
		protected final String column;
		protected final DLColumnUnitModel model;

		public SelectMenuItemEventListener(final Menuitem item) {
			this.item = item;
			this.column = item.getValue();
			this.model = (DLColumnUnitModel) item.getAttribute("model");
		}

		public void onEvent(final org.zkoss.zk.ui.event.Event event) {
			if (Boolean.valueOf(Library.getProperty(LIBRARY_CLEAR_VALUE_AFTER_COLUMN_CHANGE, Boolean.FALSE.toString()))) {
				setValue("");
				controller.getModel().setValue("");
			}

			controller.getModel().setKey(column);
			controller.getModel().setModel(model);
			setActiveFilter(item);
		}
	}

	@Override
	public String getSclass() {
		// listControll parent may contain default values for class
		if (getParent() instanceof DLListControl) {
			return super.getSclass() + " " + ((DLListControl) getParent()).getQFilterClass();
		} else {
			return super.getSclass();
		}
	}

	@Override
	public String getStyle() {
		// listControll parent may contain default values for style
		if (getParent() instanceof DLListControl) {
			return super.getStyle() + " " + ((DLListControl) getParent()).getQFilterStyle();
		} else {
			return super.getStyle();
		}
	}

    /**
     * If set, show button with this label instead of magnifier glass icon.
     */
    public String getQuickFilterButton() {
        return quickFilterButton;
    }
    /**
     * If set, show button with custom css class.
     */
    public String getQuickFilterButtonClass() {
        return quickFilterButtonClass;
    }

    /**
     * If set, show button with this label instead of magnifier glass icon.
     * @param quickFilterButton label for button.
     */
    public void setQuickFilterButton(String quickFilterButton) {
        this.quickFilterButton = quickFilterButton;
        smartUpdate("quickFilterButton", quickFilterButton);
    }
    /**
     * If set, show button with custom css class.
     * @param quickFilterButtonClass label for button.
     */
    public void setQuickFilterButtonClass(String quickFilterButtonClass) {
        this.quickFilterButtonClass = quickFilterButtonClass;
        smartUpdate("quickFilterButtonClass", quickFilterButtonClass);
    }

    /** Should the filter run for onchanging event (use only for fast queries) */
    public boolean isAutocomplete() {
        return autocomplete;
    }

    /** Should the filter run for onchanging event (use only for fast queries) */
    public void setAutocomplete(boolean autocomplete) {
        // if changed, attach or detach listener
        if (this.autocomplete != autocomplete) {
            if (autocomplete)
                addEventListener(Events.ON_CHANGING, searchListener);
            else
                removeEventListener(Events.ON_CHANGING, searchListener);
        }

        this.autocomplete = autocomplete;
    }

    /**
     * Label is selected value for the key.
     * @param label selected value for the key.
     */
    protected void setLabel(String label) {
        this.label = label;
        smartUpdate("label", label);
    }


    //-- super --//
    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);

        render(renderer, "label", label);
        render(renderer, "quickFilterButton", quickFilterButton);
        render(renderer, "quickFilterButtonClass", quickFilterButtonClass);
    }

    @Override
    protected Object coerceFromString(String value) throws WrongValueException {
        return value != null ? value: "";
    }

    @Override
    protected String coerceToString(Object value) {
        return value != null ? (String)value: "";
    }

    /** Returns the value.
     * The same as {@link #getText}.
     * <p>Default: "".
     * @exception WrongValueException if user entered a wrong value
     */
    public String getValue() throws WrongValueException {
        return getText();
    }
    /** Sets the value.
     *
     * @param value the value; If null, it is considered as empty.
     * @exception WrongValueException if value is wrong
     */
    public void setValue(String value) throws WrongValueException {
        setText(value);
    }

    @Override
    public void service(AuRequest request, boolean everError) {
        final String cmd = request.getCommand();
        if (cmd.equals("onOpenPopup")) {
            // page was not set in constructor, delay to the show event
            popup.setParent(DLQuickFilter.this);
            popup.open(DLQuickFilter.this);
        }

        super.service(request, everError);
    }

    // allow add popup below quickfilter
    protected boolean isChildable() {
        return true;
    }
}
