/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.service;

import cz.datalite.service.GenericService;
import cz.datalite.task.model.Task;


/**
 * Generic Service for entity Task.
 *
 * @author Jiri Bubnik
 * @see cz.datalite.task.model.Task
 * @see cz.datalite.task.service.impl.TaskServiceImpl
 */
public interface TaskService extends GenericService<Task, Long> {

}
