package cz.datalite.zk.components.doublebox;

import java.math.BigDecimal;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.NumberInputElement;
import org.zkoss.zul.mesg.MZul;

/**
 * Number component for Big Decimals.
 *
 * @author Jiri Bubnik
 */
public class DLBigDecimalbox extends NumberInputElement {

    public DLBigDecimalbox() {
        setCols(11);
    }

    public DLBigDecimalbox(double value) throws WrongValueException {
        this();
        setValue(new BigDecimal(value));
    }

    public DLBigDecimalbox(int value) throws WrongValueException {
        this();
        setValue(new BigDecimal(value));
    }

    public DLBigDecimalbox(BigDecimal value) throws WrongValueException {
        this();
        setValue(value);
    }

    /**
     * ZClass same as doublebox - "z-doublebox"
     */
    public String getZclass() {
        return _zclass == null ? "z-doublebox" : _zclass;
    }

    @Override
    protected Object coerceFromString(String value) throws WrongValueException {
        final Object[] vals = toNumberOnly(value);
        final String val = (String) vals[0];
        if (val == null || val.length() == 0) {
            return null;
        }

        try {
            double v = Double.parseDouble(val);
            int divscale = vals[1] != null ? ((Integer) vals[1]).intValue() : 0;
            if (divscale > 0) {
                v /= Math.pow(10, divscale);
            }
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            throw showCustomError(
                    new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
        }

    }

    @Override
    protected String coerceToString(Object value) {
        return formatNumber(value, "0.##########");
    }

    /**
     * Check the value and set it.
     *
     * @param value the value
     * @throws WrongValueException If validation fails
     */
    public void setValue(BigDecimal value) {
        validate(value);
        setRawValue(value);
    }

    /** Returns the value (in BigDecimal), might be null unless
     * a constraint stops it.
     * @exception WrongValueException if user entered a wrong value
     */
    public BigDecimal getValue() throws WrongValueException {
        return (BigDecimal) getTargetValue();
    }
}
