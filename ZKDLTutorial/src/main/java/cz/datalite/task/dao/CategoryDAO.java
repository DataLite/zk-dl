/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.dao;

import cz.datalite.dao.GenericDAO;
import cz.datalite.task.model.Category;


/**
 * DAO interface for entity Category.
 *
 * @author Jiri Bubnik
 * @see Category
 * @see cz.datalite.task.dao.impl.CategoryDAOImpl
 */
public interface CategoryDAO extends GenericDAO<Category, Long> {

}