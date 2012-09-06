package cz.datalite.dao.impl;

import cz.datalite.dao.GenericDAO;
import java.io.Serializable;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

/**
 * Factory methods to instantiate generic DAO instances.
 *
 * @author Jiri Bubnik
 */
public class GenericDAOFactory {

    /**
     * Create new DAO with generic methods on interface and setup it with entity manager factory.
     *
     * @param <T> entity class
     * @param <ID> ID class
     * @param <DAO> DAO interface class
     * @param applicationContext context to find entity manager factory bean (either by name 'entityManagerFactory'
     *        or by class 'javax.persistence.PersistenceManagerFactory'
     * @param entityClass entity class
     * @param idClass ID class
     * @param daoClass DAO interface class
     * @return initialized DAO of appropriate type.
     */
    public static <T, ID extends Serializable, DAO extends GenericDAO<T, ID>>
            DAO createDAO(ApplicationContext applicationContext, Class<T> entityClass, Class<ID> idClass, Class<DAO> daoClass) {

        final EntityManagerFactory entityManagerFactory = getEntityManagerFactory(applicationContext, daoClass);

        GenericDAOImpl impl = new GenericDAOImpl<T, ID>(entityClass) {

            // we need to return transaction managed entity manager
            @Override
            public EntityManager getEntityManager() {
                return EntityManagerFactoryUtils.doGetTransactionalEntityManager(entityManagerFactory, null);
            }
        };
        

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(impl);
        result.setInterfaces(new Class[]{daoClass});
        return (DAO) result.getProxy();
    }

    /**
     * Find bean with name entityManagerFactory or of type EntityManagerFactory to  setup DAO with entityManager from this factory.
     *
     * @param applicationContext context to find the bean
     * @param daoClass only for error message
     * @return EntityManagerFactory
     */
    private static EntityManagerFactory getEntityManagerFactory(ApplicationContext applicationContext, Class daoClass) {
        EntityManagerFactory entityManagerFactory = null;
        
        try
        {
            entityManagerFactory = (EntityManagerFactory) applicationContext.getBean("entityManagerFactory");
        }
        catch (NoSuchBeanDefinitionException e) {}

        if (entityManagerFactory == null) {
            Map<String, EntityManagerFactory> beans = applicationContext.getBeansOfType(EntityManagerFactory.class);
            if (beans.isEmpty()) {
                throw new InstantiationError("Create default DAO for class '" + daoClass.getName() + "': " + 
                        "No bean with name of 'entityManagerFactory' or type of 'javax.persistence.EntityManager' found.");
            } else if (beans.size() > 1) {
                StringBuffer keys = new StringBuffer();
                for (String bean : beans.keySet()) {
                    keys.append(bean);
                    keys.append(",");
                }
                throw new InstantiationError("Create default DAO for class '" +
                        daoClass.getName() + "': " +
                        "No bean with name of 'entityManagerFactory' and multiple beans of type 'javax.persistence.EntityManagerFactory' found: " +
                        "' [" + keys.toString() + "]");
            } else {
                entityManagerFactory = beans.values().iterator().next();
            }
        }

        return entityManagerFactory;
    }
}
