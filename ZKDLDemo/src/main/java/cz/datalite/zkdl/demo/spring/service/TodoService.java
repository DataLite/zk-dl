package cz.datalite.zkdl.demo.spring.service;

import cz.datalite.zkdl.demo.spring.model.Todo;
import cz.datalite.service.GenericService;


/**
 * Generic service for Todo entity.
 *
 * @author Jiri Bubnik
 * @see Todo
 * @see cz.datalite.zkdl.demo.spring.impl.TodoServiceImpl
 */
public interface TodoService extends GenericService<Todo, Long>
{

}