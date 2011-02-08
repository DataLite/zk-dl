package cz.datalite.zkdl.demo.spring.dao.impl;

import cz.datalite.dao.impl.GenericDAOImpl;
import cz.datalite.stereotype.DAO;
import cz.datalite.zkdl.demo.spring.dao.TodoDAO;
import cz.datalite.zkdl.demo.spring.model.Todo;


/**
 * JPA implementation of DAO layer for entity Todo.
 *
 * @author Jiri Bubnik
 * @see Todo
 */
@DAO
public class TodoDAOImpl extends GenericDAOImpl<Todo, Long> implements TodoDAO
{

}