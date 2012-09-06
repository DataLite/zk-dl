/**
 * Copyright 19.3.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz 
 */
package cz.datalite.task.service.impl;

import cz.datalite.service.impl.GenericServiceImpl;
import cz.datalite.stereotype.Service;
import cz.datalite.task.dao.CategoryDAO;
import cz.datalite.task.model.Category;
import cz.datalite.task.service.CategoryService;


/**
 * Generic Service implementation for JPA / Hibernate backend for entity Category.
 *
 * @author Jiri Bubnik
 * @see cz.datalite.task.model.Category
 */
@Service
public class CategoryServiceImpl extends GenericServiceImpl<Category, Long, CategoryDAO> implements CategoryService {

}
