package cz.datalite.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import cz.datalite.dao.GenericDAO;
import cz.datalite.dao.impl.GenericDAOFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Resolve @Autowired GenericDAO&lt;Entity, Id&gt;
 *
 * This will suggest single bean DAO matching required entity class. First all GenericDAO beans are found by type
 * and persistenceClass property is compared to required entity class. If matched, this bean is returned.
 * If no eligible entity is found, new bean instance is created using GenericDAOFactory.
 */
public class GenericAutowiredCandidateResolver extends QualifierAnnotationAutowireCandidateResolver
        implements ApplicationContextAware, BeanFactoryPostProcessor {

    // application context (used to create new DAO instance)
    ApplicationContext context;
    // bean factory to resolve beans
    ListableBeanFactory beanFactory;

    /**
     * Main method to resolve correct DAO bean.
     * If the bean is not of type GenericDAO, default behaviour is assumed (current implementation returns null).
     *
     * AutowireCandidateResolver.isAutowireCandidate() should never be called for GenericDAO -
     *  all is resolved in this method and correct bean should always be returned.
     *
     * @param descriptor descriptor of required type
     * @return GenericDAO bean or default behaviour for other types.
     */
    @Override
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        if (GenericDAO.class.equals(descriptor.getDependencyType())) {
            Class entityClass = resolveEnityClass(descriptor);
            Map<String, GenericDAO> candidates = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    this.beanFactory, GenericDAO.class);

            for (GenericDAO candidate : candidates.values()) {
               if (isGenericDAOOfType(candidate, entityClass)) {
                   return candidate;
               }
            }

            return createDAO(descriptor);
        } else {
            return super.getSuggestedValue(descriptor);
        }
    }

    protected GenericDAO createDAO(DependencyDescriptor descriptor) {
        // declaration GenericDAO<Entity, Long>
        Class entityClass = resolveEnityClass(descriptor);
        Class idClass = resolveTypeParameter(descriptor, 1);

        return GenericDAOFactory.createDAO(context, entityClass, idClass, GenericDAO.class);
    }

    private Class resolveEnityClass(DependencyDescriptor descriptor) {
        return resolveTypeParameter(descriptor, 0);
    }

    private Class resolveTypeParameter(DependencyDescriptor descriptor, int parameter) {
        if (descriptor.getField() != null) {
            return (Class) ((ParameterizedType)descriptor.getField().getGenericType()).getActualTypeArguments()[parameter];
        }
        else {
            return (Class) ((ParameterizedType)descriptor.getMethodParameter().getGenericParameterType()).getActualTypeArguments()[parameter];
        }
    }

    protected boolean isGenericDAOOfType(GenericDAO dao, Type type) {
        return dao.getPersistentClass().equals(type);
    }

    /**
     * Get application context. ListableBeanFactory is required as bean factory.
     *
     * @param applicationContext the context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.beanFactory = (ListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * Register this class as autowire candidate resolver.
     *
     * @param beanFactory factory to use
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) beanFactory;
        bf.setAutowireCandidateResolver(this);
    }

}
