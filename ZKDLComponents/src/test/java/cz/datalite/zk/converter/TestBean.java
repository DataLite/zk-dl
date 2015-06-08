package cz.datalite.zk.converter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Test bean for unit tests
 */
public class TestBean {

	private String strProp;

	private Integer intProp;

	private Date dateProp;

	private Double doubleProp;

	private BigDecimal decimalProp;

	public String getStrProp() {
		return strProp;
	}

	public void setStrProp(String strProp) {
		this.strProp = strProp;
	}

	public Integer getIntProp() {
		return intProp;
	}

	public void setIntProp(Integer intProp) {
		this.intProp = intProp;
	}

	public Date getDateProp() {
		return dateProp;
	}

	public void setDateProp(Date dateProp) {
		this.dateProp = dateProp;
	}

	public Double getDoubleProp() {
		return doubleProp;
	}

	public void setDoubleProp(Double doubleProp) {
		this.doubleProp = doubleProp;
	}

	public BigDecimal getDecimalProp() {
		return decimalProp;
	}

	public void setDecimalProp(BigDecimal decimalProp) {
		this.decimalProp = decimalProp;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TestBean{");
		sb.append("strProp='").append(strProp).append('\'');
		sb.append(", intProp=").append(intProp);
		sb.append(", dateProp=").append(dateProp);
		sb.append(", doubleProp=").append(doubleProp);
		sb.append(", decimalProp=").append(decimalProp);
		sb.append('}');
		return sb.toString();
	}
}
