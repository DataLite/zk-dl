package cz.datalite.zk.components.list.window.controller;

import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.events.SaveProfileEvent;

/**
 * Controller for the popup window used to create/edit profile.
 */
public class ListboxProfileEditController {

	private DLListboxProfile profile;
	private Component view;
	private String buttonMold;
	private Converter<Radio, Boolean, Radiogroup> booleanConverter = new BooleanConverter();
	
	@Wire("button")
	private List<Button> buttons;

	@Init
	public void init(@ContextParam(ContextType.VIEW) Component view, @ExecutionArgParam("profile") DLListboxProfile profile, @ExecutionArgParam("buttonMold") String buttonMold) {
		this.profile = profile;
		this.view = view;
		this.buttonMold = buttonMold;
	}

	@AfterCompose
	public void compose() {
		Selectors.wireComponents(this.view, this, false);
		
		for (Button button : this.buttons) {
			button.setMold(this.buttonMold);
		}
	}

	@Command
	public void close() {
		this.view.detach();
	}
	
	@Command
	public void save(@BindingParam("saveFilterModel") Boolean saveFilterModel, @BindingParam("saveColumnModel") Boolean saveColumnModel) {
		Events.postEvent(new SaveProfileEvent(this.view, saveColumnModel, saveFilterModel));
		this.view.detach();
	}
	
	@Command
	public void delete() {
		Messagebox.show(
				Labels.getLabel("listbox.profileManager.delete.confirm", new String[] { this.profile.getName() }),
				Labels.getLabel("listbox.profileManager.delete.tooltip"), Messagebox.OK | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(final Event event) {
						if (event.getData().equals(Messagebox.OK)) {
							Events.postEvent(new Event("onDelete", view, null));
							view.detach();
						}
					}
				});
	}

	public DLListboxProfile getProfile() {
		return profile;
	}

	public void setProfile(DLListboxProfile profile) {
		this.profile = profile;
	}

	public Converter<Radio, Boolean, Radiogroup> getBooleanConverter() {
		return booleanConverter;
	}

	private class BooleanConverter implements Converter<Radio, Boolean, Radiogroup> {
		@Override
		public Radio coerceToUi(Boolean beanProp, Radiogroup component, BindContext ctx) {
			if (beanProp != null && beanProp) {
				return (Radio) component.getFirstChild().getFirstChild();
			} else {
				return (Radio) component.getFirstChild().getLastChild();
			}
		}

		@Override
		public Boolean coerceToBean(Radio compAttr, Radiogroup component, BindContext ctx) {
			return Boolean.valueOf(compAttr.getValue().toString());
		}
	}	
	
}
