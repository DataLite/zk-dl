package cz.datalite.zk.components.profile;

import java.util.HashMap;
import java.util.Map;

import cz.datalite.helpers.StringHelper;
import cz.datalite.zk.bind.ZKBinderHelper;
import cz.datalite.zk.components.list.controller.DLProfileManagerController;
import cz.datalite.zk.components.list.view.DLListbox;
import cz.datalite.zk.components.list.view.DLListhead;
import cz.datalite.zk.components.list.view.DLListheader;
import cz.datalite.zk.components.lovbox.DLLovbox;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.components.lovbox.DLLovboxPopupComponentPosition;
import org.zkoss.util.resource.Labels;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Bar with advanced tools for managing the listbox profiles.
 */
public class DLProfileManager<T> extends Hlayout {

	private static final long serialVersionUID = 1L;
	
	protected static final String CONST_EVENT = Events.ON_CLICK;
    protected static final String CONST_DEFAULT_ICON_PATH = "~./dlzklib/img/";
    protected static final String CONST_IMAGE_SIZE = "20px";

    /**
	 * Default profile will be loaded from persistent storage and applied to
	 * listbox (onCreate) if set to true.
	 */
    private boolean applyDefaultProfile = false;
    
    /**
	 * Height of band popup (inner listbox).
	 */
    private String popupHeight = "300px";
    
    /**
	 * Label for edit button.
	 */
    private String editButtonLabel = Labels.getLabel("listbox.profileManager.edit");
    
    /**
	 * Image for edit button (image is used instead of button if not empty).
	 */
    private String editButtonImage;
    
	/**
	 * Width of edit cell.
	 */
	private String editButtonWidth;
    
    /**
	 * Mold used for all buttons (default,os,trendy).
	 */
    private String buttonMold = "trendy";
    
    /**
   	 * Button to create new profile is visible when set to true.
   	 */
    private boolean showNewButton = true;
    
    /**
 	 * Common (public) profile can be created if set to true.
 	 *
 	 */
     private boolean allowCreatePublic = true;
    
    /**
	 * Profile type column (personal / common) is visible when set to true.
	 */
    private boolean showType = true;
    
    /**
	 * Default profile column is visible when set to true.
	 */
    private boolean showDefault = true;

    /**
     * Show column settings checkbox.
     */
    private boolean showColumnSettings = true;

    /**
     * Show filter settings checkbox.
     */
    private boolean showFilterSettings = true;

	private DLProfileManagerController<T> controller;
	private final DLLovbox<DLListboxProfile> profilesLovbox;
	private final DLListbox profilesListbox;

