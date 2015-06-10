package cz.datalite.hibernate;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

/**
 * Extends {@link org.hibernate.criterion.Order} to allow ordering by an SQL formula passed by the user.
 * Is simply appends the {@code sqlFormula} passed by the user to the resulting SQL query, without any verification.
 * @author Sorin Postelnicu, http://blog.tremend.ro/2008/06/10/how-to-order-by-a-custom-sql-formulaexpression-when-using-hibernate-criteria-api/
 * @since Jun 10, 2008
 *
 */
public class OrderBySqlFormula extends Order {
    private String sqlFormula;

    /**
     * Constructor for Order.
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     */
    protected OrderBySqlFormula(String sqlFormula) {
        super(sqlFormula, true);
        this.sqlFormula = sqlFormula;
    }

    public String toString() {
        return sqlFormula;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return sqlFormula;
    }

    /**
     * Custom order
     *
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     * @return Order
     */
    public static Order sqlFormula(String sqlFormula) {
        return new OrderBySqlFormula(sqlFormula);
    }
}
