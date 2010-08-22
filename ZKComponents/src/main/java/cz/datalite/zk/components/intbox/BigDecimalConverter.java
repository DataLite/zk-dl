package cz.datalite.zk.components.intbox;

import java.math.BigDecimal;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * Convertor for BigDecimal values. It can be used to create bigdecimalbox component and set this convertor in lang-addon.xml.
 *
 * @author Jiri Bubnik
 */
public class BigDecimalConverter implements TypeConverter, java.io.Serializable {

    /**
     * It returns the value directly.
     */
    public Object coerceToUi(Object val, Component comp) {
        return val;
    }

    /**
     * Converts value to BigDecimal and returns it.
     */
    public Object coerceToBean(Object val, Component comp) {
        if (val instanceof Integer)
        {
            return new BigDecimal((Integer)val);
        }
        else
        {
            return val;
        }
    }
}
