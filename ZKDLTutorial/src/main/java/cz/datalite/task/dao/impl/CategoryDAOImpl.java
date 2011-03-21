/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.dao.impl;

import cz.datalite.dao.impl.GenericDAOImpl;
import cz.datalite.stereotype.DAO;
import cz.datalite.task.dao.CategoryDAO;
import cz.datalite.task.model.Category;

/**
 * JPA / Hibernate implementation of DAO for entity Category.
 *
 * @author Jiri Bubnik
 * @see cz.datalite.task.model.Category
 */
@DAO
public class CategoryDAOImpl extends GenericDAOImpl<Category, Long> implements CategoryDAO {

}
