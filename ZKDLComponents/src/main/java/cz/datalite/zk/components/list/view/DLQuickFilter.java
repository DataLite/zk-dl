package cz.datalite.zk.components.list.view;

import cz.datalite.helpers.EqualsHelper;
import cz.datalite.zk.bind.ZKBinderHelper;
import cz.datalite.zk.components.list.controller.DLQuickFilterController;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Component for tool which allows user to quickly filter in the listbox
 * @author Karel Cemus
 */
public class DLQuickFilter extends org.zkoss.zul.Hbox {

	private static final long serialVersionUID = 6888715542666042737L;
	
	// Controller
    protected DLQuickFilterController controller;
    
    // Model
    protected List<Entry<DLColumnUnitModel, String>> model;
    protected boolean quickFilterAll = true;
    protected String quickFilterDefault;
    
    // View
    protected final Menupopup popup;
    protected final Textbox textbox;
    protected final Label selector;
    
    // Constants
    protected static final String	CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String	CONST_IMAGE_SIZE = "20px";
    protected static final String	CONST_IMAGE_STYLE = "";
    private static final String		CONST_POINTER_STYLE = "cursor: pointer;";

    // Variables
    private Hbox	parent;
    private Image	activateFilter;

    public DLQuickFilter() {
    	super();
    	
    	// quickfilter components are composed into this hbox
        parent = new Hbox();
        parent.setSclass("datalite-listbox-qfiltr");
        this.appendChild(parent);

        // selector is currently selected menuitem text  
        selector = new Label();
		selector.setSclass("datalite-listbox-qfiltr-selector");
		selector.setStyle(CONST_POINTER_STYLE);
		selector.setTooltiptext(Labels.getLabel("quickFilter.tooltip.filterRange"));
        selector.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(final Event event) {
                popup.open(selector);
            }
        });        
		parent.appendChild(selector);

		// popup with items applicable to filtering
		popup = new Menupopup();
		popup.setSclass("datalite-listbox-qfiltr-popup");
		popup.setStyle("z-index: 100000 !important;");
		parent.appendChild(popup);

		// image next to selector  
		final Image open = new Image();
		open.setSclass("datalite-listbox-qfiltr-open");
		open.setTooltiptext(Labels.getLabel("quickFilter.tooltip.openFilter"));
		open.setSrc(CONST_DEFAULT_ICON_PATH + "open.png");
		open.setStyle(CONST_POINTER_STYLE);
		open.setWidth("16px");
		open.setHeight("10px");
		open.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(final Event event) {
				popup.open(selector);
			}
		});
		parent.appendChild(open);

		// textbox for quickfilter value
        textbox = new Textbox();
        textbox.setSclass( "datalite-listbox-qfiltr-textbox" );
		addEventListener(Events.ON_OK, new EventListener<Event>() {
			public void onEvent(final Event event) {
				onQuickFilter();
			}
		});
		parent.appendChild(textbox);

		// magnifier image which activates (submit) filter
		activateFilter = new Image();
		activateFilter.setSclass("datalite-listbox-qfiltr-image");
		activateFilter.setSrc(CONST_DEFAULT_ICON_PATH + "search25x25.png");
		activateFilter.setStyle(CONST_IMAGE_STYLE);
		activateFilter.setWidth(CONST_IMAGE_SIZE);
		activateFilter.setHeight(CONST_IMAGE_SIZE);
		activateFilter.setStyle(CONST_POINTER_STYLE);
		activateFilter.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(final Event event) {
				onQuickFilter();
			}
		});
		appendChild(activateFilter);        
    }

    public void setController( final DLQuickFilterController controller ) {
    	this.controller = controller;
        setAttribute( getUuid() + "_model", controller, Component.COMPONENT_SCOPE);
        
        // register proper annotation based used version of binding
		if (ZKBinderHelper.version(textbox) == 1) {
			ZKBinderHelper.registerAnnotation(textbox, "value", "value", getUuid() + "_model.bindingModel.value");
		} else if (ZKBinderHelper.version(textbox) == 2) {
			ZKBinderHelper.registerAnnotation(textbox, "value", "bind", getUuid() + "_model.bindingModel.value");
		}
    }

    public void fireChanges() {
    	// odstraníme všechny položky
        while ( popup.getChildren().size() > 0 ) {
            popup.removeChild( popup.getLastChild() );
        }

        // znovu vytvoříme položky, podle modelu
		if (quickFilterAll) {
			popup.appendChild(new Menuitem(Labels.getLabel("quickFilter.menu.all")) {
				private static final long serialVersionUID = 4832675217567501552L;
				{
					setValue(cz.datalite.zk.components.list.filter.QuickFilterModel.CONST_ALL);
					addEventListener(Events.ON_CLICK, new SelectMenuItemEventListener(this));
				}
			});
		}

        for ( final Entry<DLColumnUnitModel, String> entry : model ) {
        	// filter allowed for all columns, use quickFilter="false" to disable, JIRA ref. ZK-197
//          // do not allow to filter by invisible column
//			if (!entry.getKey().isVisible()) {
//				continue;
//			}
            
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
    }

    protected void setActiveFilter() {
    	// Actual button selection
        final DLColumnUnitModel unit = controller.getBindingModel().getModel();
        
		int indexOfVisible = quickFilterAll ? 0 : -1;
		for (Entry<DLColumnUnitModel, String> entry : model) {
			// do not allow to filter by invisible column
			if (entry.getKey().isVisible())
				++indexOfVisible;
			else
				continue;

			if (EqualsHelper.isEqualsNull(entry.getKey(), unit)) {
				setActiveFilter((Menuitem) popup.getChildren().get(indexOfVisible));
				return;
			}
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
    	selector.setValue( item.getLabel() );        
        controller.getBindingModel().setKey(item.getValue());
        controller.getBindingModel().setModel((DLColumnUnitModel) item.getAttribute("model"));
    }

    public void onQuickFilter() {
        if (!controller.validateQuickFilter()) {
            throw new WrongValueException(textbox, Labels.getLabel("quickFilter.validation.failed"));
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

    /**
     * Setting focus on Quick Filter means focus on textbox
     * @param focus if true, textbox is selected
     */
    @Override
    public void setFocus(boolean focus) {
        textbox.setFocus(focus);
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
			controller.getBindingModel().setKey(column);
			controller.getBindingModel().setModel(model);
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
     * Setting filter button with label = parameter.
     * @param quickFilterButton label for button.
     */
	public void setQuickFilterButton(String quickFilterButton) {
		// whether i want filter button
		if (quickFilterButton != null) {
			activateFilter.setParent(null);

			final Button button = new Button();
			button.setLabel(quickFilterButton);
			button.setImage(CONST_DEFAULT_ICON_PATH + "search25x25.png");
			button.setClass("quick-filter-button");
			button.setStyle(CONST_POINTER_STYLE);
			button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				public void onEvent(final Event event) {
					onQuickFilter();
				}
			});
			parent.appendChild(button);

			((DLListControl) this.getParent()).setHeight("34px");
		}
	}
}
