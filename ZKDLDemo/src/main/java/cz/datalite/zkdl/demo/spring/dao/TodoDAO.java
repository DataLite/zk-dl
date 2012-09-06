package cz.datalite.zkdl.demo.spring.dao;

import cz.datalite.dao.GenericDAO;
import cz.datalite.zkdl.demo.spring.model.Todo;


/**
 * Interface of the DAO layer for Todo entity.
 *
 * @author Jiri Bubnik
 * @see Todo
 * @see cz.datalite.zkdl.demo.spring.dao.impl.TodoDAOImpl
 */
public interface TodoDAO extends GenericDAO<Todo, Long>
{

}