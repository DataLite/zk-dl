package cz.datalite.db;

import cz.datalite.exception.PersistenceProblem;
import cz.datalite.exception.ProblemException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@DAO// TODO: bud nejak vyresit zavislosti (SpringService), nebo nechat byt - pripadne pouziti pres spring xml deklaraci
public class DatabaseConstraintDAOImpl
		implements DatabaseConstraintDAO {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConstraintDAOImpl.class);

	private static final String SELECT =
					"select c.constraint_type as \"constraintType\", c.table_name as \"tableName\", listagg(cc.column_name, ', ') within group (order by cc.position) as \"columnNames\" " +
					"from all_constraints c, all_cons_columns cc " +
					"where c.constraint_name = :cname " +
					"  and c.constraint_name=cc.constraint_name " +
					"group by c.constraint_type, c.table_name " +
					"union all " +
					"select 'U', ind.table_name, listagg(indc.column_name, ', ') within group (order by indc.column_position) " +
					"from all_indexes ind, all_ind_columns indc " +
					"where uniqueness = 'UNIQUE' " +
					"  and ind.index_name = indc.index_name " +
					"  and ind.index_name = :cname " +
					"group by ind.table_name ";


	EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Session getSession() {
		return (Session) getEntityManager().getDelegate();
	}

	@Override
	public DatabaseConstraint getConstraint(String name) {

		try {
			Query q = getSession().createSQLQuery(SELECT)
					.addScalar("constraintType", StringType.INSTANCE)
					.addScalar("tableName", StringType.INSTANCE)
					.addScalar("columnNames", StringType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(DatabaseConstraint.class))
					.setParameter("cname", name);

			@SuppressWarnings("unchecked")
			List<DatabaseConstraint> retList = (List<DatabaseConstraint>) q.list();

			if (retList.isEmpty()) {
				return null;
			}
			return retList.get(0);

		} catch (HibernateException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(e, PersistenceProblem.CONSTRAINT);
		}
	}

}
