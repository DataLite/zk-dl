/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.service.impl;

import cz.datalite.service.impl.GenericServiceImpl;
import cz.datalite.stereotype.Service;
import cz.datalite.task.dao.TaskDAO;
import cz.datalite.task.model.Task;
import cz.datalite.task.service.TaskService;


/**
 * Generic Service implementation for JPA / Hibernate backend for entity Task.
 *
 * @author Jiri Bubnik
 * @see cz.datalite.task.model.Task
 */
@Service
public class TaskServiceImpl extends GenericServiceImpl<Task, Long, TaskDAO> implements TaskService {

}
