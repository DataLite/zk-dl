package cz.datalite.zk.components.list.window.controller;

import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxFilterController;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.lovbox.DLLovboxController;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.components.profile.DLListboxProfile;
import cz.datalite.zk.components.profile.DLListboxProfileCategory;
import cz.datalite.zk.components.profile.DLProfileManager;
import cz.datalite.zk.components.profile.impl.DLListboxProfileCategoryImpl;
import cz.datalite.zk.events.SaveProfileEvent;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the popup window used to create/edit profile.
 */
public class ListboxProfileEditController {

    // active profile
    private DLListboxProfile profile;

    // list of categories minus categories from profile
    List<DLListboxProfileCategory> profileCategories;

    // main window component
    private Component view;

    // manager component - provides settings
    private DLProfileManager dlProfileManager;

    // if there are no categories defined, do not show the lovbox
    private boolean categoriesDefined;

    @Wire("button")
    private List<Button> buttons;

    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view,
                     @ExecutionArgParam("profile") DLListboxProfile profile,
                     @ExecutionArgParam("categories") List<DLListboxProfileCategory> profileCategories,
                     @ExecutionArgParam("dlProfileManager") DLProfileManager dlProfileManager) {
        this.profile = profile;

        // init categories
        if (profileCategories != null)
            this.profileCategories = new ArrayList<>(profileCategories);
        else
            this.profileCategories = new ArrayList<>();

        categoriesDefined = !this.profileCategories.isEmpty();

        this.profileCategories.removeAll(profile.getCategories());
        sortCategories();

        this.view = view;
        this.dlProfileManager = dlProfileManager;
    }

    @AfterCompose
    public void compose() {
        Selectors.wireComponents(this.view, this, false);

        for (Button button : this.buttons) {
            button.setMold(this.dlProfileManager.getButtonMold());
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
                Labels.getLabel("listbox.profileManager.delete.confirm", new String[]{this.profile.getName()}),
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

    @Command
    @NotifyChange("profile")
    public void addCategory(@BindingParam("category") DLListboxProfileCategory category) {
        profile.addCategory(category);
        profileCategories.remove(category);
        categoryLovbox.invalidateListboxModel();
    }

    @Command
    @NotifyChange("profile")
    public void removeCategory(@BindingParam("category") DLListboxProfileCategory category) {
        profile.removeCategory(category);
        profileCategories.add(category);
        sortCategories();
        categoryLovbox.invalidateListboxModel();
    }

    private void sortCategories() {
        Collections.sort(profileCategories, new DLListboxProfileCategoryImpl.NameComparator());
    }


    public DLListboxProfile getProfile() {
        return profile;
    }

    public void setProfile(DLListboxProfile profile) {
        this.profile = profile;
    }

    // lovbox to add selected item
    DLLovboxController<DLListboxProfileCategory> categoryLovbox = new DLLovboxGeneralController<>(
            new DLListboxFilterController<DLListboxProfileCategory>() {
                @Override
                protected List<DLListboxProfileCategory> loadData() {
                    return profileCategories;
                }
            });

    public DLLovboxController<DLListboxProfileCategory> getCategoryLovbox() {
       return categoryLovbox;
    }

    public DLProfileManager getDlProfileManager() {
        return dlProfileManager;
    }

    public boolean isCategoriesDefined() {
        return categoriesDefined;
    }
}
