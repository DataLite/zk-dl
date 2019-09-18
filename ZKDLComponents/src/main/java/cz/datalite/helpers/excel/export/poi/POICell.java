package cz.datalite.helpers.excel.export.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

/**
 *
 */
public class POICell<T> {
	private CellStyle style;
	private T value;

	public POICell(T value) {
		this.value = value;
	}

	public POICell(T value, CellStyle style) {
		this.value = value;
		this.style = style;
	}

	public CellStyle getStyle() {
		return style;
	}

	public void setStyle(CellStyle style) {
		this.style = style;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @see org.apache.poi.ss.usermodel.CellType#BLANK
	 * @see org.apache.poi.ss.usermodel.CellType#BOOLEAN
	 * @see org.apache.poi.ss.usermodel.CellType#ERROR
	 * @see org.apache.poi.ss.usermodel.CellType#FORMULA
	 * @see org.apache.poi.ss.usermodel.CellType#NUMERIC
	 * @see org.apache.poi.ss.usermodel.CellType#STRING
	 */
	public CellType getType() {
		if (value == null) {
			return CellType.BLANK;
		}
		if (value.getClass().isAssignableFrom(Boolean.class)) {
			return CellType.BOOLEAN;
		}
		if (value.getClass().isAssignableFrom(Double.class)) {// Numeric?
			return CellType.NUMERIC;
		}

		return CellType.STRING;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("POICell{");
		sb.append("value=").append(value);
		sb.append(", style=").append(style);
		sb.append('}');
		return sb.toString();
	}
}
