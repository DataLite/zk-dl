package cz.datalite.helpers;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import java.math.BigInteger;

/**
 * Utility methods for Oracle with Hiberante.
 */
public abstract class OracleHelper {

    /**
     * Returns next sequence ID.
     *
     * @param session hibernate session
     * @param sequenceName sequence name
     * @return next ID
     */
    public static long getNextSequenceId(Session session, String sequenceName) {
        Query query = session.createSQLQuery("select " + sequenceName + ".nextval as num from dual")
                        .addScalar("num", StandardBasicTypes.BIG_INTEGER);

        return ((BigInteger) query.uniqueResult()).longValue();
    }
}
