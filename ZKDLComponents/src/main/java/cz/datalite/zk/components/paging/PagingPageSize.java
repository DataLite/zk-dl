package cz.datalite.zk.components.paging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.mesg.MZul;

/**
 * Component for paginator extension (page size control)
 */
public class PagingPageSize extends Div {

	private static final Logger logger = LoggerFactory.getLogger(PagingPageSize.class);

	private static final int MIN_PAGE_SIZE = 1;
	private static final int DEFAULT_MAX_PAGE_SIZE = 100;

	@WireVariable
	private DLPaging paging;

	@WireVariable
	private Integer maxPageSize = DEFAULT_MAX_PAGE_SIZE;

	private Combobox pageSizeCombo;

	public PagingPageSize() {
		super();

		setSclass("dl-paging-pagesize");

		Label label = new Label(Labels.getLabel("listbox.paging.pageSize", "Page size: "));
		this.appendChild(label);

		pageSizeCombo = new Combobox();
		createComboitems();

		pageSizeCombo.setWidth("50px");

		pageSizeCombo.addEventListener(Events.ON_CHANGE, new ChangeEventListener());
		this.addEventListener(Events.ON_CREATE, new CreateEventListener());

		this.appendChild(pageSizeCombo);
	}

	private void createComboitems() {
		if (maxPageSize <= 0) {
			throw new IllegalArgumentException("Argument maxPageSize must be positive! Current value: " + maxPageSize);
		}
		for (int i = 10; i <= maxPageSize; i += 10) {
			pageSizeCombo.appendChild(new Comboitem(String.valueOf(i)));
		}
	}

	public DLPaging getPaging() {
		return paging;
	}

	public void setPaging(DLPaging paging) {
		this.paging = paging;
	}

	public Integer getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(Integer maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	private class CreateEventListener implements EventListener<CreateEvent> {
		@Override
		public void onEvent(CreateEvent event) throws Exception {
			if (paging == null) {
				throw new NullPointerException("Attribute 'paging' with DLPaging is mandatory!");
			}

			List<Comboitem> items = pageSizeCombo.getItems();
			boolean contains = false;
			String pageSize = String.valueOf(paging.getPageSize());
			for (Comboitem item : items) {
				if (pageSize.equals(item.getLabel())) {
					contains = true;
				}
			}
			if (!contains) {
				pageSizeCombo.appendItem(pageSize);
			}

			pageSizeCombo.setValue(pageSize);
		}
	}

	private class ChangeEventListener implements EventListener<InputEvent> {
		@Override
		public void onEvent(InputEvent event) throws Exception {
			try {
				Integer size = Integer.valueOf(event.getValue());
				if (size < MIN_PAGE_SIZE || size > maxPageSize) {
					throw new WrongValueException(event.getTarget(), MZul.OUT_OF_RANGE, new Object[] { MIN_PAGE_SIZE + " - " + maxPageSize });
				}
				logger.info("Changed pagesize from {} to {} on {}", event.getPreviousValue(), size, paging);
				paging.setPageSize(size);
			} catch (NumberFormatException e) {
				throw new WrongValueException(event.getTarget(), MZul.NUMBER_REQUIRED, new Object[] { event.getValue() });
			}
		}
	}
}
