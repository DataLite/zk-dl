package cz.datalite.dao.support;

import java.io.Serializable;

/**
 * Base class for implementations of {@link JpaEntityInformation}. Considers an entity to be new whenever
 * {@link #getId(Object)} returns {@literal null}.
 *
 * @author Oliver Gierke
 * @author Jiri Bubnik
 */
public abstract class AbstractEntityInformation<T, ID extends Serializable> implements JpaEntityInformation<T, ID> {

    private final Class<T> domainClass;

    /**
     * Creates a new {@link AbstractEntityInformation} from the given domain class.
     *
     * @param domainClass
     */
    public AbstractEntityInformation(Class<T> domainClass) {

        this.domainClass = domainClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.IsNewAware#isNew(java.lang
     * .Object)
     */
    public boolean isNew(T entity) {

        return getId(entity) == null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.EntityInformation#getJavaType
     * ()
     */
    public Class<T> getJavaType() {

        return this.domainClass;
    }
}
