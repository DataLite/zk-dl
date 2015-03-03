package cz.datalite.dao.impl;

import cz.datalite.dao.DLNullPrecedence;
import cz.datalite.hibernate.OrderBySqlFormula;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

/**
 * HACK - Hibernate supports NullPrecedence query from version 4.2, but we need to support 4.1.7
 */
public class OrderNullPrecedence extends Order {

    DLNullPrecedence nullPrecedence;

    /**
     * Constructor for Order.
     *
     * @param propertyName
     * @param ascending
     */
    protected OrderNullPrecedence(String propertyName, boolean ascending, DLNullPrecedence nullPrecedence) {
        super(propertyName, ascending);
        this.nullPrecedence = nullPrecedence;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String sql = super.toSqlString(criteria, criteriaQuery);

        if (nullPrecedence != null) {
            switch (nullPrecedence) {
                case FIRST:
                    sql += " NULLS FIRST ";
                    break;
                case LAST:
                    sql += " NULLS LAST ";
                    break;
                default:
            }
        }

        return sql;
    }
}
