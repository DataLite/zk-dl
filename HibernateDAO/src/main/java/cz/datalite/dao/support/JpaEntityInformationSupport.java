/*
 * Copyright 2011 the original author or authors.
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

import cz.datalite.helpers.StringHelper;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Metamodel;
import java.io.Serializable;

/**
 * Base class for {@link JpaEntityInformation} implementations to share common method implementations.
 * 
 * @author Oliver Gierke
 */
public abstract class JpaEntityInformationSupport<T, ID extends Serializable> extends AbstractEntityInformation<T, ID>
		implements JpaEntityInformation<T, ID> {

	/**
	 * Creates a new {@link JpaEntityInformationSupport} with the given domain class.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public JpaEntityInformationSupport(Class<T> domainClass) {

		super(domainClass);
	}

	/**
	 * Creates a {@link JpaEntityInformation} for the given domain class and {@link javax.persistence.EntityManager}.
	 * 
	 * @param domainClass must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> JpaEntityInformation<T, ?> getMetadata(Class<T> domainClass, EntityManager em) {

		assert (domainClass != null);
		assert (em != null);

		Metamodel metamodel = em.getMetamodel();

	    return new JpaMetamodelEntityInformation(domainClass, metamodel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.support.JpaEntityInformation#
	 * getEntityName()
	 */
	public String getEntityName() {

		Class<?> domainClass = getJavaType();
		Entity entity = domainClass.getAnnotation(Entity.class);
		boolean hasName = !StringHelper.isNull(entity.name());

		return hasName ? entity.name() : domainClass.getSimpleName();
	}
}
