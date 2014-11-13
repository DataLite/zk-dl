package cz.datalite.zk.components.list.view;

import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.event.ColSizeEvent;
import org.zkoss.zul.event.ZulEvents;

/**
 * ZK component DLListheader is extended component from standard Listhead.
 * <ul>
 * <li>Change default sizable to {@code true}.</li>
 * <li>Added {@code onColSize} event handling with profile manager. See {@link DLListheader#setWidth()}.</li>
 * </ul>
 *
 * @author Jiri Bubnik
 */
public class DLListhead extends Listhead {

	public DLListhead() {
		super();
		setSizable(true);
		initOnColSize();
	}


	private void initOnColSize() {
		addEventListener(ZulEvents.ON_COL_SIZE, new EventListener<ColSizeEvent>() {
			@Override
			public void onEvent(ColSizeEvent event) throws Exception {
				DLColumnUnitModel model = ((DLListheader) event.getColumn()).model;
				if (model != null) {
					model.setWidth(event.getWidth());
				}
			}
		});
	}
}
