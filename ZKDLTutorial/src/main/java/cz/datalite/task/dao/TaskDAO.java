/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.dao;

import cz.datalite.dao.GenericDAO;
import cz.datalite.task.model.Task;


/**
 * DAO interface for entity Task.
 *
 * @author Jiri Bubnik
 * @see cz.datalite.task.model.Task
 * @see cz.datalite.task.dao.impl.TaskDAOImpl
 */
public interface TaskDAO extends GenericDAO<Task, Long> {

}