/**
 * Copyright 26.2.11 (c) DataLite, spol. s r.o. All rights reserved.
 * Web: http://www.datalite.cz    Mail: info@datalite.cz
 */
package cz.datalite.zk.liferay.mock;

import cz.datalite.zk.liferay.DLLiferayService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory method - a service can has seperate implementation to support mocked mode. This factory method will select
 * appropriate implementation according to the mode - if Liferay is running, liferayClass is selected, mocked class othrerwise.
 * The implementation is really simple - check if isLiferayRunning() and create new instance via Class.newInstance().
 * <p/>
 * Example:
 *     <bean id="documentService" class="cz.datalite.zk.liferay.mock.SpringSelectMockFactoryBean">
 *        <property name="dlLiferayService" ref="dlLiferayService"/>
 *        <property name="liferayClass" value="cz.datalite.obec.doc.service.impl.SouborServiceLiferayImpl"/>
 *        <property name="mockClass" value="cz.datalite.obec.doc.service.impl.SouborServiceDemoImpl"/>
 *    </bean>
 *
 */
public class SpringSelectMockFactoryBean implements FactoryBean, ApplicationContextAware {

    /**
     * The main service - it is used to check if Liferay is running
     */
    protected DLLiferayService dlLiferayService;

    /**
     * class with implementation for Liferay API
     */
    protected Class liferayClass;

    /**
     * class for mocked mode (some simle demo implementation.
     */
    protected Class mockClass;

    /**
     * The application context to enable autowiring
     */
    protected ApplicationContext applicationContext;

    /**
     * Create new instance and autowire fields..
     */
    public Object getObject() throws Exception {
        final Object instance = doCreateInstance();
           if(instance != null){
               applicationContext
                 .getAutowireCapableBeanFactory()
                 .autowireBean(instance);
           }
           return instance;
    }

    /**
     * Create new instance as a simple newInstance method.
     * Depending on if liferay is actually runnning returns liferayClass instance or mockClass instance
     *
     * @return new object instance
     */
    protected Object doCreateInstance() throws Exception {
        if (dlLiferayService.isLiferayRunning()) {
            return liferayClass.newInstance();
        } else {
            return mockClass.newInstance();
        }
    }

    /**
     * Depending on if liferay is actually runnning returns liferayClass or mockClass
     */
    public Class<?> getObjectType() {

        // the type is not known before properties are set
        if (dlLiferayService == null) {
            return null;
        }

        if (dlLiferayService.isLiferayRunning()) {
            return liferayClass;
        } else {
            return mockClass;
        }
    }

    /**
     * Always true (for Liferay)
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * The main service - it is used to check if Liferay is running
     */
    public void setDlLiferayService(DLLiferayService dlLiferayService) {
        this.dlLiferayService = dlLiferayService;
    }

    /**
     * class with implementation for Liferay API
     */
    public void setLiferayClass(Class liferayClass) {
        this.liferayClass = liferayClass;
    }

    /**
     * class for mocked mode (some simle demo implementation.
     */
    public void setMockClass(Class mockClass) {
        this.mockClass = mockClass;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
