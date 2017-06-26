package cz.datalite.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;


/**
 * Custom type to save boolean property in database as A/N CHAR values. It recoginze (A, Y, a, y) as true, (N, n) as false, error otherwise.
 *
 * @author Jiri Bubnik
 */
public class CharBooleanUserType extends AbstractUserType implements ParameterizedType {

    // setup defaultValue in case of null from database.
    // values Y,A/N
    public static final String DEFALUT_VALUE_KEY = "defaultValue";

    // defaultValue in case of null from database.
    private Boolean defaultValue = null;

    public int[] sqlTypes() {
        return new int[] { Types.CHAR };
    }

    public Class returnedClass() {
        return Boolean.class;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String stringValue = rs.getString(names[0]);

        if (stringValue == null || stringValue.length() == 0 || rs.wasNull()) {
            return defaultValue;
        }

        char charValue = stringValue.charAt(0);
        return booleanValue(names[0], charValue);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.CHAR);
        } else {
            st.setString(index, (Boolean) value ? "A" : "N");
        }
    }

    private Boolean booleanValue(String column, char charValue) throws SQLException {
        if (charValue == 'Y' || charValue == 'y' || charValue == 'A' || charValue == 'a') {
            return new Boolean(true);
        } else if (charValue == 'N' || charValue == 'n') {
            return new Boolean(false);
        } else {
            throw new SQLException("Column [" + column + "] contains character [" + charValue + "]. Allowed values are A, a, Y, y, N, n");
        }
    }

    public void setParameterValues(Properties parameters) {
        if (parameters.containsKey(DEFALUT_VALUE_KEY))
        {
            String defaultValueString = parameters.getProperty(DEFALUT_VALUE_KEY);

            if (defaultValueString == null || defaultValueString.length() != 1)
            {
                throw new IllegalArgumentException("Invalid parameter " + DEFALUT_VALUE_KEY + " for Boolean type: " + defaultValueString);
            }

            try {
                defaultValue = booleanValue(DEFALUT_VALUE_KEY, defaultValueString.charAt(0));
            } catch (SQLException e) {
                throw new IllegalArgumentException("Invalid parameter " + DEFALUT_VALUE_KEY + " for Boolean type.", e);
            }
        }
    }
}