	public DLProfileManager() {
		super();

		// init lovbox
		this.profilesLovbox = new DLLovbox<>();
		this.profilesLovbox.setCreateQuickFilter(false);
		this.profilesLovbox.setQuickFilterAll(false);
		this.profilesLovbox.setCreatePaging(false);
		this.profilesLovbox.setClearButton(true);
		this.profilesLovbox.setLabelProperty("name");
		this.profilesLovbox.setSearchProperty("name");
		this.profilesLovbox.setMultiple(false);
        this.profilesLovbox.setWatermark(Labels.getLabel("listbox.profileManager.profile.watermark"));

		
		// init bandpop and listbox inside lovbox
		this.profilesListbox = new DLListbox();
		this.profilesListbox.setStyle("overflow-y:auto!important");
		this.profilesListbox.setSelectFirstRow(false);
				
		Bandpopup popup = new Bandpopup();
		popup.appendChild(this.profilesListbox);
		popup.setSclass("dl-listbox-manager-popup");
		this.profilesLovbox.appendChild(popup);
		
		// bar with buttons to manage profiles
		Hlayout buttonBar = new Hlayout();
		buttonBar.setStyle("text-align:right;padding:5px;");
		buttonBar.setSpacing("5px");
		
		this.createButtonWithTooltip(buttonBar, "listbox.profileManager.create", this.showNewButton, new EventListener<Event>() {
			public void onEvent(final Event event) {
				controller.onEditProfile(null);
				profilesLovbox.close();
			}
		});
		

        this.profilesLovbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                controller.onLoadProfile(true);
            }
        });

        this.profilesListbox.setNonselectableTags("*");  // only click on name cell is registered
		
		this.profilesLovbox.addComponentToPopup(DLLovboxPopupComponentPosition.POSITION_BOTTOM, buttonBar);
		this.appendChild(this.profilesLovbox);
    }    

	public void setProfilesLovboxController(DLLovboxGeneralController<DLListboxProfile> controller) {
		// FIXME: SPM-11 ZK8 this method cause JS error in rendering component (wgt variable is undefined)
		this.profilesListbox.setHeight(this.popupHeight);
		
		final Listhead head = new DLListhead();
		
		DLListheader header = new DLListheader();
		header.setLabel("ID");
		header.setVisible(false);
		head.appendChild(header);
		
		header = new DLListheader();
		header.setLabel("");
		header.setSort("auto(publicProfile)");
		header.setWidth("24px");
		header.setVisible(this.showType);
		header.setAlign("center");
		head.appendChild(header);
		
		header = new DLListheader();
		header.setLabel(Labels.getLabel("listbox.profileManager.profile.name"));
		header.setSort("auto(name)");		
		header.setSortDirection("ascending");
		head.appendChild(header);
		
		header = new DLListheader();
		header.setLabel("");
        header.setTooltiptext(Labels.getLabel("listbox.profileManager.profile.default"));
		header.setSort("auto(defaultProfile)");
		header.setWidth("32px");
		header.setVisible(this.showDefault);
		header.setAlign("center");
		head.appendChild(header);
		
		header = new DLListheader();
		header.setLabel("");
		if (!StringHelper.isNull(this.editButtonWidth)) {
			header.setWidth(this.editButtonWidth);
		}
		head.appendChild(header);
		
		this.profilesListbox.appendChild(head);		
		
		if (ZKBinderHelper.version(this) == 1) {
			this.profilesListbox.setItemRenderer(new ListboxListitemRenderer());
		} else if (ZKBinderHelper.version(this) == 2) {
			this.profilesListbox.setTemplate("model", new ListboxTemplate());
		} 
		
		this.profilesLovbox.setController(controller);	
		this.profilesLovbox.afterCompose();
	}

	public void setController(final DLProfileManagerController<T> controller) {
		this.controller = controller;
	}

	public boolean isApplyDefaultProfile() {
		return applyDefaultProfile;
	}

	public void setApplyDefaultProfile(boolean applyDefaultProfile) {
		this.applyDefaultProfile = applyDefaultProfile;
	}
	
	public String getPopupHeight() {
		return popupHeight;
	}

	public void setPopupHeight(String popupHeight) {
		this.popupHeight = popupHeight;
	}

	public String getEditButtonLabel() {
		return editButtonLabel;
	}

	public void setEditButtonLabel(String editButton) {
		this.editButtonLabel = editButton;
	}

	public String getEditButtonImage() {
		return editButtonImage;
	}

	public void setEditButtonImage(String editButtonImage) {
		this.editButtonImage = editButtonImage;
	}

	public String getButtonMold() {
		return buttonMold;
	}

	public void setButtonMold(String buttonMold) {
		this.buttonMold = buttonMold;
	}
	
	public String getEditButtonWidth() {
		return editButtonWidth;
	}

	public void setEditButtonWidth(String editButtonWidth) {
		this.editButtonWidth = editButtonWidth;
	}
	
	public boolean isAllowCreatePublic() {
		return allowCreatePublic;
	}

	public void setAllowCreatePublic(boolean allowCreatePublic) {
		this.allowCreatePublic = allowCreatePublic;
	}

	public boolean isShowNewButton() {
		return showNewButton;
	}

	public void setShowNewButton(boolean showNewButton) {
		this.showNewButton = showNewButton;
	}

	public boolean isShowType() {
		return showType;
	}

	public void setShowType(boolean showType) {
		this.showType = showType;
	}

	public boolean isShowDefault() {
		return showDefault;
	}

	public void setShowDefault(boolean showDefault) {
		this.showDefault = showDefault;
	}

    public boolean isShowColumnSettings() {
        return showColumnSettings;
    }

    public void setShowColumnSettings(boolean showColumnSettings) {
        this.showColumnSettings = showColumnSettings;
    }

    public boolean isShowFilterSettings() {
        return showFilterSettings;
    }

    public void setShowFilterSettings(boolean showFilterSettings) {
        this.showFilterSettings = showFilterSettings;
    }

    public Button createButtonWithTooltip(Component parent, String labelKey, boolean visible, EventListener<Event> listener) {
		final Button button = new Button(Labels.getLabel(labelKey));
		button.setTooltiptext(Labels.getLabel(labelKey + ".tooltip"));
		button.setVisible(visible);
		button.setMold(this.getButtonMold());

		button.addEventListener(CONST_EVENT, listener);
		parent.appendChild(button);

		return button;
	}
	
	public HtmlBasedComponent createEditButtonWithTooltip(Component parent, boolean visible, EventListener<Event> listener) {
		final HtmlBasedComponent button;
		
		if (StringHelper.isNull(this.editButtonImage)) {
			button = new Button(this.editButtonLabel);
			button.setMold(this.getButtonMold());
		} else {
			button = new Image(this.editButtonImage);			
		}
		button.setSclass("selectable");
		button.setTooltiptext(Labels.getLabel("listbox.profileManager.edit.tooltip"));
		button.setVisible(visible);		

		button.addEventListener(CONST_EVENT, listener);
		parent.appendChild(button);

		return button;
	}

	// template for binding version 2.0
	private class ListboxTemplate implements Template {

		@SuppressWarnings("rawtypes")
		@Override
		public Component[] create(Component parent, Component insertBefore,	VariableResolver resolver, Composer composer) {
			final Listitem listitem = new Listitem();
			
			// append to the parent
			if (insertBefore == null) {
				parent.appendChild(listitem);
			} else {
				parent.insertBefore(listitem, insertBefore);
			}

			// create template components & add binding expressions
			final Listcell idCell = new Listcell();
			idCell.setVisible(false);
			listitem.appendChild(idCell);
			ZKBinderHelper.registerAnnotation(idCell, "label", "load", "item.id");
			
			// profile type cell 
			final Listcell typeCell = new Listcell();	
			typeCell.setVisible(showType);
			typeCell.setSclass("nonselectable");
			listitem.appendChild(typeCell);			
			ZKBinderHelper.registerAnnotation(typeCell, "label", "load", "item.publicProfile ? '" 
				+ Labels.getLabel("listbox.profileManager.profile.short.public") + "' : '" + Labels.getLabel("listbox.profileManager.profile.short.private") + "'");
			ZKBinderHelper.registerAnnotation(typeCell, "tooltiptext", "load", "item.publicProfile ? '"
				+ Labels.getLabel("listbox.profileManager.profile.public") + "' : '" + Labels.getLabel("listbox.profileManager.profile.private") + "'");
			// profile name cell and click handler
			final Listcell nameCell = new Listcell();
			
			nameCell.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					Listitem selectedListitem = (Listitem) nameCell.getParent();
					profilesLovbox.getController().setSelectedItem((DLListboxProfile) selectedListitem.getValue());
					profilesLovbox.close();
				}
			});
			
			listitem.appendChild(nameCell);
			ZKBinderHelper.registerAnnotation(nameCell, "label", "load", "item.name");
			
			// default profile cell 
			final Listcell defaultCell = new Listcell();
			defaultCell.setVisible(showDefault);
			defaultCell.setSclass("nonselectable");
			listitem.appendChild(defaultCell);
			
			Checkbox defaultCellCheckbox = new Checkbox();
			defaultCellCheckbox.setDisabled(true);
			defaultCell.appendChild(defaultCellCheckbox);
            defaultCell.setTooltiptext(Labels.getLabel("listbox.profileManager.profile.default"));
			ZKBinderHelper.registerAnnotation(defaultCellCheckbox, "checked", "load", "item.defaultProfile");
			
			// button edit cell and nested button
			final Listcell buttonCell = new Listcell();
			buttonCell.setSclass("nonselectable");
			listitem.appendChild(buttonCell);
			
			final HtmlBasedComponent editBtn = DLProfileManager.this.createEditButtonWithTooltip(buttonCell, true, new EventListener<Event>() {
				public void onEvent(final Event event) {
					controller.onEditProfile((Long.valueOf(idCell.getLabel())));
				}
			});
			ZKBinderHelper.registerAnnotation(editBtn, "visible", "load", "item.editable");
			
			Component[] components = new Component[1];
			components[0] = listitem;

			return components;
		}

		@Override
		public Map<String, Object> getParameters() {
			Map<String, Object> parameters = new HashMap<>();
			// set binding variable
			parameters.put("var", "item");

			return parameters;
		}
	}
	
	// renderer for binding version 1.0
	private class ListboxListitemRenderer implements ListitemRenderer<DLListboxProfile> {

		@Override
		public void render(Listitem item, DLListboxProfile profile, int index) throws Exception {
			item.setValue(profile);
			
			final Listcell idCell = new Listcell(profile.getId().toString());
			item.appendChild(idCell);
			
			// profile type cell 
			final Listcell typeCell = new Listcell(profile.isPublicProfile() ? Labels.getLabel("listbox.profileManager.profile.short.public")
							: Labels.getLabel("listbox.profileManager.profile.short.private"));	
			typeCell.setVisible(showType);
			typeCell.setSclass("nonselectable");
			item.appendChild(typeCell);			
			
			// profile name cell and click handler
			final Listcell nameCell = new Listcell(profile.getName());
			
			nameCell.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					Listitem selectedListitem = (Listitem) nameCell.getParent();
					profilesLovbox.getController().setSelectedItem((DLListboxProfile) selectedListitem.getValue());
					profilesLovbox.close();
				}
			});
			
			item.appendChild(nameCell);
			
			// default profile cell 
			final Listcell defaultCell = new Listcell();	
			defaultCell.setVisible(showDefault);
			defaultCell.setSclass("nonselectable");
			item.appendChild(defaultCell);	
			
			Checkbox defaultCellCheckbox = new Checkbox();
			defaultCellCheckbox.setDisabled(true);
			defaultCellCheckbox.setChecked(profile.isDefaultProfile());
			defaultCell.appendChild(defaultCellCheckbox);						

			// button edit cell and nested button
			final Listcell buttonCell = new Listcell();
			buttonCell.setSclass("nonselectable");
			item.appendChild(buttonCell);
				
			DLProfileManager.this.createEditButtonWithTooltip(buttonCell, profile.isEditable(), new EventListener<Event>() {
				public void onEvent(final Event event) {
					controller.onEditProfile((Long.valueOf(idCell.getLabel())));
				}
			});
		}		
	}
}
