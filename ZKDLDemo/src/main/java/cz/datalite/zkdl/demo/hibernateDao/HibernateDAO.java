package cz.datalite.zkdl.demo.hibernateDao;

import cz.datalite.dao.impl.GenericDAOImpl;
import java.util.Calendar;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * Empty implementation - all basic operation for search & count is already in GenericDAOImpl.
 *
 * The GenericDAOImpl is only a thin layer on Hibernate and Criteria API especially, check the implementation in the class.
 *
 * This is only a simple example - it is very loosly coupled, but we uses SpringUtil to get entity manager via spring- usually you will use
 * some framework - either EJB or Spring directly. Btw. GenericDAOImpl already contains @PersistenceContext annotation, so for normal use
 * you don't need to do anyting.
 *
 * @author Jiri Bubnik
 */
public class HibernateDAO extends GenericDAOImpl<HibernateTodo, Long>
{
    @Override
    public EntityManager getEntityManager()
    {
        // lookup the entity manager from OpenEntityInView spring filter
        // this is dirty code - just to get some entity manager and not to mind how to open and close it
        EntityManagerFactory emf =  (EntityManagerFactory) SpringUtil.getBean("entityManagerFactory");
        EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        return holder.getEntityManager();
    }


    /**
     * Just setup some data if the table is still empty
     */ 
    public void setupExampleData()
    {
        // check that the table is still empty
        if (!findAll().isEmpty())
            return;

        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        makePersistent(new HibernateTodo("Check out ZK-DL", "Download maven artifact and start today!", today.getTime()));
        makePersistent(new HibernateTodo("Look at ZK-DL Composer", "You can read your controller code like a story with these nice annotations", today.getTime()));
        makePersistent(new HibernateTodo("Get rid of getters/setters", "DLComposer has many more use", tomorrow.getTime()));
        makePersistent(new HibernateTodo("Ease of development - Maven", "You can start your own demo project with a  simple maven artifact.", tomorrow.getTime()));
        makePersistent(new HibernateTodo("Ease of development - Jetty", "Just write mvn jetty:run in a commmand line and launch our demo in 5 seconds.", tomorrow.getTime()));
        makePersistent(new HibernateTodo("Ease of development - JRebel", "No need to restart the server, just install JRebel - save source & refresh in the browser.", tomorrow.getTime()));
        makePersistent(new HibernateTodo("Test ZK with Selenium 2.0 (WebDriver)", "While still in beta, this testing style is very promissing", tomorrow.getTime()));


    }
}
