package cz.datalite.zk.components;

import org.zkoss.lang.Objects;
import org.zkoss.zul.impl.LabelElement;

public class DLLabeledTag extends LabelElement {
	private static final long serialVersionUID = 1L;

	public static final String ON_REMOVE = "onRemove";

	private String value;
	private Object data;

	static {
		addClientEvent(DLLabeledTag.class, ON_REMOVE, CE_IMPORTANT|CE_NON_DEFERRABLE);
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		renderer.render("value", getValue());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (!Objects.equals(this.value, value)) {
			this.value = value;
			smartUpdate("value", this.value);
		}
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		if (!Objects.equals(this.data, data)) {
			this.data = data;
			smartUpdate("data", this.data);
		}
	}
}
