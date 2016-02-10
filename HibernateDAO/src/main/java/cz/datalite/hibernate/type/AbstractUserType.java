package cz.datalite.hibernate.type;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Basic methods of user type as immutable and default equals and hascode.
 *
 * @author Jiri Bubnik
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractUserType implements UserType {

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }


}
