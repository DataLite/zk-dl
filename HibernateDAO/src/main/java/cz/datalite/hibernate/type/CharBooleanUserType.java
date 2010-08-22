package cz.datalite.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;


/**
 * Custom type to save boolean property in database as A/N CHAR values. It recoginze (A, Y, a, y) as true, (N, n) as false, error otherwise.
 *
 * @author Jiri Bubnik
 */
public class CharBooleanUserType extends AbstractUserType{

    public int[] sqlTypes() {
        return new int[] { Hibernate.CHARACTER.sqlType() };
    }

    public Class returnedClass() {
        return Boolean.class;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String stringValue = rs.getString(names[0]);

        if (stringValue == null || stringValue.length() == 0 || rs.wasNull())
            return null;

        char charValue = stringValue.charAt(0);
        if (charValue == 'Y' || charValue == 'y' || charValue == 'A' || charValue == 'a')
            return new Boolean(true);
        else if (charValue == 'N' || charValue == 'n')
            return new Boolean(false);
        else
            throw new SQLException("Column [" + names[0] + "] contains character [" + charValue + "]. Allowed values are A, a, Y, y, N, n");
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null)
            st.setNull(index, Hibernate.CHARACTER.sqlType());
        else
            st.setString(index, (Boolean)value ? "A" : "N");
    }
}
