package cz.datalite.zk.events;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class SaveProfileEvent extends Event {

	private static final long serialVersionUID = -2703176443939222196L;

	public static final String ON_SAVE_PROFILE = "onSaveProfile";

	private boolean saveColumnModel;
	private boolean saveFilterModel;

	public SaveProfileEvent(Component target, boolean saveColumnModel, boolean saveFilterModel) {
		super(ON_SAVE_PROFILE, target);
		
		this.saveColumnModel = saveColumnModel;
		this.saveFilterModel = saveFilterModel;
	}

	public boolean isSaveColumnModel() {
		return saveColumnModel;
	}

	public void setSaveColumnModel(boolean saveColumnModel) {
		this.saveColumnModel = saveColumnModel;
	}

	public boolean isSaveFilterModel() {
		return saveFilterModel;
	}

	public void setSaveFilterModel(boolean saveFilterModel) {
		this.saveFilterModel = saveFilterModel;
	}

}
