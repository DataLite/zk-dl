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

import cz.datalite.helpers.ReflectionHelper;

import javax.persistence.IdClass;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Implementation of {@link JpaEntityInformation} that uses JPA {@link javax.persistence.metamodel.Metamodel}
 * to find the domain class' id field.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jiri Bubnik
 */
public class JpaMetamodelEntityInformation<T, ID extends Serializable> extends JpaEntityInformationSupport<T, ID> {

	private final IdMetadata<T> idMetadata;
	private final SingularAttribute<? super T, ?> versionAttribute;

	/**
	 * Creates a new {@link JpaMetamodelEntityInformation} for the given domain class and {@link javax.persistence.metamodel.Metamodel}.
	 *
	 * @param domainClass must not be {@literal null}.
	 * @param metamodel must not be {@literal null}.
	 */
	public JpaMetamodelEntityInformation(Class<T> domainClass, Metamodel metamodel) {

		super(domainClass);

		assert (metamodel != null);
		ManagedType<T> type = metamodel.managedType(domainClass);

		if (type == null) {
			throw new IllegalArgumentException("The given domain class can not be found in the given Metamodel!");
		}

		if (!(type instanceof IdentifiableType)) {
			throw new IllegalArgumentException("The given domain class does not contain an id attribute!");
		}

		this.idMetadata = new IdMetadata<T>((IdentifiableType<T>) type);
		this.versionAttribute = findVersionAttribute(type);
	}

	/**
	 * Returns the version attribute of the given {@link javax.persistence.metamodel.ManagedType} or {@literal null} if none available.
	 * 
	 * @param type must not be {@literal null}.
	 * @return
	 */
	private static <T> SingularAttribute<? super T, ?> findVersionAttribute(ManagedType<T> type) {

		Set<SingularAttribute<? super T, ?>> attributes = type.getSingularAttributes();

		for (SingularAttribute<? super T, ?> attribute : attributes) {
			if (attribute.isVersion()) {
				return attribute;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public ID getId(T entity) {

		if (idMetadata.hasSimpleId()) {
			return (ID) getFieldValue(idMetadata.getSimpleIdAttribute().getName(), entity);
		}

        Object id = null;
        try {
            id = idMetadata.getType().newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Unable to create metadata instance: " + idMetadata.getType(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to create metadata instance: " + idMetadata.getType(), e);
        }
        boolean partialIdValueFound = false;

		for (SingularAttribute<? super T, ?> attribute : idMetadata) {
			Object propertyValue = getFieldValue(attribute.getName(), entity);

			if (propertyValue != null) {
				partialIdValueFound = true;
			}

            setFieldValue(attribute.getName(), id, propertyValue);
		}

		return (ID) (partialIdValueFound ? id : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.support.EntityInformation#getIdType()
	 */
	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		return (Class<ID>) idMetadata.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.jpa.repository.support.JpaEntityMetadata#
	 * getIdAttribute()
	 */
	public SingularAttribute<? super T, ?> getIdAttribute() {
		return idMetadata.getSimpleIdAttribute();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.support.JpaEntityInformation#hasCompositeId()
	 */
	public boolean hasCompositeId() {
		return !idMetadata.hasSimpleId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.support.JpaEntityInformation#getIdAttributeNames()
	 */
	public Iterable<String> getIdAttributeNames() {

		List<String> attributeNames = new ArrayList<String>(idMetadata.attributes.size());

		for (SingularAttribute<? super T, ?> attribute : idMetadata.attributes) {
			attributeNames.add(attribute.getName());
		}

		return attributeNames;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.support.JpaEntityInformation#getCompositeIdAttributeValue(java.io.Serializable, java.lang.String)
	 */
	public Object getCompositeIdAttributeValue(Serializable id, String idAttribute) {
		assert (hasCompositeId());
		return getFieldValue(idAttribute, id);
	}


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.AbstractEntityInformation#isNew(java.lang.Object)
     */
	@Override
	public boolean isNew(T entity) {

		if (versionAttribute == null) {
			return super.isNew(entity);
		}

		return getFieldValue(versionAttribute.getName(), entity) == null;
	}

    /**
     * Field value via reflection.
     */
    private Object getFieldValue(String field, Object o) {
        try {
            return ReflectionHelper.getFieldValue(field, o);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Field " + field + " not found on entity " + o.getClass() + " instance " + o, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Field getter " + field + " not found on entity " + o.getClass() + " instance " + o, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Error while invoking field " + field + " of class " + o.getClass() + " instance " + o, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Not accessible field " + field + " on class " + o.getClass() + " instance " + o, e);
        }
    }

    /**
     * Set field value via reflection.
     */
    private void setFieldValue(String field, Object o, Object value) {
        try {
            ReflectionHelper.setFieldValue(field, o, value);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Field " + field + " not found on entity " + o.getClass() + " instance " + o + " value " + value, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Field accesor " + field + " not found on entity " + o.getClass() + " instance " + o+ " value " + value, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Error while invoking field " + field + " of class " + o.getClass() + " instance " + o+ " value " + value, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Not accessible field " + field + " on class " + o.getClass() + " instance " + o+ " value " + value, e);
        }
    }

    /**
	 * Simple value object to encapsulate id specific metadata.
	 * 
	 * @author Oliver Gierke
	 */
	private static class IdMetadata<T> implements Iterable<SingularAttribute<? super T, ?>> {

		private final IdentifiableType<T> type;
		private final Set<SingularAttribute<? super T, ?>> attributes;

		@SuppressWarnings("unchecked")
		public IdMetadata(IdentifiableType<T> source) {

			this.type = source;
			this.attributes = (Set<SingularAttribute<? super T, ?>>) (source.hasSingleIdAttribute() ? Collections
					.singleton(source.getId(source.getIdType().getJavaType())) : source.getIdClassAttributes());
		}

		public boolean hasSimpleId() {
			return attributes.size() == 1;
		}

		public Class<?> getType() {

			try {
				return type.getIdType().getJavaType();
			} catch (IllegalStateException e) {
				// see https://hibernate.onjira.com/browse/HHH-6951
				IdClass annotation = type.getJavaType().getAnnotation(IdClass.class);
				return annotation == null ? null : annotation.value();
			}
		}

		public SingularAttribute<? super T, ?> getSimpleIdAttribute() {
			return attributes.iterator().next();
		}

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<SingularAttribute<? super T, ?>> iterator() {
			return attributes.iterator();
		}
	}
}
