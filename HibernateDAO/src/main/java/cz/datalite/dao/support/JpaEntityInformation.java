/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.datalite.dao.support;

import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;

/**
 * Hibernate entity information.
 *
 * Original implementation: https://github.com/SpringSource/spring-data-jpa
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jiri Bubnik
 */
public interface JpaEntityInformation<T, ID extends Serializable>  {

    /**
     * Returns whether the given entity is considered to be new.
     *
     * @param entity must never be {@literal null}
     * @return
     */
    boolean isNew(T entity);

    /**
     * Returns the id of the given entity.
     *
     * @param entity must never be {@literal null}
     * @return
     */
    ID getId(T entity);

    /**
     * Returns the type of the id of the entity.
     *
     * @return
     */
    Class<ID> getIdType();

	/**
	 * Returns the id attribute of the entity.
	 * 
	 * @return
	 */
	SingularAttribute<? super T, ?> getIdAttribute();

	/**
	 * Returns {@literal true} if the entity has a composite id
	 * 
	 * @return
	 */
	boolean hasCompositeId();

	/**
	 * Returns the attribute names of the id attributes. If the entity has a composite id, then all id attribute names are
	 * returned. If the entity has a single id attribute then this single attribute name is returned.
	 * 
	 * @return
	 */
	Iterable<String> getIdAttributeNames();

	/**
	 * Extracts the value for the given id attribute from a composite id
	 * 
	 * @param id
	 * @param idAttribute
	 * @return
	 */
	Object getCompositeIdAttributeValue(Serializable id, String idAttribute);

	/**
	 * Returns the JPA entity name.
	 * 
	 * @return
	 */
	String getEntityName();
}
