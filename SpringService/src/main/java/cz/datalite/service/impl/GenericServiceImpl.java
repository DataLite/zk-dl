package cz.datalite.service.impl;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.dao.GenericDAO;
import cz.datalite.dao.impl.GenericDAOFactory;
import cz.datalite.helpers.ReflectionHelper;
import cz.datalite.service.GenericService;
import cz.datalite.stereotype.Autowired;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic service to communicate with Generic DAO layer. It will resolve DAO class from generic type DAO and try to find
 * spring bean of this type. If no bean is found, this service will create new instance of DAO class and use inject it with
 * persistence context.
 *
 * @author Jiri Bubnik
 */
public class GenericServiceImpl<T, ID extends Serializable, DAO extends GenericDAO<T, ID>> implements GenericService<T,ID> {

    protected final static Logger LOGGER = LoggerFactory.getLogger(GenericServiceImpl.class);

    protected DAO defaultDAO;

    private Class <T> entityClass;
    private Class<ID> idClass;
    private Class<DAO> daoClass;

    public GenericServiceImpl() {
        List<Class<?>> types = ReflectionHelper.getTypeArguments(GenericServiceImpl.class, getClass());
        //Type[] types = ( (ParameterizedType) getClass().getGenericSuperclass() ).getActualTypeArguments();

        if (types.size() != 3)
        {
            throw new InstantiationError("Invalid number type parameters of '" + this.getClass().getName() + "'. " +
                    "All three parameters - entity type, primary key type and DAO class are mandatory.");
        }

        this.entityClass = (Class<T>) types.get(0);
        this.idClass = (Class<ID>) types.get(1);
        this.daoClass = (Class<DAO>) types.get(2);

        LOGGER.trace("Create new Service for class '{}' EntityClass = '{}', DAOClass = '{}'.", new Object[] {
                this.getClass().getCanonicalName(), this.entityClass.getCanonicalName(),this.daoClass.getCanonicalName() } );
    }
    
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        Map<String, DAO> beans = applicationContext.getBeansOfType(daoClass);

        if (beans.isEmpty() || GenericDAO.class.getName().equals(daoClass.getName()))
        {
            // autocreate generic DAO implementation according to interface
            defaultDAO = GenericDAOFactory.createDAO(applicationContext, entityClass, idClass, daoClass);
            LOGGER.trace("Default DAO created - '{}'.", defaultDAO.getClass().getCanonicalName());

//            throw new InstantiationError("Error autowiring bean '" + this.getClass().getName() + "': " +
//                    "No bean exists of DAO type '" + daoClass.getName() + "'.");
        }
        else if (beans.size() > 1)
        {
            StringBuffer keys = new StringBuffer();
            for (String bean : beans.keySet())
            {
                keys.append(bean);
                keys.append(",");
            }
            throw new InstantiationError("Error autowiring bean '" + this.getClass().getName() + "': " +
                    "Multiple bean exists of DAO type " + daoClass.getName() + "' [" + keys.toString() + "]");
        }
        else
        {
            defaultDAO = (DAO) applicationContext.getBeansOfType(daoClass).values().iterator().next();
            LOGGER.trace("Uniqueue default DAO got by bean class type '{}'.", daoClass.getCanonicalName());
        }
    }

    @Transactional(readOnly=true)
    public T get(ID id) {
        return defaultDAO.get(id);
    }
    
    @Transactional(readOnly=true)
    public T findById(ID id, boolean lock) {
        return defaultDAO.findById(id, lock);
    }

    @Transactional(readOnly=true)
    public T findById(ID id) {
        return defaultDAO.findById(id);
    }

    @Transactional(readOnly=true)
    public List<T> findAll() {
        return defaultDAO.findAll();
    }

    @Transactional(readOnly=true)
    public List<T> findByExample(T exampleInstance) {
        return defaultDAO.findByExample(exampleInstance);
    }

    @Transactional(readOnly=true)
    public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
        return defaultDAO.findByExample(exampleInstance, excludeProperty);
    }

    @Transactional(readOnly=true)
    public List<T> search(DLSearch<T> search) {
        return defaultDAO.search(search);
    }

    @Transactional(readOnly=true)
    public Integer count(DLSearch<T> search) {
        return defaultDAO.count(search);
    }

    @Transactional(readOnly=true)
    public DLResponse<T> searchAndCount(DLSearch<T> search) {
        return defaultDAO.searchAndCount(search);
    }

    @Transactional
    public void save(T entity) {
        defaultDAO.makePersistent(entity);
    }

    @Transactional
    public void delete(T entity) {
        defaultDAO.makeTransient(entity);
    }

    @Transactional
    public void attachPersistenceContext(T entity) {
        defaultDAO.reattach(entity);
    }
}
