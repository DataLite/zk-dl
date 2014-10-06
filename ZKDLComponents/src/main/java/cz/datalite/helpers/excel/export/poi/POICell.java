package cz.datalite.helpers.excel.export.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

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
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_BLANK
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_BOOLEAN
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_ERROR
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_FORMULA
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_NUMERIC
	 * @see org.apache.poi.ss.usermodel.Cell#CELL_TYPE_STRING
	 */
	public int getType() {
		if (value == null) {
			return Cell.CELL_TYPE_BLANK;
		}
		if (value.getClass().isAssignableFrom(Boolean.class)) {
			return Cell.CELL_TYPE_BOOLEAN;
		}
		if (value.getClass().isAssignableFrom(Double.class)) {// Numeric?
			return Cell.CELL_TYPE_NUMERIC;
		}

		return Cell.CELL_TYPE_STRING;
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
