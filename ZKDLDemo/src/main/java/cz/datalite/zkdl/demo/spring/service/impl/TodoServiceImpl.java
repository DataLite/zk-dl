package cz.datalite.zkdl.demo.spring.service.impl;

import cz.datalite.stereotype.Service;
import cz.datalite.zkdl.demo.spring.dao.TodoDAO;
import cz.datalite.zkdl.demo.spring.model.Todo;
import cz.datalite.zkdl.demo.spring.service.TodoService;
import cz.datalite.service.impl.GenericServiceImpl;


/**
 * Implementation of the generic service. note types - it knows about it's DAO (it can even automaticaly create implemetnation,
 * if no Spring service bean with this type exists).
 *
 * @author Jiri Bubnik
 */
@Service
public class TodoServiceImpl extends GenericServiceImpl<Todo, Long, TodoDAO> implements TodoService
{

}